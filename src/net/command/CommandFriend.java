package net.command;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Server;
import net.command.chat.CommandPlayerNotFound;
import net.command.chat.CommandSendMessage;
import net.command.chat.MessageType;
import net.connection.Connection;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.Player;

public class CommandFriend extends Command {

	private static JDOStatement searchPlayer;
	private static JDOStatement removeFriendFromDB;
	private static JDOStatement loadCharacterNameFromID;
	private static JDOStatement addFriendToDB;
	
	public CommandFriend(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	@Override
	public void read() {
		byte packetId = this.connection.readByte();
		if(packetId == PacketID.FRIEND_ADD) {
			String name = this.connection.readString();
			name = name.substring(0, 1).toUpperCase()+name.substring(1).toLowerCase();
			Player player = Server.getInGameCharacter(name);
			int character_id = 0;
			if(player == null) { //player is offline or doesn't exist
				character_id = checkPlayerInDB(name); //player is offline
			}
			if(player != null || character_id != 0) {
				if(!name.equals(this.player.getName())) {
					if((player != null && !this.player.isFriendWith(player)) || (character_id != 0 && !this.player.isFriendWith(character_id))) {
						if(player != null) {
							if(this.player.addFriend(player.getCharacterId())) {
								addOnlineFriend(this.player, player);
								addFriendInDB(this.player, player.getCharacterId());
							}
							else {
								CommandSendMessage.write(this.connection, "Your friendlist is full.", MessageType.SELF);
							}
						}
						else if(character_id != 0) {
							if(this.player.addFriend(character_id)) {
								addOfflineFriend(this.connection, character_id, name);
								addFriendInDB(this.player, character_id);
							}
							else {
								CommandSendMessage.write(this.connection, "Your friendlist is full.", MessageType.SELF);
							}
						}
					}
					else {
						CommandSendMessage.write(this.connection, name+" is already in your friendlist.", MessageType.SELF);
					}
				}
				else {
					CommandSendMessage.write(this.connection, "You can't add yourself as friend.", MessageType.SELF);
				}
			}
			else {
				CommandPlayerNotFound.write(this.connection, name);
			}
		}
		else if(packetId == PacketID.FRIEND_REMOVE) {
			int id = this.connection.readInt();
			if(id != this.player.getCharacterId()) {
				if(this.player.removeFriend(id)) {
					System.out.println("Friend removed from list");
					removeFriendFromDB(this.player.getCharacterId(), id);
				}
				else {
					CommandSendMessage.write(this.connection, "This player is not in your friendlist.", MessageType.SELF);
				}
			}
			else {
				CommandSendMessage.write(this.connection, "You can't delete yourself.", MessageType.SELF);
			}
		}
	}
	
	private void addFriendInDB(Player player, int friend_id) {
		try {
			if(addFriendToDB == null) {
				addFriendToDB = Server.getJDO().prepare("INSERT INTO friend (character_id, friend_id) VALUES (?, ?)");
			}
			addFriendToDB.clear();
			addFriendToDB.putInt(player.getCharacterId());
			addFriendToDB.putInt(friend_id);
			addFriendToDB.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadFriendList(Player player) { //id, name, level, race, classe
		int i = 0;
		player.getConnection().writeByte(PacketID.FRIEND);
		player.getConnection().writeByte(PacketID.FRIEND_LOAD_ALL);
		player.getConnection().writeInt(player.getFriendList().size());
		while(i < player.getFriendList().size()) {
			player.getConnection().writeInt(player.getFriendList().get(i));
			player.getConnection().writeBoolean(Server.getInGamePlayerList().containsKey(player.getFriendList().get(i)));
			if(!Server.getInGamePlayerList().containsKey(player.getFriendList().get(i))) {
				player.getConnection().writeString(loadCharacterNameFromID(player.getFriendList().get(i)));
			}
			else {
				player.getConnection().writeString(Server.getInGameCharacter(player.getFriendList().get(i)).getName());
				player.getConnection().writeInt(Server.getInGameCharacter(player.getFriendList().get(i)).getLevel());
				player.getConnection().writeChar(Server.getInGameCharacter(player.getFriendList().get(i)).getRace().getValue());
				player.getConnection().writeChar(Server.getInGameCharacter(player.getFriendList().get(i)).getClasse().getValue());
			}
			i++;
		}
		player.getConnection().send();
	}
	
	public static void notifyFriendOffline(Player player, Player friend) {
		player.getConnection().writeByte(PacketID.FRIEND);
		player.getConnection().writeByte(PacketID.FRIEND_OFFLINE);
		player.getConnection().writeInt(friend.getCharacterId());
		player.getConnection().send();
	}
	
	public static void notifyFriendOnline(Player player, Player friend) {
		player.getConnection().writeByte(PacketID.FRIEND);
		player.getConnection().writeByte(PacketID.FRIEND_ONLINE);
		player.getConnection().writeInt(friend.getCharacterId());
		player.getConnection().writeString(friend.getName());
		player.getConnection().writeInt(friend.getLevel());
		player.getConnection().writeChar(friend.getRace().getValue());
		player.getConnection().writeChar(friend.getClasse().getValue());
		player.getConnection().send();
	}
	
	private static String loadCharacterNameFromID(int id) {
		try {
			if(loadCharacterNameFromID == null) {
				loadCharacterNameFromID = Server.getJDO().prepare("SELECT name FROM `character` WHERE character_id = ?");
			}
			loadCharacterNameFromID.clear();
			loadCharacterNameFromID.putInt(id);
			loadCharacterNameFromID.execute();
			if(loadCharacterNameFromID.fetch()) {
				return loadCharacterNameFromID.getString();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private static int checkPlayerInDB(String name) {
		int id = 0;
		try {
			if(searchPlayer == null) {
				searchPlayer = Server.getJDO().prepare("SELECT character_id FROM `character` WHERE name = ?");
			}
			searchPlayer.clear();
			searchPlayer.putString(name);
			searchPlayer.execute();
			if(searchPlayer.fetch()) {
				id = searchPlayer.getInt();
			}
			return id;
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return id;
	}
	
	private static void removeFriendFromDB(int character_id, int friend_id) {
		try {
			if(removeFriendFromDB == null) {
				removeFriendFromDB = Server.getJDO().prepare("DELETE FROM friend WHERE character_id = ? AND friend_id = ?");
			}
			removeFriendFromDB.clear();
			removeFriendFromDB.putInt(character_id);
			removeFriendFromDB.putInt(friend_id);
			removeFriendFromDB.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void addOnlineFriend(Player player, Player friend) {
		player.getConnection().writeByte(PacketID.FRIEND);
		player.getConnection().writeByte(PacketID.FRIEND_ADD);
		player.getConnection().writeBoolean(true);
		player.getConnection().writeInt(friend.getCharacterId());
		player.getConnection().writeString(friend.getName());
		player.getConnection().writeInt(friend.getLevel());
		player.getConnection().writeChar(friend.getRace().getValue());
		player.getConnection().writeChar(friend.getClasse().getValue());
		player.getConnection().send();
	}
	
	private static void addOfflineFriend(Connection connection, int id, String name) {
		connection.writeByte(PacketID.FRIEND);
		connection.writeByte(PacketID.FRIEND_ADD);
		connection.writeBoolean(false);
		connection.writeInt(id);
		connection.writeString(name);
		connection.send();
	}
}

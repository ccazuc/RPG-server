package net.command;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Server;
import net.command.chat.CommandPlayerNotFound;
import net.command.chat.CommandSendMessage;
import net.command.chat.MessageType;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.Player;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;

public class CommandFriend extends Command {

	private static JDOStatement searchPlayer;
	private static JDOStatement loadCharacterNameFromID;
	private final static SQLRequest addFriendInDB = new SQLRequest("INSERT INTO friend (character_id, friend_id) VALUES (?, ?)") {
		
		@Override
		public void gatherData() {
			try {
				SQLDatas datas = this.datasList.get(0);
				this.statement.clear();
				this.statement.putInt(datas.getIValue1());
				this.statement.putInt(datas.getIValue2());
				this.statement.execute();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
	private final static SQLRequest removeFriendFromDB = new SQLRequest("DELETE FROM friend WHERE character_id = ? AND friend_id = ?") {
		
		@Override
		public void gatherData() {
			try {
				SQLDatas datas = this.datasList.get(0);
				this.statement.clear();
				this.statement.putInt(datas.getIValue1());
				this.statement.putInt(datas.getIValue2());
				this.statement.execute();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
	
	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		byte packetId = connection.readByte();
		if(packetId == PacketID.FRIEND_ADD) {
			String name = connection.readString();
			if(name.length() > 2) {
				name = name.substring(0, 1).toUpperCase()+name.substring(1).toLowerCase();
				Player member = Server.getInGameCharacter(name);
				int character_id = 0;
				if(member == null) { //player is offline or doesn't exist
					character_id = checkPlayerInDB(name); //player is offline
				}
				if(member != null || character_id != 0) {
					if(!name.equals(player.getName())) {
						if((member != null && !player.isFriendWith(member)) || (character_id != 0 && !player.isFriendWith(character_id))) {
							if(member != null) {
								if(player.addFriend(member.getCharacterId())) {
									addOnlineFriend(player, member);
									addFriendInDB(player, member.getCharacterId());
								}
								else {
									CommandSendMessage.write(connection, "Your friendlist is full.", MessageType.SELF);
								}
							}
							else if(character_id != 0) {
								if(player.addFriend(character_id)) {
									addOfflineFriend(connection, character_id, name);
									addFriendInDB(player, character_id);
								}
								else {
									CommandSendMessage.write(connection, "Your friendlist is full.", MessageType.SELF);
								}
							}
						}
						else {
							CommandSendMessage.write(connection, name+" is already in your friendlist.", MessageType.SELF);
						}
					}
					else {
						CommandSendMessage.write(connection, "You can't add yourself as friend.", MessageType.SELF);
					}
				}
				else {
					CommandPlayerNotFound.write(connection, name);
				}
			}
			else {
				CommandPlayerNotFound.write(connection, name);
			}
		}
		else if(packetId == PacketID.FRIEND_REMOVE) {
			int id = connection.readInt();
			if(id != player.getCharacterId()) {
				if(player.removeFriend(id)) {
					removeFriendFromDB(player.getCharacterId(), id);
				}
				else {
					CommandSendMessage.write(connection, "This player is not in your friendlist.", MessageType.SELF);
				}
			}
			else {
				CommandSendMessage.write(connection, "You can't delete yourself.", MessageType.SELF);
			}
		}
	}
	
	private void addFriendInDB(Player player, int friend_id) {
		//addFriendInDB.setId(player.getCharacterId());
		//addFriendInDB.setId2(friend_id);
		addFriendInDB.addDatas(new SQLDatas(player.getCharacterId(), friend_id));
		Server.addNewRequest(addFriendInDB);
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
		//removeFriendFromDB.setId(character_id);
		//removeFriendFromDB.setId2(friend_id);
		removeFriendFromDB.addDatas(new SQLDatas(character_id, friend_id));
		Server.addNewRequest(removeFriendFromDB);
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

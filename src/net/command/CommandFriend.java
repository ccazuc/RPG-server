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
	
	public CommandFriend(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	@Override
	public void read() {
		byte packetId = this.connection.readByte();
		if(packetId == PacketID.FRIEND_ADD) {
			String name = this.connection.readString();
			name = name.substring(0, 1).toUpperCase()+name.substring(1).toLowerCase();
			Player player = Server.getCharacter(name); //TODO: search character in DB
			int character_id = 0;
			if(player == null) { //player is offline or doesn't exist
				character_id = checkPlayerInDB(name);
			}
			if(player != null || character_id != 0) {
				if(!name.equals(this.player.getName())) {
					if(player != null) {
						if(this.player.addFriend(player.getCharacterId())) {
							addOnlineFriend(this.player, player);
						}
						else {
							CommandSendMessage.write(this.connection, "Your friendlist is full.", MessageType.SELF);
						}
					}
					else if(character_id != 0) {
						if(this.player.addFriend(character_id)) {
							addOfflineFriend(this.connection, character_id, name);
						}
						else {
							CommandSendMessage.write(this.connection, "Your friendlist is full.", MessageType.SELF);
						}
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
			
		}
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

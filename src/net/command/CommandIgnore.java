package net.command;

import java.util.ArrayList;

import net.Server;
import net.command.chat.CommandPlayerNotFound;
import net.command.chat.CommandSendMessage;
import net.command.chat.MessageType;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.Player;
import net.game.manager.CharacterManager;
import net.game.manager.IgnoreManager;

public class CommandIgnore extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		byte packetId = connection.readByte();
		if(packetId == PacketID.IGNORE_ADD) {
			String name = connection.readString();
			if(name.length() > 2) {
				name = name.substring(0, 1).toUpperCase()+name.substring(1).toLowerCase();
				Player ignore = Server.getInGameCharacter(name);
				int character_id = 0;
				if(ignore == null) {
					character_id = CharacterManager.playerExistsInDB(name);
				}
				if(ignore != null || character_id != -1) {
					if(ignore != null && !IgnoreManager.isIgnored(player.getCharacterId(), ignore.getCharacterId())) {
						IgnoreManager.addIgnore(player.getCharacterId(), ignore.getCharacterId());
						addIgnore(connection, ignore);
					}
					else if(character_id != -1 && !IgnoreManager.isIgnored(player.getCharacterId(), character_id)) {
						IgnoreManager.addIgnore(player.getCharacterId(), character_id);
						addIgnore(connection, character_id, name);
					}
					else {
						CommandSendMessage.write(connection, name+" is already in your ignore list.", MessageType.SELF);
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
		else if(packetId == PacketID.IGNORE_REMOVE) {
			int id = connection.readInt();
			if(IgnoreManager.isIgnored(player.getCharacterId(), id)) {
				removeIgnore(connection, id);
				IgnoreManager.removeIgnore(player.getCharacterId(), id);
			}
			else {
				CommandSendMessage.write(connection, "This player is not in your ignore list.", MessageType.SELF);
			}
		}
	}
	
	public static void ignoreInit(Connection connection, Player player) {
		int i = 0;
		ArrayList<Integer> ignoreList = IgnoreManager.getIgnoreList(player.getCharacterId());
		if(ignoreList != null) {
			connection.writeByte(PacketID.IGNORE);
			connection.writeByte(PacketID.IGNORE_INIT);
			while(i < ignoreList.size()) {
				int value = ignoreList.get(i);
				connection.writeInt(value);
				Player temp = Server.getInGameCharacter(value);
				if(temp != null) {
					connection.writeString(temp.getName());
				}
				else {
					connection.writeString(CharacterManager.loadCharacterNameFromID(value));
				}
				i++;
			}
			connection.send();
		}
	}
	
	public static void removeIgnore(Connection connection, int id) {
		connection.writeByte(PacketID.IGNORE);
		connection.writeByte(PacketID.IGNORE_REMOVE);
		connection.writeInt(id);
		connection.send();
	}
	
	public static void addIgnore(Connection connection, Player ignore) {
		connection.writeByte(PacketID.IGNORE);
		connection.writeByte(PacketID.IGNORE_ADD);
		connection.writeInt(ignore.getCharacterId());
		connection.writeString(ignore.getName());
		connection.send();
	}
	
	public static void addIgnore(Connection connection, int id, String name) {
		connection.writeByte(PacketID.IGNORE);
		connection.writeByte(PacketID.IGNORE_ADD);
		connection.writeInt(id);
		connection.writeString(name);
		connection.send();
	}
}

package net.command.player;

import java.util.ArrayList;

import net.Server;
import net.command.Command;
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
		short packetId = connection.readShort();
		if(packetId == PacketID.IGNORE_ADD) {
			String name = connection.readString();
			if(!(name.length() > 2)) {
				CommandPlayerNotFound.write(connection, name);
				return;
			}
			name = name.substring(0, 1).toUpperCase()+name.substring(1).toLowerCase();
			Player ignore = Server.getInGameCharacter(name);
			int character_id = 0;
			if(ignore == null) {
				character_id = CharacterManager.playerExistsInDB(name);
			}
			if(!(ignore != null || character_id != -1)) {
				CommandPlayerNotFound.write(connection, name);
				return;
			}
			if(ignore != null && !IgnoreManager.isIgnored(player.getCharacterId(), ignore.getCharacterId())) {
				IgnoreManager.addIgnore(player.getCharacterId(), ignore.getCharacterId());
				addIgnore(connection, ignore);
			}
			else if(character_id != -1 && !IgnoreManager.isIgnored(player.getCharacterId(), character_id)) {
				IgnoreManager.addIgnore(player.getCharacterId(), character_id);
				addIgnore(connection, character_id, name);
			}
			else {
				CommandSendMessage.selfWithouthAuthor(connection, name+" is already in your ignore list.", MessageType.SELF);
			}
		}
		else if(packetId == PacketID.IGNORE_REMOVE) {
			int id = connection.readInt();
			if(!IgnoreManager.isIgnored(player.getCharacterId(), id)) {
				CommandSendMessage.selfWithouthAuthor(connection, "This player is not in your ignore list.", MessageType.SELF);
				return;
			}
			removeIgnore(connection, id);
			IgnoreManager.removeIgnore(player.getCharacterId(), id);
		}
	}
	
	public static void ignoreInit(Connection connection, Player player) {
		int i = 0;
		ArrayList<Integer> ignoreList = IgnoreManager.getIgnoreList(player.getCharacterId());
		if(ignoreList == null) {
			return;
		}
		connection.writeShort(PacketID.IGNORE);
		connection.writeShort(PacketID.IGNORE_INIT);
		connection.writeInt(ignoreList.size());
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
	
	public static void removeIgnore(Connection connection, int id) {
		connection.writeShort(PacketID.IGNORE);
		connection.writeShort(PacketID.IGNORE_REMOVE);
		connection.writeInt(id);
		connection.send();
	}
	
	public static void addIgnore(Connection connection, Player ignore) {
		connection.writeShort(PacketID.IGNORE);
		connection.writeShort(PacketID.IGNORE_ADD);
		connection.writeInt(ignore.getCharacterId());
		connection.writeString(ignore.getName());
		connection.send();
	}
	
	public static void addIgnore(Connection connection, int id, String name) {
		connection.writeShort(PacketID.IGNORE);
		connection.writeShort(PacketID.IGNORE_ADD);
		connection.writeInt(id);
		connection.writeString(name);
		connection.send();
	}
}

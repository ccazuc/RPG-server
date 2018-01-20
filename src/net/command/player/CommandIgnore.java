package net.command.player;

import java.util.HashSet;

import net.Server;
import net.command.Command;
import net.command.chat.CommandPlayerNotFound;
import net.command.chat.CommandSendMessage;
import net.command.chat.MessageType;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.manager.CharacterMgr;
import net.game.manager.IgnoreMgr;
import net.game.unit.Player;
import net.utils.StringUtils;

public class CommandIgnore extends Command {

	public CommandIgnore(String name, boolean debug)
	{
		super(name, debug);
	}
	
	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		short packetId = connection.readShort();
		if(packetId == PacketID.IGNORE_ADD) {
			String name = connection.readString();
			if(!StringUtils.checkPlayerNameLength(name)) {
				CommandPlayerNotFound.write(connection, name);
				return;
			}
			name = StringUtils.formatPlayerName(name);
			if (name.equalsIgnoreCase(player.getName()))
			{
				CommandSendMessage.selfWithoutAuthor(connection, "You can't ignore yourself.", MessageType.SELF);
				return;
			}
			Player ignore = Server.getInGameCharacterByName(name);
			int character_id = 0;
			if(ignore == null) {
				character_id = CharacterMgr.loadCharacterIDFromName(name);
			}
			if(ignore == null && character_id == -1) {
				CommandPlayerNotFound.write(connection, name);
				return;
			}
			if(ignore != null && !IgnoreMgr.isIgnored(player.getUnitID(), ignore.getUnitID())) {
				IgnoreMgr.addIgnore(player.getUnitID(), ignore.getUnitID());
				addIgnore(connection, ignore);
			}
			else if(character_id != -1 && !IgnoreMgr.isIgnored(player.getUnitID(), character_id)) {
				IgnoreMgr.addIgnore(player.getUnitID(), character_id);
				addIgnore(connection, character_id, name);
			}
			else {
				CommandSendMessage.selfWithoutAuthor(connection, name.concat(" is already in your ignore list."), MessageType.SELF);
			}
		}
		else if(packetId == PacketID.IGNORE_REMOVE) {
			int id = connection.readInt();
			if(!IgnoreMgr.isIgnored(player.getUnitID(), id)) {
				CommandSendMessage.selfWithoutAuthor(connection, "This player is not in your ignore list.", MessageType.SELF);
				return;
			}
			removeIgnore(connection, id);
			IgnoreMgr.removeIgnore(player.getUnitID(), id);
		}
	}
	
	public static void ignoreInit(Connection connection, Player player) {
		HashSet<Integer> ignoreList = IgnoreMgr.getIgnoreList(player.getUnitID());
		if(ignoreList == null)
			return;
		connection.startPacket();
		connection.writeShort(PacketID.IGNORE);
		connection.writeShort(PacketID.IGNORE_INIT);
		connection.writeInt(ignoreList.size());
		Player tmp;
		for (int id : ignoreList)
		{
			connection.writeInt(id);
			if ((tmp = Server.getInGameCharacter(id)) != null)
				connection.writeString(tmp.getName());
			else
				connection.writeString(CharacterMgr.loadCharacterNameFromID(id));
				
		}
		connection.endPacket();
		connection.send();
	}
	
	public static void removeIgnore(Connection connection, int id) {
		connection.startPacket();
		connection.writeShort(PacketID.IGNORE);
		connection.writeShort(PacketID.IGNORE_REMOVE);
		connection.writeInt(id);
		connection.endPacket();
		connection.send();
	}
	
	public static void addIgnore(Connection connection, Player ignore) {
		connection.startPacket();
		connection.writeShort(PacketID.IGNORE);
		connection.writeShort(PacketID.IGNORE_ADD);
		connection.writeInt(ignore.getUnitID());
		connection.writeString(ignore.getName());
		connection.endPacket();
		connection.send();
	}
	
	public static void addIgnore(Connection connection, int id, String name) {
		connection.startPacket();
		connection.writeShort(PacketID.IGNORE);
		connection.writeShort(PacketID.IGNORE_ADD);
		connection.writeInt(id);
		connection.writeString(name);
		connection.endPacket();
		connection.send();
	}
}

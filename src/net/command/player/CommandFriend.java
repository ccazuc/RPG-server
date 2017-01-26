package net.command.player;

import net.Server;
import net.command.Command;
import net.command.chat.CommandPlayerNotFound;
import net.command.chat.CommandSendMessage;
import net.command.chat.MessageType;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.manager.CharacterMgr;
import net.game.manager.FriendMgr;
import net.game.unit.Player;
import net.utils.StringUtils;

public class CommandFriend extends Command {
	
	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		short packetId = connection.readShort();
		if(packetId == PacketID.FRIEND_ADD) {
			String name = connection.readString();
			if(!(name.length() > 2)) {
				CommandPlayerNotFound.write(connection, name);
				return;
			}
			name = StringUtils.formatPlayerName(name);
			if(!StringUtils.checkPlayerNameLength(name)) {
				CommandPlayerNotFound.write(connection, name);
				return;
			}
			Player member = Server.getInGameCharacterByName(name);
			int character_id = 0;
			if(member == null) { //player is offline or doesn't exist
				character_id = CharacterMgr.playerExistsInDB(name);
			}
			if(!(member != null || character_id != -1)) {
				CommandPlayerNotFound.write(connection, name);
				return;
			}
			if(name.equals(player.getName())) {
				CommandSendMessage.selfWithoutAuthor(connection, "You can't add yourself as friend.", MessageType.SELF);
				return;
			}
			if(!((member != null && !player.isFriendWith(member)) || (character_id != -1 && !player.isFriendWith(character_id)))) {
				CommandSendMessage.selfWithoutAuthor(connection, name.concat(" is already in your friendlist."), MessageType.SELF);
				return;
			}
			if(member != null) {
				if(player.addFriend(member.getUnitID())) {
					addOnlineFriend(player, member);
					FriendMgr.addFriendInDB(player.getUnitID(), member.getUnitID());
				}
				else {
					CommandSendMessage.selfWithoutAuthor(connection, "Your friendlist is full.", MessageType.SELF);
				}
			}
			else if(character_id != -1) {
				if(player.addFriend(character_id)) {
					addOfflineFriend(connection, character_id, name);
					FriendMgr.addFriendInDB(player.getUnitID(), character_id);
				}
				else {
					CommandSendMessage.selfWithoutAuthor(connection, "Your friendlist is full.", MessageType.SELF);
				}
			}
		}
		else if(packetId == PacketID.FRIEND_REMOVE) {
			int id = connection.readInt();
			if(id == player.getUnitID()) {
				CommandSendMessage.selfWithoutAuthor(connection, "You can't delete yourself.", MessageType.SELF);
				return;
			}
			if(!player.removeFriend(id)) {
				CommandSendMessage.selfWithoutAuthor(connection, "This player is not in your friendlist.", MessageType.SELF);
				return;
			}
			FriendMgr.removeFriendFromDB(player.getUnitID(), id);
			removeFriend(connection, id);
		}
	}
	
	public static void removeFriend(Connection connection, int id) {
		connection.startPacket();
		connection.writeShort(PacketID.FRIEND);
		connection.writeShort(PacketID.FRIEND_REMOVE);
		connection.writeInt(id);
		connection.endPacket();
		connection.send();
	}
	
	public static void loadFriendList(Player player) {
		int i = 0;
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.FRIEND);
		player.getConnection().writeShort(PacketID.FRIEND_LOAD_ALL);
		player.getConnection().writeInt(player.getFriendList().size());
		while(i < player.getFriendList().size()) {
			player.getConnection().writeInt(player.getFriendList().get(i));
			player.getConnection().writeBoolean(Server.getInGamePlayerList().containsKey(player.getFriendList().get(i)));
			if(!Server.getInGamePlayerList().containsKey(player.getFriendList().get(i))) {
				player.getConnection().writeString(CharacterMgr.loadCharacterNameFromID(player.getFriendList().get(i)));
			}
			else {
				player.getConnection().writeString(Server.getInGameCharacter(player.getFriendList().get(i)).getName());
				player.getConnection().writeInt(Server.getInGameCharacter(player.getFriendList().get(i)).getLevel());
				player.getConnection().writeByte(Server.getInGameCharacter(player.getFriendList().get(i)).getRace().getValue());
				player.getConnection().writeByte(Server.getInGameCharacter(player.getFriendList().get(i)).getClasse().getValue());
			}
			i++;
		}
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	public static void notifyFriendOffline(Player player, Player friend) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.FRIEND);
		player.getConnection().writeShort(PacketID.FRIEND_OFFLINE);
		player.getConnection().writeInt(friend.getUnitID());
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	public static void notifyFriendOnline(Player player, Player friend) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.FRIEND);
		player.getConnection().writeShort(PacketID.FRIEND_ONLINE);
		player.getConnection().writeInt(friend.getUnitID());
		player.getConnection().writeString(friend.getName());
		player.getConnection().writeInt(friend.getLevel());
		player.getConnection().writeByte(friend.getRace().getValue());
		player.getConnection().writeByte(friend.getClasse().getValue());
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	private static void addOnlineFriend(Player player, Player friend) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.FRIEND);
		player.getConnection().writeShort(PacketID.FRIEND_ADD);
		player.getConnection().writeBoolean(true);
		player.getConnection().writeInt(friend.getUnitID());
		player.getConnection().writeString(friend.getName());
		player.getConnection().writeInt(friend.getLevel());
		player.getConnection().writeByte(friend.getRace().getValue());
		player.getConnection().writeByte(friend.getClasse().getValue());
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	private static void addOfflineFriend(Connection connection, int id, String name) {
		connection.startPacket();
		connection.writeShort(PacketID.FRIEND);
		connection.writeShort(PacketID.FRIEND_ADD);
		connection.writeBoolean(false);
		connection.writeInt(id);
		connection.writeString(name);
		connection.endPacket();
		connection.send();
	}
}

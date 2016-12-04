package net.command;

import net.Server;
import net.command.chat.CommandPlayerNotFound;
import net.command.chat.CommandSendMessage;
import net.command.chat.MessageType;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.Player;
import net.game.manager.CharacterManager;
import net.game.manager.FriendManager;

public class CommandFriend extends Command {
	
	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		byte packetId = connection.readByte();
		if(packetId == PacketID.FRIEND_ADD) {
			String name = connection.readString();
			if(name.length() > 2) {
				CommandPlayerNotFound.write(connection, name);
				return;
			}
			name = name.substring(0, 1).toUpperCase()+name.substring(1).toLowerCase();
			Player member = Server.getInGameCharacter(name);
			int character_id = 0;
			if(member == null) { //player is offline or doesn't exist
				character_id = CharacterManager.playerExistsInDB(name);
			}
			if(!(member != null || character_id != -1)) {
				CommandPlayerNotFound.write(connection, name);
				return;
			}
			if(name.equals(player.getName())) {
				CommandSendMessage.write(connection, "You can't add yourself as friend.", MessageType.SELF);
				return;
			}
			if(!((member != null && !player.isFriendWith(member)) || (character_id != -1 && !player.isFriendWith(character_id)))) {
				CommandSendMessage.write(connection, name+" is already in your friendlist.", MessageType.SELF);
				return;
			}
			if(member != null) {
				if(player.addFriend(member.getCharacterId())) {
					addOnlineFriend(player, member);
					FriendManager.addFriendInDB(player.getCharacterId(), member.getCharacterId());
				}
				else {
					CommandSendMessage.write(connection, "Your friendlist is full.", MessageType.SELF);
				}
			}
			else if(character_id != -1) {
				if(player.addFriend(character_id)) {
					addOfflineFriend(connection, character_id, name);
					FriendManager.addFriendInDB(player.getCharacterId(), character_id);
				}
				else {
					CommandSendMessage.write(connection, "Your friendlist is full.", MessageType.SELF);
				}
			}
		}
		else if(packetId == PacketID.FRIEND_REMOVE) {
			int id = connection.readInt();
			if(id == player.getCharacterId()) {
				CommandSendMessage.write(connection, "You can't delete yourself.", MessageType.SELF);
				return;
			}
			if(!player.removeFriend(id)) {
				CommandSendMessage.write(connection, "This player is not in your friendlist.", MessageType.SELF);
				return;
			}
			FriendManager.removeFriendFromDB(player.getCharacterId(), id);
			removeFriend(connection, id);
		}
	}
	
	public static void removeFriend(Connection connection, int id) {
		connection.writeByte(PacketID.FRIEND);
		connection.writeByte(PacketID.FRIEND_REMOVE);
		connection.writeInt(id);
		connection.send();
	}
	
	public static void loadFriendList(Player player) {
		int i = 0;
		player.getConnection().writeByte(PacketID.FRIEND);
		player.getConnection().writeByte(PacketID.FRIEND_LOAD_ALL);
		player.getConnection().writeInt(player.getFriendList().size());
		while(i < player.getFriendList().size()) {
			player.getConnection().writeInt(player.getFriendList().get(i));
			player.getConnection().writeBoolean(Server.getInGamePlayerList().containsKey(player.getFriendList().get(i)));
			if(!Server.getInGamePlayerList().containsKey(player.getFriendList().get(i))) {
				player.getConnection().writeString(CharacterManager.loadCharacterNameFromID(player.getFriendList().get(i)));
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

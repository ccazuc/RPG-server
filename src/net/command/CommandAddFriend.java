package net.command;

import net.Server;
import net.command.chat.CommandPlayerNotFound;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.Player;

public class CommandAddFriend extends Command {
	
	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		String name = connection.readString();
		if(name.length() > 2) {
			name = name.substring(0, 1).toUpperCase()+name.substring(1).toLowerCase();
			Player member = Server.getCharacter(name);
			if(member != null) {
				player.addFriend(member.getCharacterId());
				writeAddFriend(player, member);
			}
		}
		else {
			CommandPlayerNotFound.write(connection, name);
		}
	}
	
	private static void writeAddFriend(Player player, Player friend) {
		player.getConnection().writeByte(PacketID.FRIEND);
		player.getConnection().writeByte(PacketID.FRIEND_SEND_INFO);
		player.getConnection().writeString(friend.getName());
		player.getConnection().writeInt(friend.getLevel());
		player.getConnection().writeChar(friend.getRace().getValue());
		player.getConnection().writeChar(friend.getClasse().getValue());
		player.getConnection().send();
	}
}

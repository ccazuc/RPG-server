package net.command.player;

import net.Server;
import net.command.Command;
import net.command.chat.CommandPlayerNotFound;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.unit.Player;
import net.utils.StringUtils;

public class CommandAddFriend extends Command {
	
	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		String name = connection.readString();
		if(name.length() <= 2) {
			CommandPlayerNotFound.write(connection, name);
			return;
		}
		name = StringUtils.formatPlayerName(name);
		Player member = Server.getCharacter(name);
		if(member == null) {
			CommandPlayerNotFound.write(connection, name);
			return;
		}
		player.addFriend(member.getUnitID());
		writeAddFriend(player, member);
	}
	
	private static void writeAddFriend(Player player, Player friend) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.FRIEND);
		player.getConnection().writeShort(PacketID.FRIEND_SEND_INFO);
		player.getConnection().writeString(friend.getName());
		player.getConnection().writeInt(friend.getLevel());
		player.getConnection().writeByte(friend.getRace().getValue());
		player.getConnection().writeByte(friend.getClasse().getValue());
		player.getConnection().endPacket();
		player.getConnection().send();
	}
}

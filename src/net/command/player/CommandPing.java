package net.command.player;

import net.command.Command;
import net.connection.PacketID;
import net.game.Player;

public class CommandPing extends Command {
	
	@Override
	public void read(Player player) {
		player.setPingTimer(System.currentTimeMillis());
		player.setPingStatus(true);
		write(player);
	}

	public static void write(Player player) {
		player.getConnection().writeShort(PacketID.PING);
		player.getConnection().send();
	}
}

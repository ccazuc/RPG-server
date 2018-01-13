package net.command.player;

import net.command.Command;
import net.connection.PacketID;
import net.game.unit.Player;

public class CommandPing extends Command {

	public CommandPing(String name, boolean debug)
	{
		super(name, debug);
	}
	
	@Override
	public void read(Player player) {
		player.setPingTimer(System.currentTimeMillis());
		player.setPingStatus(true);
		write(player);
	}

	public static void write(Player player) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.PING);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
}

package net.command.item;

import net.command.Command;
import net.connection.Connection;
import net.game.unit.Player;

public class CommandAuction extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		short packetId = connection.readShort();
	}
}

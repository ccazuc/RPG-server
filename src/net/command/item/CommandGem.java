package net.command.item;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.Player;
import net.game.item.gem.GemManager;;

public class CommandGem extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		int id = connection.readInt();
		if(GemManager.exists(id)) {
			connection.startPacket();
			connection.writeShort(PacketID.GEM);
			connection.writeGem(GemManager.getGem(id));
			connection.endPacket();
			connection.send();
		}
	}
}

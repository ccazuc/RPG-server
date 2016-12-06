package net.command.item;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.Player;
import net.game.item.stuff.StuffManager;

public class CommandStuff extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		int id = connection.readInt();
		if(StuffManager.exists(id)) {
			connection.writeShort(PacketID.STUFF);
			connection.writeStuff(StuffManager.getStuff(id));
			connection.send();
		}
	}
}

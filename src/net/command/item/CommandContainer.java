package net.command.item;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.item.bag.ContainerManager;
import net.game.unit.Player;

public class CommandContainer extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		int id = connection.readInt();
		if(ContainerManager.exists(id)) {
			connection.startPacket();
			connection.writeShort(PacketID.CONTAINER);
			connection.writeContainer(ContainerManager.getContainer(id));
			connection.endPacket();
			connection.send();
		}
	}
}

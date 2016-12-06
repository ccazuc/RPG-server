package net.command.item;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.Player;
import net.game.item.bag.ContainerManager;

public class CommandContainer extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		int id = connection.readInt();
		if(ContainerManager.exists(id)) {
			connection.writeShort(PacketID.CONTAINER);
			connection.writeContainer(ContainerManager.getContainer(id));
			connection.send();
		}
	}
}

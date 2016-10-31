package net.command.item;

import net.command.Command;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.item.bag.ContainerManager;

public class CommandContainer extends Command {

	public CommandContainer(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	@Override
	public void read() {
		int id = this.connection.readInt();
		if(ContainerManager.exists(id)) {
			this.connection.writeByte(PacketID.CONTAINER);
			this.connection.writeContainer(ContainerManager.getContainer(id));
			this.connection.send();
		}
	}
}

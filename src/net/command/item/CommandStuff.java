package net.command.item;

import net.command.Command;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.item.stuff.StuffManager;

public class CommandStuff extends Command {

	public CommandStuff(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	@Override
	public void read() {
		int id = this.connection.readInt();
		if(StuffManager.exists(id)) {
			this.connection.writeByte(PacketID.STUFF);
			this.connection.writeStuff(StuffManager.getStuff(id));
			this.connection.send();
		}
	}
}

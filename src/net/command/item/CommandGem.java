package net.command.item;

import net.command.Command;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.item.gem.GemManager;;

public class CommandGem extends Command {

	public CommandGem(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	@Override
	public void read() {
		int id = this.connection.readInt();
		if(GemManager.exists(id)) {
			this.connection.writeByte(PacketID.GEM);
			this.connection.writeGem(GemManager.getGem(id));
			this.connection.send();
		}
	}
}

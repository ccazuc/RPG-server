package net.command.item;

import net.command.Command;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.item.potion.PotionManager;

public class CommandPotion extends Command {

	public CommandPotion(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	@Override
	public void read() {
		int id = this.connection.readInt();
		if(PotionManager.exists(id)) {
			this.connection.writeByte(PacketID.POTION);
			this.connection.writePotion(PotionManager.getPotion(id));
			this.connection.send();
		}
	}
}

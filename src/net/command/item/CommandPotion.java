package net.command.item;

import net.command.Command;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.item.potion.Potion;
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
			this.player.addItemSentToClient(id);
		}
	}
	
	public void write(Potion potion) {
		this.connection.writeByte(PacketID.POTION);
		this.connection.writeInt(potion.getId());
		this.connection.writeString(potion.getSpriteId());
		this.connection.writeString(potion.getStuffName());
		this.connection.writeInt(potion.getLevel());
		this.connection.writeInt(potion.getPotionHeal());
		this.connection.writeInt(potion.getPotionMana());
		this.connection.writeInt(potion.getSellPrice());
		this.connection.send();
	}
}

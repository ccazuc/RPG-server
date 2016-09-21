package net.command;

import net.connection.ConnectionManager;
import net.game.item.stuff.Stuff;

public class CommandStuff extends Command {


	public CommandStuff(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	@Override
	public void read() {}
	
	public void write(Stuff stuff) {
		int i = 0;
		this.connection.writeChar(stuff.getType().getValue());
		this.connection.writeInt(stuff.getClassType().length);
		while(i < stuff.getClassType().length) {
			this.connection.writeChar(stuff.getClassType(i).getValue());
			i++;
		}
		this.connection.writeString(stuff.getSpriteId());
		this.connection.writeInt(stuff.getId());
		this.connection.writeString(stuff.getStuffName());
		this.connection.writeInt(stuff.getQuality());
		this.connection.writeChar(stuff.getGemSlot1().getValue());
		this.connection.writeChar(stuff.getGemSlot2().getValue());
		this.connection.writeChar(stuff.getGemSlot3().getValue());
		this.connection.writeChar(stuff.getGemBonusType().getValue());
		this.connection.writeInt(stuff.getGemBonusValue());
		this.connection.writeInt(stuff.getLevel());
		this.connection.writeChar(stuff.getWear().getValue());
		this.connection.writeInt(stuff.getCritical());
		this.connection.writeInt(stuff.getStrength());
		this.connection.writeInt(stuff.getStamina());
		this.connection.writeInt(stuff.getArmor());
		this.connection.writeInt(stuff.getMana());
		this.connection.writeInt(stuff.getSellPrice());
		this.connection.send();
	}
}

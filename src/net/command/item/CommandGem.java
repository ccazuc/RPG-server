package net.command.item;

import net.command.Command;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.item.gem.Gem;
import net.game.item.gem.GemManager;;

public class CommandGem extends Command {

	public CommandGem(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	@Override
	public void read() {
		int id = this.connection.readInt();
		if(GemManager.exists(id)) {
			write(GemManager.getGem(id));
		}
	}
	
	public void write(Gem gem) {
		this.connection.writeByte(PacketID.GEM);
		this.connection.writeInt(gem.getId());
		this.connection.writeString(gem.getSpriteId());
		this.connection.writeString(gem.getStuffName());
		this.connection.writeInt(gem.getQuality());
		this.connection.writeChar(gem.getColor().getValue());
		this.connection.writeInt(gem.getStrength());
		this.connection.writeInt(gem.getStamina());
		this.connection.writeInt(gem.getArmor());
		this.connection.writeInt(gem.getMana());
		this.connection.writeInt(gem.getCritical());
		this.connection.writeInt(gem.getSellPrice());
		this.connection.send();
	}
}

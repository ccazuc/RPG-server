package net.command.item;

import net.command.Command;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.item.gem.GemManager;
import net.game.item.potion.PotionManager;
import net.game.item.stuff.StuffManager;
import net.game.item.weapon.WeaponManager;

public class CommandRequestItem extends Command {
	
	public CommandRequestItem(ConnectionManager connectionManager) {
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
		else if(WeaponManager.exists(id)) {
			this.connection.writeByte(PacketID.WEAPON);
			this.connection.writeWeapon(WeaponManager.getWeapon(id));
			this.connection.send();
		}
		else if(GemManager.exists(id)) {
			this.connection.writeByte(PacketID.GEM);
			this.connection.writeGem(GemManager.getGem(id));
			this.connection.send();
		}
		else if(PotionManager.exists(id)) {
			this.connection.writeByte(PacketID.POTION);
			this.connection.writePotion(PotionManager.getPotion(id));
			this.connection.send();
		}
	}
}

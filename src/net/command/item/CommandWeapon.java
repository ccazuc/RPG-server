package net.command.item;

import net.command.Command;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.item.stuff.Stuff;
import net.game.item.weapon.WeaponManager;

public class CommandWeapon extends Command {


	public CommandWeapon(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	@Override
	public void read() {
		int id = this.connection.readInt();
		if(WeaponManager.exists(id)) {
			write(WeaponManager.getWeapon(id));
		}
	}
	
	public void write(Stuff weapon) {
		int i = 0;
		this.connection.writeByte(PacketID.WEAPON);
		this.connection.writeInt(weapon.getId());
		this.connection.writeString(weapon.getStuffName());
		this.connection.writeString(weapon.getSpriteId());
		this.connection.writeInt(weapon.getClassType().length);
		while(i < weapon.getClassType().length) {
			this.connection.writeChar(weapon.getClassType(i).getValue());
			i++;
		}
		this.connection.writeChar(weapon.getWeaponType().getValue());
		this.connection.writeChar(weapon.getWeaponSlot().getValue());
		this.connection.writeInt(weapon.getQuality());
		this.connection.writeChar(weapon.getGemSlot1().getValue());
		this.connection.writeChar(weapon.getGemSlot2().getValue());
		this.connection.writeChar(weapon.getGemSlot3().getValue());
		this.connection.writeChar(weapon.getGemBonusType().getValue());
		this.connection.writeInt(weapon.getGemBonusValue());
		this.connection.writeInt(weapon.getLevel());
		this.connection.writeInt(weapon.getArmor());
		this.connection.writeInt(weapon.getStamina());
		this.connection.writeInt(weapon.getMana());
		this.connection.writeInt(weapon.getCritical());
		this.connection.writeInt(weapon.getStrength());
		this.connection.writeInt(weapon.getSellPrice());
		this.connection.send();
	}
}
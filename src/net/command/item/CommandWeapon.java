package net.command.item;

import net.command.Command;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.item.weapon.WeaponManager;

public class CommandWeapon extends Command {

	public CommandWeapon(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	@Override
	public void read() {
		int id = this.connection.readInt();
		if(WeaponManager.exists(id)) {
			this.connection.writeByte(PacketID.WEAPON);
			this.connection.writeWeapon(WeaponManager.getWeapon(id));
			this.connection.send();
		}
	}
}
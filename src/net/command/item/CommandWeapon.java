package net.command.item;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.Player;
import net.game.item.weapon.WeaponManager;

public class CommandWeapon extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		int id = connection.readInt();
		if(WeaponManager.exists(id)) {
			connection.writeByte(PacketID.WEAPON);
			connection.writeWeapon(WeaponManager.getWeapon(id));
			connection.send();
		}
	}
}
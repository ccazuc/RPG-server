package net.command.item;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.item.weapon.WeaponManager;
import net.game.unit.Player;

public class CommandWeapon extends Command {

	public CommandWeapon(String name, boolean debug)
	{
		super(name, debug);
	}
	
	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		int id = connection.readInt();
		if(WeaponManager.exists(id)) {
			connection.startPacket();
			connection.writeShort(PacketID.WEAPON);
			connection.writeWeapon(WeaponManager.getWeapon(id));
			connection.endPacket();
			connection.send();
		}
	}
}
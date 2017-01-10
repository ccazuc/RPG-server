package net.command.player;

import net.command.Command;
import net.connection.PacketID;
import net.game.unit.Player;

public class CommandSendPlayer extends Command {

	
	public static void write(Player player) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.SEND_PLAYER);
		player.getConnection().writeByte(player.getClasse().getValue());
		player.getConnection().writeInt(player.getUnitID());
		player.getConnection().writeByte(player.getWear().getValue());
		player.getConnection().writeByte((byte)player.getWeaponType().length);
		int i = 0;
		while(i < player.getWeaponType().length) {
			player.getConnection().writeByte(player.getWeaponType()[i].getValue());
			i++;
		}
		player.getConnection().writeInt(player.getStamina());
		player.getConnection().writeInt(player.getMaxStaminaEffective());
		player.getConnection().writeInt(player.getMana());
		player.getConnection().writeInt(player.getMaxManaEffective());
		player.getConnection().writeInt(5);
		player.getConnection().writeInt(5);
		player.getConnection().writeInt(5);
		player.getConnection().endPacket();
		player.getConnection().send();
		System.out.println("PLAYER SENT");
	}
}

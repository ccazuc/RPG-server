package net.command.player;

import net.command.Command;
import net.connection.PacketID;
import net.game.unit.Player;

public class CommandSendPlayer extends Command
{

	public CommandSendPlayer(String name, boolean debug)
	{
		super(name, debug);
	}
	
	public static void write(Player player)
	{
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.SEND_PLAYER);
		player.getConnection().writeByte(player.getClasse().getValue());
		player.getConnection().writeInt(player.getUnitID());
		player.getConnection().writeString(player.getName());
		player.getConnection().writeByte(player.getWear().getValue());
		player.getConnection().writeByte((byte)player.getWeaponType().length);
		for (int i = 0; i < player.getWeaponType().length; ++i)
			player.getConnection().writeByte(player.getWeaponType()[i].getValue());
		player.getConnection().writeInt(player.getStamina());
		player.getConnection().writeInt(player.getMaxStaminaEffective());
		player.getConnection().writeInt(player.getMana());
		player.getConnection().writeInt(player.getMaxManaEffective());
		player.getConnection().writeInt(5);
		player.getConnection().writeInt(5);
		player.getConnection().writeInt(5);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
}

package net.command.player;

import net.command.Command;
import net.connection.PacketID;
import net.game.unit.Player;

public class CommandSendGCD extends Command
{
	
	public CommandSendGCD(String name, boolean debug)
	{
		super(name, debug);
	}
	
	public static void sendGCD(Player player, long startTimer, long endTimer)
	{
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.SEND_GCD);
		player.getConnection().writeLong(startTimer);
		player.getConnection().writeLong(endTimer);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
}

package net.command.player;

import net.command.Command;
import net.connection.PacketID;
import net.game.unit.Player;

public class CommandLogout extends Command
{

	public CommandLogout(String name, boolean debug)
	{
		super(name, debug);
	}
	
	@Override
	public final void read(Player player)
	{
		player.close(false);
		System.out.println("Logout received from account: " + player.getAccountName());
	}
	
	public static void loggout(Player player)
	{
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.LOGOUT);
		player.getConnection().endPacket();
		player.getConnection().send();
		//System.out.println("Logout sent");
	}
}

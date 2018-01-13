package net.command.player;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.unit.Player;

public class CommandChangeGold extends Command {

	public CommandChangeGold(String name, boolean debug)
	{
		super(name, debug);
	}
	
	@Override
	public void read(Player player)
	{
		
	}
	
	public static void updateGold(Player player)
	{
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.CHANGE_GOLD);
		connection.writeLong(player.getGold());
		connection.endPacket();
		connection.send();
	}
}

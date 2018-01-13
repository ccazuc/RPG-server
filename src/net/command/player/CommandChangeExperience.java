package net.command.player;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.unit.Player;

public class CommandChangeExperience extends Command {

	public CommandChangeExperience(String name, boolean debug)
	{
		super(name, debug);
	}
	
	@Override
	public void read(Player player)
	{
		
	}
	
	public static void updateExperience(Player player)
	{
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.CHANGE_EXPERIENCE);
		connection.writeLong(player.getExperience());
		connection.endPacket();
		connection.send();
	}
}

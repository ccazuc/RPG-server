package net.command.chat;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.unit.Player;

public class CommandPlayed extends Command {

	public CommandPlayed(String name, boolean debug)
	{
		super(name, debug);
	}
	
	@Override
	public void read(Player player)
	{
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.PLAYED);
		connection.writeLong(player.getPlayedTimer());
		connection.writeLong(player.getPlayedLevelTimer());
		connection.endPacket();
		connection.send();
	}
}

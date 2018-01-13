package net.command.chat;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.unit.Player;

public class CommandPlayerNotFound extends Command {

	public CommandPlayerNotFound(String name, boolean debug)
	{
		super(name, debug);
	}
	
	@Override
	public void read(Player player) {
		
	}
	
	public static void write(Connection connection, String name) {
		connection.startPacket();
		connection.writeShort(PacketID.PLAYER_NOT_FOUND);
		connection.writeString(name);
		connection.endPacket();
		connection.send();
	}
}

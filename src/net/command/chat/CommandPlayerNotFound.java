package net.command.chat;

import net.command.Command;
import net.connection.Connection;
import net.connection.ConnectionManager;
import net.connection.PacketID;

public class CommandPlayerNotFound extends Command {

	public CommandPlayerNotFound(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	@Override
	public void read() {
		
	}
	
	public static void write(Connection connection, String name) {
		connection.writeByte(PacketID.PLAYER_NOT_FOUND);
		connection.writeString(name);
		connection.send();
	}
}

package net.command;

import net.Server;
import net.connection.Connection;
import net.connection.ConnectionManager;
import net.connection.PacketID;

public class CommandRegisterToAuthServer extends Command {
	
	public CommandRegisterToAuthServer(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	public static void write(Connection connection) {
		connection.writeByte(PacketID.REGISTER_WORLD_SERVER);
		connection.writeString(Server.getRealmName());
		connection.writeInt(Server.getRealmId());
		connection.writeInt(Server.getPort());
		connection.send();
	}

}

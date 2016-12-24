package net.command.auth;

import net.Server;
import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;

public class CommandRegisterToAuthServer extends Command {
	
	public static void write(Connection connection) {
		connection.startPacket();
		connection.writeShort(PacketID.REGISTER_WORLD_SERVER);
		connection.writeString(Server.getRealmName());
		connection.writeInt(Server.getRealmId());
		connection.writeInt(Server.getPort());
		connection.endPacket();
		connection.send();
	}
}

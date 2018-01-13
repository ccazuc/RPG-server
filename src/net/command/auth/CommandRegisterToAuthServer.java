package net.command.auth;

import net.Server;
import net.command.Command;
import net.config.ConfigMgr;
import net.connection.Connection;
import net.connection.PacketID;

public class CommandRegisterToAuthServer extends Command {

	public CommandRegisterToAuthServer(String name, boolean debug)
	{
		super(name, debug);
	}
	
	public static void write(Connection connection) {
		connection.startPacket();
		connection.writeShort(PacketID.REGISTER_WORLD_SERVER);
		connection.writeString(Server.getRealmName());
		connection.writeInt(ConfigMgr.REALM_ID);
		connection.writeInt(ConfigMgr.PORT);
		connection.endPacket();
		connection.send();
	}
}

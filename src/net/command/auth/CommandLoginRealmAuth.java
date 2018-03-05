package net.command.auth;

import net.Server;
import net.command.Command;
import net.connection.Connection;
import net.connection.Key;
import net.connection.PacketID;

public class CommandLoginRealmAuth extends Command {

	public CommandLoginRealmAuth(String name, boolean debug)
	{
		super(name, debug);
	}
	
	@Override
	public void read(Connection connection) {
		short packetId = connection.readShort();
		if(packetId == PacketID.LOGIN_NEW_KEY) {
			double key = connection.readDouble();
			int account_id = connection.readInt();
			int account_rank = connection.readInt();
			String account_name = connection.readString();
			String ipAdresse = connection.readString();
			Server.addKey(new Key(account_id, account_rank, account_name, key, ipAdresse, Server.getLoopTickTimer()));
		}
	}
}

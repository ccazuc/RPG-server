package net.command;

import net.Server;
import net.connection.Connection;
import net.connection.Key;
import net.connection.PacketID;

public class CommandLoginRealmAuth extends Command {
	
	@Override
	public void read(Connection connection) {
		byte packetId = connection.readByte();
		if(packetId == PacketID.LOGIN_NEW_KEY) {
			double key = connection.readDouble();
			int account_id = connection.readInt();
			int account_rank = connection.readInt();
			Server.addKey(new Key(account_id, account_rank, key));
		}
	}
}

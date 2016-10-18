package net.command;

import net.Server;
import net.connection.Connection;
import net.connection.ConnectionManager;
import net.connection.Key;
import net.connection.PacketID;

public class CommandLoginRealm extends Command {
	
	public CommandLoginRealm(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	public CommandLoginRealm(Connection connection) {
		super(connection);
	}
	
	@Override
	public void read() {
		byte packetId = this.connection.readByte();
		if(packetId == PacketID.LOGIN_NEW_KEY) {
			double key = this.connection.readDouble();
			int account_id = this.connection.readInt();
			Server.addKey(new Key(account_id, key));
			System.out.println("LOGINREALM:LOGIN_NEW_KEY");
		}
		else if(packetId == PacketID.LOGIN_REALM_REQUEST) {
			double key = this.connection.readDouble();
			int account_id = this.connection.readInt();
			if(Server.hasKey(key, account_id)) {
				connectionAccepted(this.connection);
				Server.removeKey(key);
			}
			else {
				this.player.close();
			}
		}
	}
	
	private static void connectionAccepted(Connection connection) {
		connection.writeByte(PacketID.LOGIN_REALM);
		connection.writeByte(PacketID.LOGIN_REALM_SUCCESS);
		connection.send();
	}
}

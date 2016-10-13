package net.command;

import net.connection.ConnectionManager;
import net.connection.PacketID;

public class CommandUpdateStats extends Command {

	public CommandUpdateStats(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	@Override
	public void write(byte packetID, int id, int value) {
		this.connection.writeByte(PacketID.UPDATE_STATS);
		this.connection.writeByte(packetID);
		this.connection.writeInt(id);
		this.connection.writeInt(value);
		this.connection.send();
	}
}

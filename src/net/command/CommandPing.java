package net.command;

import net.connection.ConnectionManager;
import net.connection.PacketID;

public class CommandPing extends Command {
	

	public CommandPing(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	@Override
	public void read() {
		this.player.setPingTimer(System.currentTimeMillis());
		this.player.setPingStatus(true);
		write();
	}
	
	public void write() {
		this.connection.writeByte(PacketID.PING);
		this.connection.send();
	}
}

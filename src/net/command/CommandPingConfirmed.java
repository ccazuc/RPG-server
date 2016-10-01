package net.command;

import net.connection.ConnectionManager;

public class CommandPingConfirmed extends Command {

	public CommandPingConfirmed(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	@Override
	public void read() {
		this.player.setPingStatus(false);
	}
}

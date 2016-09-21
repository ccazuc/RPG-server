package net.command;

import net.connection.ConnectionManager;

public class CommandLogout extends Command {
	
	public CommandLogout(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	@Override
	public final void read() {
		this.player.close();
	}
}

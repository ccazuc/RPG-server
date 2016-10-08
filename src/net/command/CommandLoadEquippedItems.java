package net.command;

import net.connection.ConnectionManager;

public class CommandLoadEquippedItems extends Command {
	
	public CommandLoadEquippedItems(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	@Override
	public void read() {}
	
	@Override
	public void write() {}
}

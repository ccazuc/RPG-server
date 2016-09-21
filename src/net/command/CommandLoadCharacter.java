package net.command;

import net.connection.ConnectionManager;

public class CommandLoadCharacter extends Command {
	
	public CommandLoadCharacter(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	@Override
	public void read() {
		this.player.loadEquippedItemSQL();
		this.player.loadEquippedBagSQL();
	}
	
	public void write() {
		
	}
}

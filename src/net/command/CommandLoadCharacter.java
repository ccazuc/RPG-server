package net.command;

import net.connection.ConnectionManager;

public class CommandLoadCharacter extends Command {
	
	public CommandLoadCharacter(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	@Override
	public void read() {
		int id = this.connection.readInt();
		this.player.setCharacterId(id);
		this.player.loadEquippedItemSQL();
		this.player.loadBagItemSQL();
		//this.player.loadEquippedBagSQL();
	}
	
	public void write() {
		
	}
}

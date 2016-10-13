package net.command;

import net.connection.ConnectionManager;

public class CommandSpellCast extends Command {

	public CommandSpellCast(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	@Override
	public void read() {
		int spellId = this.connection.readInt();
		if(this.player.getSpellUnlocked().containsKey((spellId))) {
			this.player.getSpellUnlocked(spellId).action(this.player, this.player.getTarget());
		}
	}
}

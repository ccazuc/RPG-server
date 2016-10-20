package net.command;

import net.Server;
import net.connection.ConnectionManager;

public class CommandLoadCharacter extends Command {
	
	public CommandLoadCharacter(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	@Override
	public void read() {
		int id = this.connection.readInt();
		this.player.setCharacterId(id);
		this.player.initTable();
		this.player.loadEquippedBagSQL();
		this.player.loadEquippedItemSQL();
		this.player.loadBagItemSQL();
		this.player.loadCharacterInfoSQL();
		this.player.loadFriendList();
		//this.player.loadSpellUnlocked();
		this.player.sendStats();
		Server.addLoggedPlayer(this.player);
		Server.removeLoggedPlayer(this.player);
	}
	
	@Override
	public void write() {}
}

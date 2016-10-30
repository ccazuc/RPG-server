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
		this.player.loadCharacterInfoSQL();
		this.player.sendStats();
		this.player.loadEquippedBagSQL();
		this.player.loadEquippedItemSQL();
		this.player.loadBagItemSQL();
		this.player.loadFriendList();
		//this.player.loadSpellUnlocked();
		Server.addInGamePlayer(this.player);
		Server.removeLoggedPlayer(this.player);
	}
	
	@Override
	public void write() {}
}

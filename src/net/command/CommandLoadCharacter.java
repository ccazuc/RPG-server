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
		if(Server.getLoggedPlayerList().containsKey(this.player.getAccountId())) {
			this.player.setCharacterId(id);
			this.player.initTable();
			this.player.loadCharacterInfoSQL();
			this.player.sendStats();
			this.player.loadEquippedBagSQL();
			this.player.loadEquippedItemSQL();
			this.player.loadBagItemSQL();
			this.player.loadFriendList();
			CommandFriend.loadFriendList(this.player);
			this.player.notifyFriendOnline();
			//this.player.loadSpellUnlocked();
			Server.addInGamePlayer(this.player);
			Server.removeLoggedPlayer(this.player);
		}
		else {
			//player is using WPE
		}
	}
	
	@Override
	public void write() {}
}

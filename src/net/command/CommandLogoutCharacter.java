package net.command;

import net.connection.ConnectionManager;

public class CommandLogoutCharacter extends Command {
	
	public CommandLogoutCharacter(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	@Override
	public void read() {
		this.player.resetDatas();
		CommandTrade.closeTrade(this.player);
	}
}

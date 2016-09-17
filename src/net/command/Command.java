package net.command;

import net.Player;
import net.connection.Connection;
import net.connection.ConnectionManager;

public class Command {
	
	protected Connection connection;
	protected Player player;
	
	public Command(final ConnectionManager connectionManager) {
		this.connection = connectionManager.getConnection();
		this.player = connectionManager.getPlayer();
	}
	
	public void read() {}
}

package net.command;

import net.connection.Connection;
import net.connection.ConnectionManager;
import net.game.Player;

public class Command {
	
	protected Connection connection;
	protected Player player;
	
	public Command(final ConnectionManager connectionManager) {
		this.connection = connectionManager.getConnection();
		this.player = connectionManager.getPlayer();
	}
	
	public void read() {}
	public void write() {}
	public void write(byte PacketID, int id, int value) {}
}

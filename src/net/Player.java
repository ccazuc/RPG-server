package net;
import java.nio.channels.SocketChannel;

import net.connection.ConnectionManager;

public class Player {

	private ConnectionManager connectionManager;
	private int id;
	private String name;
	private boolean logged;
	
	public Player(SocketChannel socket) {
		this.connectionManager = new ConnectionManager(this, socket);
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public ConnectionManager getConnectionManager() {
		return this.connectionManager;
	}
	
	public boolean isLoggedIn() {
		return this.logged;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void close() {
		this.connectionManager.getConnection().close();
		Server.removeNonLoggedPlayer(this);
		Server.removeLoggedPlayer(this);
	}
}

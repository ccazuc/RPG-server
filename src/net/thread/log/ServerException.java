package net.thread.log;

import net.game.unit.Player;

public class ServerException {

	
	private Exception e;
	private Player player;
	
	public ServerException(Exception e, Player player) {
		this.e = e;
		this.player = player;
	}
	
	public ServerException(Exception e) {
		this(e, null);
	}
	
	public Exception getException() {
		return this.e;
	}
	
	public Player getPlayer() {
		return this.player;
	}
}

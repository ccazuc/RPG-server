package net.connection;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Server;
import net.game.Player;

public class SQLRequest {
	
	private JDOStatement statement;
	protected int id;
	protected Player player;
	private boolean shouldSendPlayerId;
	
	public SQLRequest(String request, boolean shouldSendPlayerId) {
		try {
			this.statement = Server.getJDO().prepare(request);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		this.shouldSendPlayerId = shouldSendPlayerId;
	}
	
	public void execute() throws SQLException {
		this.statement.clear();
		if(this.shouldSendPlayerId) {
			this.statement.putInt(this.id);
		}
		this.statement.execute();
		gatherData();
		this.id = 0;
		this.player = null;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setPlayer(Player message) {
		this.player = message;
	}
	
	public void gatherData() throws SQLException {}
}

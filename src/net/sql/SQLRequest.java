package net.sql;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Server;
import net.game.Player;

public class SQLRequest {
	
	protected JDOStatement statement;
	protected int id;
	protected Player player;
	private boolean shouldSendPlayerId;
	protected String userName;
	protected String password;
	
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
		gatherData();
		this.id = 0;
		this.player = null;
		this.userName = null;
		this.password = null;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setPlayer(Player message) {
		this.player = message;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void gatherData() {}
}

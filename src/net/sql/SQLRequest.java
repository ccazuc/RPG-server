package net.sql;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Servers;
import net.game.Player;

public class SQLRequest {
	
	protected JDOStatement statement;
	protected int id;
	protected Player player;
	protected String userName;
	protected String password;
	
	public SQLRequest(String request) {
		try {
			this.statement = Servers.getJDO().prepare(request);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void execute() {
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

package net.thread.sql;

import java.sql.SQLException;
import java.util.ArrayList;

import jdo.JDOStatement;
import net.Server;

public class SQLRequest {
	
	protected JDOStatement statement;
	/*protected int id;
	protected int id2;
	protected long value;
	protected String name;
	protected String msg;
	protected Player player;
	protected String userName;
	protected String password;*/
	protected ArrayList<SQLDatas> datasList;
	
	public SQLRequest(String request) {
		try {
			this.statement = Server.getAsynJDO().prepare(request);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		this.datasList = new ArrayList<SQLDatas>();
	}
	
	public void execute() {
		gatherData();
		if(this.datasList.size() > 0) {
			this.datasList.remove(0);
		}
	}
	
	public void addDatas(SQLDatas datas) {
		this.datasList.add(datas);
	}
	
	/*public void setValue(long value) {
		this.value = value;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setId2(int id) {
		this.id2 = id;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public void setPlayer(Player message) {
		this.player = message;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}*/
	
	public void gatherData() {}
}

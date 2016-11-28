package net.thread.sql;

import java.sql.SQLException;
import java.util.ArrayList;

import jdo.JDOStatement;
import net.Server;

public class SQLRequest {
	
	protected JDOStatement statement;
	protected ArrayList<SQLDatas> datasList;
	protected String name;
	protected boolean debugActive;
	
	public SQLRequest(String request, String name) {
		try {
			this.statement = Server.getAsyncJDO().prepare(request);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		this.datasList = new ArrayList<SQLDatas>();
		this.name = name;
		this.debugActive = true;
	}
	
	public void execute() {
		gatherData();
		if(this.datasList.size() > 0) {
			this.datasList.remove(0);
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean debugActive() {
		return this.debugActive;
	}
	
	public void addDatas(SQLDatas datas) {
		this.datasList.add(datas);
	}
	
	public void gatherData() {}
}

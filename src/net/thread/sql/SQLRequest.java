package net.thread.sql;

import java.sql.SQLException;
import java.util.ArrayList;

import jdo.JDOStatement;
import net.Server;

public class SQLRequest {
	
	protected JDOStatement statement;
	protected ArrayList<SQLDatas> datasList;
	
	public SQLRequest(String request) {
		try {
			this.statement = Server.getAsyncJDO().prepare(request);
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
	
	public void gatherData() {}
}

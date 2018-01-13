package net.thread.sql;

import java.sql.SQLException;
import java.util.ArrayList;

import jdo.JDOStatement;
import net.Server;

public class SQLRequest {
	
	protected JDOStatement statement;
	protected final ArrayList<SQLDatas> datasList;
	protected final String name;
	protected final SQLRequestPriority priority;
	protected final boolean debugActive;
	
	public SQLRequest(String request, String name, SQLRequestPriority priority) {
		try {
			if(priority == SQLRequestPriority.LOW) {
				this.statement = Server.getAsyncLowPriorityJDO().prepare(request);
			}
			else {
				this.statement = Server.getAsyncHighPriorityJDO().prepare(request);
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		this.priority = priority;
		this.datasList = new ArrayList<SQLDatas>();
		this.name = name;
		this.debugActive = true;
	}
	
	public SQLRequest(String request, String name, SQLRequestPriority priority, boolean debug) {
		try {
			if(priority == SQLRequestPriority.LOW) {
				this.statement = Server.getAsyncLowPriorityJDO().prepare(request);
			}
			else {
				this.statement = Server.getAsyncHighPriorityJDO().prepare(request);
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		this.priority = priority;
		this.datasList = new ArrayList<SQLDatas>();
		this.name = name;
		this.debugActive = debug;
	}
	public final void execute() {
		synchronized (this.datasList)
		{
			this.statement.clear();
			try {
				gatherData();
				this.statement.execute();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
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
		synchronized (this.datasList)
		{
			this.datasList.add(datas);
		}
	}
	
	public SQLRequestPriority getPriority() {
		return this.priority;
	}
	
	@SuppressWarnings("unused")
	public void gatherData() throws SQLException {}
}

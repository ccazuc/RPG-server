package net.thread.sql;

import java.util.ArrayList;

public class SQLTask {

	protected final ArrayList<SQLDatas> datasList;
	private final String name;
	private final boolean debug;
	
	public SQLTask(String name) {
		this.datasList = new ArrayList<SQLDatas>();
		this.name = name;
		this.debug = true;
	}
	
	public SQLTask(String name, boolean debug) {
		this.datasList = new ArrayList<SQLDatas>();
		this.name = name;
		this.debug = debug;
	}
	
	public final void execute() {
		synchronized (this.datasList)
		{
			gatherData();
			this.datasList.remove(0);
		}
	}
	
	public void gatherData() {}
	
	public void addDatas(SQLDatas datas) {
		synchronized(this.datasList) {
			this.datasList.add(datas);
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean getDebug()
	{
		return (this.debug);
	}
}

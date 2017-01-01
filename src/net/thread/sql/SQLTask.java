package net.thread.sql;

import java.util.ArrayList;

public class SQLTask {

	protected final ArrayList<SQLDatas> datasList;
	private String name;
	
	public SQLTask(String name) {
		this.datasList = new ArrayList<SQLDatas>();
		this.name = name;
	}
	
	public void execute() {
		gatherData();
		this.datasList.remove(0);
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
}

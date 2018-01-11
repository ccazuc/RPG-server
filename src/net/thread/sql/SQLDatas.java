package net.thread.sql;

public class SQLDatas {

	private final Object[] object;
	private int currentIndex = -1;
	
	public SQLDatas(Object ...obj)
	{
		this.object = obj;
	}
	
	public Object getNextObject()
	{
		return (this.object[++this.currentIndex]);
	}
}

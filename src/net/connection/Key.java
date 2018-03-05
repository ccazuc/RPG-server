package net.connection;

public class Key {

	private final int accountRank;
	private final int accountId;
	private final double value;
	private final String accountName;
	private final long timer;
	private final String ipAdress;
	
	public Key(int accountId, int accountRank, String accountName, double value, String ipAdress, long timer) {
		this.timer = timer;
		this.accountRank = accountRank;
		this.accountId = accountId;
		this.value = value;
		this.accountName = accountName;
		this.ipAdress = ipAdress;
	}
	
	public String getIpAdress()
	{
		return (this.ipAdress);
	}

	public String getAccountName()
	{
		return (this.accountName);
	}
	
	public int getAccountId()
	{
		return (this.accountId);
	}
	
	public long getTimer()
	{
		return (this.timer);
	}
	
	public double getValue()
	{
		return (this.value);
	}
	
	public int getAccountRank()
	{
		return (this.accountRank);
	}
}

package net.connection;

public class Key {

	private int accountRank;
	private int accountId;
	private double value;
	private String accountName;
	private long timer;
	
	public Key(int accountId, int accountRank, String accountName, double value) {
		this.timer = System.currentTimeMillis();
		this.accountRank = accountRank;
		this.accountId = accountId;
		this.value = value;
		this.accountName = accountName;
	}
	
	public String getAccountName() {
		return this.accountName;
	}
	
	public int getAccountId() {
		return this.accountId;
	}
	
	public long getTimer() {
		return this.timer;
	}
	
	public double getValue() {
		return this.value;
	}
	
	public int getAccountRank() {
		return this.accountRank;
	}
}

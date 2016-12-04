package net.connection;

public class Key {

	private int accountRank;
	private int accountId;
	private double value;
	private long timer;
	
	public Key(int accountId, int accountRank, double value) {
		this.timer = System.currentTimeMillis();
		this.accountRank = accountRank;
		this.accountId = accountId;
		this.value = value;
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

package net.connection;

public class Key {

	private int accountId;
	private long timer;
	private double value;
	
	public Key(int accountId, double value) {
		this.accountId = accountId;
		this.value = value;
		this.timer = System.currentTimeMillis();
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
}

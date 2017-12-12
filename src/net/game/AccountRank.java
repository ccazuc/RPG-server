package net.game;

import net.thread.log.LogRunnable;

public enum AccountRank {

	PLAYER((byte)1, "Player"),
	MODERATOR((byte)2, "Moderator"),
	GAMEMASTER((byte)3, "Gamemaster"),
	ADMINISTRATOR((byte)4, "Administrator");
	
	private byte value;
	private String name;
	
	private AccountRank(byte value, String name) {
		this.value = value;
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public byte getValue() {
		return this.value;
	}
	
	public boolean superiorTo(AccountRank rank) {
		return this.value > rank.getValue();
	}
	
	public boolean superiorOrEqualsTo(AccountRank rank) {
		return this.value >= rank.getValue();
	}
	
	public static AccountRank getRank(int index) {
		if (index < 0 || index >= AccountRank.values().length)
		{
			LogRunnable.addErrorLog("Error in AccountRank.getRank(), invalid index: "+index);
		}
		return values()[index-1];
	}
}

package net.game;

public enum AccountRank {

	PLAYER((byte)1),
	MODERATOR((byte)2),
	GAMEMASTER((byte)3),
	ADMINISTRATOR((byte)4);
	
	private byte value;
	
	private AccountRank(byte value) {
		this.value = value;
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
	
	public static AccountRank get(int index) {
		return values()[index-1];
	}
}

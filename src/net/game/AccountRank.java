package net.game;

public enum AccountRank {

	PLAYER((char)1),
	MODERATOR((char)2),
	GAMEMASTER((char)3),
	ADMINISTRATOR((char)4);
	
	private char value;
	
	private AccountRank(char value) {
		this.value = value;
	}
	
	public char getValue() {
		return this.value;
	}
	
	public static AccountRank get(int index) {
		return values()[index-1];
	}
}

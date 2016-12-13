package net.game;

public enum AccountRank {

	PLAYER((char)0),
	MODERATOR((char)1),
	GAMEMASTER((char)2),
	ADMINISTRATOR((char)3);
	
	private char value;
	
	private AccountRank(char value) {
		this.value = value;
	}
	
	public char getValue() {
		return this.value;
	}
}

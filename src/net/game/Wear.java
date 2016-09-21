package net.game;

public enum Wear {

	CLOTH((char)0),
	LEATHER((char)1),
	MAIL((char)2),
	PLATE((char)3),
	NONE((char)4);
	
	private char value;
	
	private Wear(char value) {
		this.value = value;
	}
	
	public char getValue() {
		return this.value;
	}
}

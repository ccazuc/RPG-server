package net.game;

public enum UnitType {
	PLAYER((char)0),
	GM((char)1),
	NPC((char)2);
	
	private char value;
	
	private UnitType(char value) {
		this.value = value;
	}
	
	public char getValue() {
		return this.value;
	}
}

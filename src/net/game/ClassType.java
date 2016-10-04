package net.game;

public enum ClassType {

	DRUID((char)0),
	GUERRIER((char)1),
	HUNTER((char)2),
	MAGE((char)3),
	PALADIN((char)4),
	PRIEST((char)5),
	ROGUE((char)6),
	SHAMAN((char)7),
	WARLOCK((char)8);
	
	private char value;
	
	private ClassType(char value) {
		this.value = value;
	}
	
	public char getValue() {
		return this.value;
	}
}

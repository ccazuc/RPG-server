package net.game;

public enum ClassType {

	DEATHKNIGHT((char)0),
	GUERRIER((char)1),
	HUNTER((char)2),
	MAGE((char)3),
	MONK((char)4),
	PALADIN((char)5),
	PRIEST((char)6),
	ROGUE((char)7),
	SHAMAN((char)8),
	WARLOCK((char)9);
	
	private char value;
	
	private ClassType(char value) {
		this.value = value;
	}
	
	public char getValue() {
		return this.value;
	}
}

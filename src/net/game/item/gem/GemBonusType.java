package net.game.item.gem;

public enum GemBonusType {

	ARMOR((char)0),
	STAMINA((char)1),
	MANA((char)2),
	STRENGTH((char)3),
	CRITICAL((char)4),
	NONE((char)5);
	
	private char value;
	
	private GemBonusType(char value) {
		this.value = value;
	}
	
	public char getValue() {
		return this.value;
	}
}

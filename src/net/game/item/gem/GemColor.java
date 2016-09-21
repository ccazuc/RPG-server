package net.game.item.gem;

public enum GemColor {

	RED((char)0),
	GREEN((char)1),
	BLUE((char)2),
	PURPLE((char)3),
	ORANGE((char)4),
	YELLOW((char)5),
	META((char)6),
	NONE((char)7);
	
	private char value;
	
	private GemColor(char value) {
		this.value = value;
	}
	
	public char getValue() {
		return this.value;
	}
}

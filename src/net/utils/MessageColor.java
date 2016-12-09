package net.utils;

public enum MessageColor {
	
	YELLOW((char)0, new Color(1, 251/255f, 0)),
	GREEN((char)1, Color.GREEN),
	BLUE((char)2, Color.BLUE),
	WHITE((char)3, Color.WHITE),
	;
	
	private Color color;
	private char value;

	private MessageColor(char value, Color color) {
		this.color = color;
		this.value = value;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public char getValue() {
		return this.value;
	}
}

package net.command.chat;

import net.utils.Color;

public enum MessageColor {
	
	YELLOW((byte)0, new Color(1, 251/255f, 0)),
	GREEN((byte)1, new Color(64/255f, 1, 64/255f)),
	BLUE((byte)2, Color.BLUE),
	WHITE((byte)3, Color.WHITE),
	RED((byte)4, Color.RED),
	ANNOUNCE((byte)5, new Color(0/255f, 208/255f, 225/255f)),
	;
	
	private Color color;
	private byte value;

	private MessageColor(byte value, Color color) {
		this.color = color;
		this.value = value;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public byte getValue() {
		return this.value;
	}
}

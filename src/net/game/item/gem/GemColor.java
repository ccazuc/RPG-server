package net.game.item.gem;

public enum GemColor {

	RED((byte)0),
	GREEN((byte)1),
	BLUE((byte)2),
	PURPLE((byte)3),
	ORANGE((byte)4),
	YELLOW((byte)5),
	META((byte)6),
	NONE((byte)7);
	
	private byte value;
	
	private GemColor(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}
}

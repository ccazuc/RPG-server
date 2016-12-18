package net.game;

public enum Wear {

	CLOTH((byte)0),
	LEATHER((byte)1),
	MAIL((byte)2),
	PLATE((byte)3),
	NONE((byte)4);
	
	private byte value;
	
	private Wear(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}
}

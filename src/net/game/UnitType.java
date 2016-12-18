package net.game;

public enum UnitType {
	
	PLAYER((byte)0),
	GM((byte)1),
	NPC((byte)2);
	
	private byte value;
	
	private UnitType(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}
}

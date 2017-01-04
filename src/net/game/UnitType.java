package net.game;

public enum UnitType {
	
	PLAYER((byte)0),
	NPC((byte)1);
	
	private byte value;
	
	private UnitType(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}
}

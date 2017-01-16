package net.game.unit;

public enum Faction {

	HORDE((byte)0),
	ALLIANCE((byte)1),
	;
	
	private final byte value;
	
	private Faction(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}
}

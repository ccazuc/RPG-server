package net.game.unit;

public enum Race {

	HUMAN((byte)0),
	DWARF((byte)1),
	NIGHTELF((byte)2),
	GNOME((byte)3),
	DRAENEI((byte)4),
	ORC((byte)5),
	UNDEAD((byte)6),
	TAUREN((byte)7),
	TROLL((byte)8),
	BLOODELF((byte)9);

	private final byte value;
    
	Race(byte value) {
		this.value = value;
	}
    
	public byte getValue() {
		return this.value;
	}
}

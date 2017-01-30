package net.game.item;

public enum ItemQuality {

	POOR((byte)0),
	COMMON((byte)1),
	UNCOMMON((byte)2),
	RARE((byte)3),
	EPIC((byte)4),
	LEGENDARY((byte)5),
	ARTIFACT((byte)6),
	HEIRLOOM((byte)7),
	
	;
	
	private final byte value;
	
	private ItemQuality(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}
}

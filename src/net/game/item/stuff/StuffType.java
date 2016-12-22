package net.game.item.stuff;

public enum StuffType {

	HEAD((byte)0, (byte)0),
	NECKLACE((byte)1, (byte)1),
	SHOULDERS((byte)2, (byte)2),
	CHEST((byte)3, (byte)3),
	BACK((byte)4, (byte)4),
	RAN((byte)5, (byte)5),
	RANDOM((byte)6, (byte)6),
	WRISTS((byte)7, (byte)7),
	GLOVES((byte)8, (byte)8),
	BELT((byte)9, (byte)9),
	LEGGINGS((byte)10, (byte)10),
	BOOTS((byte)11, (byte)11),
	RING((byte)12, (byte)12, (byte)13),
	TRINKET((byte)13, (byte)14, (byte)15),
	MAINHAND((byte)14, (byte)16),
	OFFHAND((byte)15, (byte)17),
	RANGED((byte)16, (byte)18);
	
	private byte value;
	private byte slot;
	private byte slot2;
	
	private StuffType(byte value, byte slot) {
		this.value = value;
		this.slot = slot;
	}
	
	private StuffType(byte value, byte slot, byte slot2) {
		this.value = value;
		this.slot2 = slot2;
		this.slot = slot;
	}
	
	public byte getValue() {
		return this.value;
	}
	
	public byte getSlot() {
		return this.slot;
	}
	
	public byte getSlot2() {
		return this.slot2;
	}
}

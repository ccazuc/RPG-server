package net.game.item.stuff;

public enum StuffType {

	HEAD((byte)0),
	NECKLACE((byte)1),
	SHOULDERS((byte)2),
	CHEST((byte)3),
	BACK((byte)4),
	RAN((byte)5),
	RANDOM((byte)6),
	WRISTS((byte)7),
	GLOVES((byte)8),
	BELT((byte)9),
	LEGGINGS((byte)10),
	BOOTS((byte)11),
	RING((byte)12),
	TRINKET((byte)13),
	MAINHAND((byte)14),
	OFFHAND((byte)15),
	RANGED((byte)16);
	
	private byte value;    

	private StuffType(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return this.value;
	}
}

package net.game.item;

public enum ItemType {

	STUFF((byte)0),
	ITEM((byte)1),
	POTION((byte)2),
	CONTAINER((byte)3),
	WEAPON((byte)4),
	GEM((byte)5);
	
	private byte value;
	
	private ItemType(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}
}

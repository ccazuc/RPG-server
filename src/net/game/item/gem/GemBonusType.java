package net.game.item.gem;

public enum GemBonusType {

	ARMOR((byte)0),
	STAMINA((byte)1),
	MANA((byte)2),
	STRENGTH((byte)3),
	CRITICAL((byte)4),
	NONE((byte)5);
	
	private byte value;
	
	private GemBonusType(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}
}

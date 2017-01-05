package net.game.item.gem;

public enum GemBonusType {

	ARMOR((byte)0),
	STAMINA((byte)1),
	MANA((byte)2),
	STRENGTH((byte)3),
	CRITICAL((byte)4),
	INTELLIGENCE((byte)5),
	SPELL_CRITICAL((byte)6),
	HASTE((byte)7),
	SPELL_HASTE((byte)8),
	MP5((byte)9),
	HEALING_POWER((byte)10),
	NONE((byte)11);
	
	private byte value;
	
	private GemBonusType(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}
}

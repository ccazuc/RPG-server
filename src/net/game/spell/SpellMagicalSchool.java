package net.game.spell;

public enum SpellMagicalSchool {

	HOLY((byte)0),
	SHADOW((byte)1),
	FIRE((byte)2),
	FROST((byte)3),
	NATURE((byte)4),
	ARCANE((byte)5),
	;
	
	private byte value;
	
	private SpellMagicalSchool(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}
}

package net.game.item.weapon;

public enum WeaponType {

	ONEHANDEDAXE((byte)0),
	TWOHANDEDAXE((byte)1),
	ONEHANDEDSWORD((byte)2),
	TWOHANDEDSWORD((byte)3),
	ONEHANDEDMACE((byte)4),
	TWOHANDEDMACE((byte)5),
	POLEARM((byte)6),
	STAFF((byte)7),
	DAGGER((byte)8),
	FISTWEAPON((byte)9),
	BOW((byte)10),
	CROSSBOW((byte)11),
	GUN((byte)12),
	THROWN((byte)13),
	WAND((byte)14);
	
	private byte value;
	
	private WeaponType(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}
}

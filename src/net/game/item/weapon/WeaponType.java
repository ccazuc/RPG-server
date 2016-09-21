package net.game.item.weapon;

public enum WeaponType {

	ONEHANDEDAXE((char)0),
	TWOHANDEDAXE((char)1),
	ONEHANDEDSWORD((char)2),
	TWOHANDEDSWORD((char)3),
	ONEHANDEDMACE((char)4),
	TWOHANDEDMACE((char)5),
	POLEARM((char)6),
	STAFF((char)7),
	DAGGER((char)8),
	FISTWEAPON((char)9),
	BOW((char)10),
	CROSSBOW((char)11),
	GUN((char)12),
	THROWN((char)13),
	WAND((char)14);
	
	private char value;
	
	private WeaponType(char value) {
		this.value = value;
	}
	
	public char getValue() {
		return this.value;
	}
}

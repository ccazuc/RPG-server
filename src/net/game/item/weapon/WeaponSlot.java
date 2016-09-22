package net.game.item.weapon;

public enum WeaponSlot {

	MAINHAND((char)0),
	OFFHAND((char)1),
	BOTH((char)2),
	RANGED((char)3);
	
	private char value;
	
	private WeaponSlot(char value) {
		this.value = value;
	}
	
	public char getValue() {
		return this.value;
	}
}

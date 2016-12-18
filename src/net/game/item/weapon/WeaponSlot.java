package net.game.item.weapon;

public enum WeaponSlot {

	MAINHAND((byte)0),
	OFFHAND((byte)1),
	BOTH((byte)2),
	RANGED((byte)3);
	
	private byte value;
	
	private WeaponSlot(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}
}

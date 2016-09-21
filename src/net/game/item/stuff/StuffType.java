package net.game.item.stuff;

public enum StuffType {

	HEAD((char)0),
	NECKLACE((char)1),
	SHOULDERS((char)2),
	CHEST((char)3),
	BACK((char)4),
	RAN((char)5),
	RANDOM((char)6),
	WRISTS((char)7),
	GLOVES((char)8),
	BELT((char)9),
	LEGGINGS((char)10),
	BOOTS((char)11),
	RING((char)12),
	TRINKET((char)13),
	MAINHAND((char)14),
	OFFHAND((char)15),
	RANGED((char)16);
	
	private char value;    

	private StuffType(char value) {
		this.value = value;
	}

	public char getValue() {
		return this.value;
	}
}

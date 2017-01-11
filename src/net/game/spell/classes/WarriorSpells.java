package net.game.spell.classes;

public enum WarriorSpells {

	CHARGE_R1(101),
	HEROIC_STRIKE_R1(102),
	MORTAL_STRIKE_R1(103),
	REND_R1(104),
	THUNDER_CLAP_R1(105),
	;
	
	private final int id;
	
	private WarriorSpells(int id) {
		this.id = id;
	}
	
	public int getID() {
		return this.id;
	}
}

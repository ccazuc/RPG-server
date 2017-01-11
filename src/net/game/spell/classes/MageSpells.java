package net.game.spell.classes;

public enum MageSpells {

	FIREBALL_R1(301),
	FLAME_STRIKE_R1(302),
	PYROBLAST_R1(303),
	;
	
	private final int id;
	
	private MageSpells(int id) {
		this.id = id;
	}
	
	public int getID() {
		return this.id;
	}
}

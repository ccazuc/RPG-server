package net.game.spell.classes;

public enum HunterSpells {

	ARCANE_SHOT_R1(201),
	MULTI_SHOT_R1(202),
	STEADY_SHOT_R1(203),
	;
	
	private final int id;
	
	private HunterSpells(int id) {
		this.id = id;
	}
	
	public int getID() {
		return this.id;
	}
}

package net.game.spell.classes;

public enum RogueSpells {

	AMBUSH_R1(701),
	EVISCERATE_R1(702),
	SINISTER_STRIKE_R1(703),
	;
	
	private final int id;
	
	private RogueSpells(int id) {
		this.id = id;
	}
	
	public int getID() {
		return this.id;
	}
}

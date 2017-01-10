package net.game.spell.classes;

public enum PriestSpells {

	RENEW(604),
	
	
	;
	private final int id;
	
	private PriestSpells(int id) {
		this.id = id;
	}
	
	public int getID() {
		return this.id;
	}
}

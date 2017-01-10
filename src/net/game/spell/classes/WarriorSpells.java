package net.game.spell.classes;

public enum WarriorSpells {

	MORTAL_STRIKE(103),
	
	
	;
	private final int id;
	
	private WarriorSpells(int id) {
		this.id = id;
	}
	
	public int getID() {
		return this.id;
	}
}

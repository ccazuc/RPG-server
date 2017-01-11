package net.game.spell.classes;

public enum PriestSpells {

	FLASH_HEAL_R1(601),
	HOLY_NOVA_R1(602),
	PENANCE_R1(603),
	RENEW_R1(604),	
	;
	
	private final int id;
	
	private PriestSpells(int id) {
		this.id = id;
	}
	
	public int getID() {
		return this.id;
	}
}

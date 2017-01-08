package net.game.aura.classe;

public enum MageAuraID {

	FROSTBOLD_R1(2),
	
	;
	private int id;
	
	
	private MageAuraID(int id) {
		this.id = id;
	}
	
	public int getID() {
		return this.id;
	}
}

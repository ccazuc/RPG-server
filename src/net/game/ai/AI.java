package net.game.ai;

import net.game.spell.Spell;
import net.game.unit.Unit;

public class AI {
	
	protected final Unit unit;
	protected boolean hasSpawned;
	protected final boolean hasCsSpell;
	
	public AI(Unit unit) {
		this.unit = unit;
		this.hasCsSpell = false;
	}
	
	public void onTick() {}
	
	public void onEnemySpellCast(Spell spell) {}
	
	public void onStun() {}
	
	public void onSilence() {}
	
	public void onFear() {}
	
	public void onKick() {}
	
	public void onDeath() {}
	
	public void onSpawn() {}
}

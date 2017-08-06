package net.game.ai;

import net.game.spell.Spell;
import net.game.unit.Unit;

public class PriestAI extends AI {
	
	public PriestAI(Unit unit) {
		super(unit);
	}
	
	@Override
	public void onTick() {
		if (!this.hasSpawned) {
			onSpawn();
			this.hasSpawned = true;
		}
		if (this.unit.isCasting()) {
			return;
		}
	}
	
	@Override
	public void onEnemySpellCast(Spell spell) {
		System.out.println("PriestAI.onEnemySpellCast "+spell.getName());
	}
	
	@Override
	public void onStun() {
		
	}
	
	@Override
	public void onSilence() {
		
	}
	
	@Override
	public void onKick() {
		
	}
	
	@Override
	public void onDeath() {
		System.out.println("PriestAI.onDeath");
	}
	
	@Override
	public void onSpawn() {
		System.out.println("PriestAI.onSpawn");
	}
}

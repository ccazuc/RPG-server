package net.game.spell;

import net.game.aura.AuraMgr;
import net.game.spell.classes.HunterSpells;
import net.game.spell.classes.MageSpells;
import net.game.spell.classes.PriestSpells;
import net.game.spell.classes.RogueSpells;
import net.game.spell.classes.WarriorSpells;
import net.game.unit.Unit;

public class StoreSpell {
	
	public static void createSpell(int id, String sprite_id, String name, byte rank, int effectValue, int manaCost, float stunRate, int stunDuration, int cd, int castTime, boolean triggerGCD, SpellMagicalSchool magicalSchool, boolean isMagical) {
		if(id == 1) {			//deathCoil
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical) {
				
				@Override
				public boolean action(Unit caster, Unit target) {
					return true;
				}
			});
		}
		else if(id == 2) {		//deathStrike
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 3) {		//deathPlague
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 101) {		//charge			
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 102) {		//heroicStrike
			
		}
		else if(id == WarriorSpells.MORTAL_STRIKE_R1.getID()) {
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical) {
				
				@Override
				public boolean action(Unit caster, Unit target) {
					if(!Spell.checkSingleTarget(caster)) {
						return false;
					}
					caster.applyAura(AuraMgr.getAura(1), caster);
					return true;
				}
				
				@Override
				public boolean canCast(Unit caster, Unit target) {
					return true;
				}
			});
		}
		else if(id == 104) {		//rend
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 105) {		//thunderClap
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == HunterSpells.ARCANE_SHOT_R1.getID()) {
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == HunterSpells.MULTI_SHOT_R1.getID()) {
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == HunterSpells.STEADY_SHOT_R1.getID()) {
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == MageSpells.FIREBALL_R1.getID()) {
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == MageSpells.FLAME_STRIKE_R1.getID()) {
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == MageSpells.PYROBLAST_R1.getID()) {
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 401) {		//tigerPalm
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 402) {		//tigerStrike
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 403) {		//touchOfDeath
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 501) {		//crusaderStrike
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 502) {		//judgment
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 503) {		//layOfHands
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == PriestSpells.FLASH_HEAL_R1.getID()) {
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == PriestSpells.HOLY_NOVA_R1.getID()) {
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == PriestSpells.PENANCE_R1.getID()) {
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == PriestSpells.RENEW_R1.getID()) {
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical) {
				
				@Override
				public boolean action(Unit caster, Unit target) {
					caster.applyAura(AuraMgr.getAura(2), caster);
					caster.applyAura(AuraMgr.getAura(3), caster);
					doDamage(caster, target, 1500);
					return true;
				}
				
				@Override
				public boolean canCast(Unit caster, Unit target) {
					return true;
				}
			});
		}
		else if(id == RogueSpells.AMBUSH_R1.getID()) {
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == RogueSpells.EVISCERATE_R1.getID()) {
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == RogueSpells.SINISTER_STRIKE_R1.getID()) {
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 801) {		//chainLightning
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 802) {		//healingSurge
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 803) {		//lightningBolt
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 901) {		//corruption
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 902) {		//immolation
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 903) {		//shadowBolt
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 9999) {		//MJ
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else {
			System.out.println("SPELL LOAD ERROR : spell not found [id: "+id+" name: "+name+']');
		}
	}
}

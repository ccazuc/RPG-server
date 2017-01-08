package net.game.spell;

import net.game.aura.AuraMgr;
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
		else if(id == 103) {		//mortalStrike
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical) {
				
				@Override
				public boolean action(Unit caster, Unit target) {
					if(!Spell.checkSingleTarget(caster)) {
						return false;
					}
					caster.applyAura(AuraMgr.getAura(1));
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
		else if(id == 201) {		//arcaneShot
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 202) {		//multiShot
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 203) {		//steadyShot
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 301) {		//fireBall
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 302) {		//flameStrike
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 303) {		//pyroBlast
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
		else if(id == 601) {		//flashHeal
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 602) { 		//holyNova
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 603) {		//penance
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 701) {		//ambush
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 702) {		//eviscerate
			SpellMgr.store(new Spell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical));
		}
		else if(id == 703) {		//sinisterStrike
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
			System.out.println("SPELL LOAD ERROR : spell not found [id : "+id+" name :"+name+']');
		}
	}
}

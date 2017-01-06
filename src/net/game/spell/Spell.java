package net.game.spell;

import net.Server;
import net.command.player.CommandSendGCD;
import net.command.player.CommandSendRedAlert;
import net.command.player.CommandSendSpellCD;
import net.game.DefaultRedAlert;
import net.game.unit.Player;
import net.game.unit.TargetType;
import net.game.unit.Unit;
import net.game.unit.UnitType;

public class Spell {

	private final int id;
	private final String sprite_id;
	private final String name;
	protected final int effectValue;
	private final int manaCost;
	private final int cd;
	private final int castTime;
	protected final float stunRate;
	private final boolean triggerGCD;
	protected final int stunDuration;
	
	public Spell(int id, String sprite_id, String name, int effectValue, int manaCost, float stunRate, int stunDuration, int cd, int castTime, boolean triggerGCD) { //Damage spells
		this.sprite_id = sprite_id;
		this.name = name;
		this.manaCost = manaCost;
		this.stunRate = stunRate;
		this.stunDuration = stunDuration;
		this.effectValue = effectValue;
		this.cd = cd;
		this.castTime = castTime;
		this.id = id;
		this.triggerGCD = triggerGCD;
	}
	
	@SuppressWarnings("unused")
	protected boolean action(Unit caster) {return false;}
	
	@SuppressWarnings("unused")
	protected boolean canCast(Unit caster) {return false;}
	
	public void use(Unit caster) {
		if(action(caster)) {
			if(this.triggerGCD) {
				if(caster.getUnitType() == UnitType.PLAYER) {
					CommandSendGCD.sendGCD((Player)caster, Server.getLoopTickTimer(), Server.getLoopTickTimer()+Unit.GCD);
				}
				caster.startGCD(Server.getLoopTickTimer());
			}
			if(this.cd > 0) {
				if(caster.getUnitType() == UnitType.PLAYER) {
					CommandSendSpellCD.sendCD((Player)caster, this.id, this.cd, Server.getLoopTickTimer());
				}
				caster.setSpellCD(this.id, Server.getLoopTickTimer()+this.cd);
			}
			caster.setMana(caster.getMana()-this.manaCost);
		}
	}
	
	public void cast(Unit caster, TargetType type) {
		/*if(this.manaCost > caster.getMana()) {
			if(caster.getUnitType() == UnitType.PLAYER) {
				CommandSendRedAlert.write((Player)caster, DefaultRedAlert.NOT_ENOUGH_MANA);
			}
			return;
		}*/
		if(canCast(caster)) {
			System.out.println("Can cast");
			if(this.castTime == 0) {
				use(caster);
			}
			else {
				System.out.println("Cast start");
				caster.cast(this);
			}
		}
	}
	
	/*public boolean doDamage(Unit target, Unit caster) {
		if(caster.getMana() < this.manaCost) {
			if(target.getUnitType() == UnitType.PLAYER) {
				
			}
			return false;
		}
		//if(caster.canCastSpell) {
		//if(!target.isProtectedAgainstSpell) {
			target.setStamina(target.getStamina()-this.getDamage(caster));
		//}
		caster.setMana(caster.getMana()-this.manaCost);
		return true;
	//}
	}*/
	
	public void doHeal(Unit target, int amount) {
		target.setStamina(target.getStamina()+amount);
	}
	
	public boolean hasMana(Player player) {
		return player.getMana() >= this.manaCost;
	}
	
	public static void doDamage(Unit caster, Unit target, int damage) {
		target.setStamina(target.getStamina()-damage);
		if(caster.getUnitType() == UnitType.PLAYER) {
			
		}
	}
	
	public void useMana(Player joueur, Spell spell) {
		joueur.setMana(joueur.getMana()-spell.manaCost);
	}

	public boolean equals(Spell spell) {
		return this.id == spell.getSpellId();
	}
	
	public static boolean checkSingleTarget(Unit unit) {
		if(unit.getTarget() == null) {
			if(unit.getUnitType() == UnitType.PLAYER) {
				CommandSendRedAlert.write((Player)unit, DefaultRedAlert.NOTHING_TO_ATTACK);
			}
			return false;
		}
		return true;
	}

	public int getManaCost() {
		return this.manaCost;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getSpellBaseCd() {
		return this.cd;
	}
	
	public int getSpellCd() {
		return 0;
	}
	
	public boolean triggerGCD() {
		return this.triggerGCD;
	}
	
	public int getSpellId() {
		return this.id;
	}
	
	public String getSpriteId() {
		return this.sprite_id;
	}
	
	public int getCastTime() {
		return this.castTime;
	}
	
	public boolean canCastWhileStunned() {return false;}
}


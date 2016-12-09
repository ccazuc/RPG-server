package net.game.spell;

import net.game.Player;
import net.game.Unit;

public class Spell {
	
	private int id;
	private String sprite_id;
	private SpellType type;
	private String name;
	private int damage;
	private int defaultDamage;
	private int manaCost;
	private int heal;
	private int cd;
	private int currentCd;
	private int castTime;
	private float stunRate;
	private int stunDuration;
	
	public Spell(int id, String sprite_id, String name, SpellType type, int damage, int manaCost, float stunRate, int stunDuration, int cd, int castTime) { //Damage spells
		this.sprite_id = sprite_id;
		this.name = name;
		this.type = type;
		this.damage = damage;
		this.manaCost = manaCost;
		this.stunRate = stunRate;
		this.stunDuration = stunDuration;
		this.cd = cd;
		this.castTime = castTime;
		this.id = id;
	}
	
	public Spell(int id, String sprite_id, String name, SpellType type, int manaCost, int heal, int cd, int castTime) { //Heal spells
		this.id = id;
		this.sprite_id = sprite_id;
		this.name = name;
		this.type = type;
		this.heal = heal;
		this.cd = cd;
		this.manaCost = manaCost;
		this.castTime = castTime;
	}

	public Spell(int id, String sprite_id, String name, SpellType type, int damage, int manaCost, float stunRate, int stunDuration, int cd, int heal, int castTime) { //Damage and heal spells
		this.sprite_id = sprite_id;
		this.name = name;
		this.type = type;
		this.damage = damage;
		this.manaCost = manaCost;
		this.stunRate = stunRate;
		this.stunDuration = stunDuration;
		this.cd = cd;
		this.castTime = castTime;
		this.id = id;
		this.heal = heal;
	}
	
	@SuppressWarnings("unused")
	public void action(Unit caster, Unit target) {}
	
	public boolean doDamage(Unit target, Unit caster) {
		if(this.currentCd <= 0) {
			//if(caster.getMana() >= this.manaCost) {
				//if(caster.canCastSpell) {
					//if(!target.isProtectedAgainstSpell) {
						System.out.println("before: "+target.getStamina());
						target.setStamina(target.getStamina()-this.getDamage(caster));
						System.out.println("after: "+target.getStamina());
					//}
					caster.setMana(caster.getMana()-this.manaCost);
					return true;
				//}
			//}
		}
		return false;
	}
	
	public boolean doHeal(Unit caster, Unit target) {
		if(this.currentCd <= 0) {
			if(caster.getMana() >= this.manaCost) {
				//if(caster.canCastSpell()) {
					//if(!target.isProtectedAgainstHeal()) {
						doHeal(target);
					//}
					caster.setMana(caster.getMana()-this.manaCost);
					return true;
				//}
			}
		}
		return false;
	}
	
	public void doHeal(Unit target) {
		target.setStamina(target.getStamina()+this.getHeal());
	}

	public int getManaCost() {
		return this.manaCost;
	}
	
	public boolean hasMana(Player player) {
		return player.getMana() >= this.manaCost;
	}
	
	public String getSpriteId() {
		return this.sprite_id;
	}
	
	public SpellType getType() {
		return this.type;
	}
	
	public int getDamage(Unit player) {
		return (int)(player.getStrength()+this.damage*(Math.random()*.1+.95));
	}
	
	public int getHeal() {
		return (int)(this.heal*(1+Math.random()));
	}
	
	public int getDefaultamage() {
		return this.defaultDamage;
	}
	
	public int getDefaultDamage() {
		return this.defaultDamage;
	}
	
	
	public int getCastTime() {
		return this.castTime;
	}
	
	public void useMana(Player joueur, Spell spell) {
		joueur.setMana(joueur.getMana()-spell.manaCost);
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
	
	public boolean equal(Spell spell) {
		return this.id == spell.getSpellId();
	}
	
	public void setSpellCd(int number) {
		this.currentCd = number;
	}

	public static void checkKeyboardCd(Spell spell) {
		if(spell != null) {
			spell.setSpellCd(spell.getSpellBaseCd());
		}
	}
	
	public int getSpellId() {
		return this.id;
	}
}


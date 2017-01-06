package net.game.unit;

import java.util.ArrayList;
import java.util.HashMap;

import net.Server;
import net.game.aura.AppliedAura;
import net.game.spell.Spell;

public class Unit {

	public final static int GCD = 1500;
	protected Unit target;
	protected Unit castTarget;
	protected ArrayList<AppliedAura> auraList;
	protected HashMap<Integer, Long> spellCDMap;
	protected int level;
	protected String name;
	protected int maxStaminaUnAura;
	protected int maxStaminaEffective;
	protected int stamina;
	protected int maxManaUnAura;
	protected int maxManaEffective;
	protected int mana;
	protected int armorUnAura;
	protected int armorEffective;
	protected int damage;
	protected int criticalUnAura;
	protected int criticalEffective;
	protected int strengthUnAura;
	protected int strengthEffective;
	protected UnitType unitType;
	protected Spell spellCasting;
	protected long endCastTimer;
	protected int id;
	private int expGained;
	private int goldGained;
	protected long GCDStartTimer;
	protected long GCDEndTimer;
	
	public Unit(UnitType unitType, int id, int stamina, int maxStamina, int mana, int maxMana, int level, String name, int armor, int critical, int strength, int expGained, int goldGained) {
		this.stamina = stamina;
		this.maxStaminaUnAura = maxStamina;
		this.maxStaminaEffective = maxStamina;
		this.mana = mana;
		this.maxManaUnAura = maxMana;
		this.maxManaEffective = maxMana;
		this.stamina = 3500;
		this.maxStaminaUnAura = 5000;
		this.mana = 6000;
		this.maxManaUnAura = 7500;
		setLevel(level);
		this.name = name;
		this.id = id;
		this.armorUnAura = armor;
		this.armorEffective = armor;
		this.criticalUnAura = critical;
		this.strengthUnAura = strength;
		this.unitType = unitType;
		this.goldGained = goldGained;
		this.expGained = expGained;
		this.auraList = new ArrayList<AppliedAura>();
		this.spellCDMap = new HashMap<Integer, Long>();
	}
	
	public Unit(UnitType unitType) {
		this.unitType = unitType;
	}
	
	public void tick() {
		checkCast();
	}
	
	public void checkCast() {
		if(this.spellCasting != null && this.endCastTimer <= Server.getLoopTickTimer()) {
			System.out.println("Cast finished");
			this.spellCasting.use(this, this.castTarget);
			this.spellCasting = null;
		}
	}
	
	public boolean hasAura(int auraID) {
		int i = this.auraList.size();
		while(--i >= 0) {
			if(this.auraList.get(i).getAura().getId() == auraID) {
				return true;
			}
		}
		return false;
	}
	
	public long getGCDStartTimer() {
		return this.GCDStartTimer;
	}
	
	public long getGCDEndTimer() {
		return this.GCDEndTimer;
	}
	
	public boolean isCasting() {
		return this.spellCasting != null && Server.getLoopTickTimer() < this.endCastTimer;
	}
	
	public void cast(Spell spell, Unit target) {
		this.spellCasting = spell;
		this.castTarget = target;
		this.endCastTimer = Server.getLoopTickTimer()+spell.getCastTime();
	}
	
	public void startGCD(long timer) {
		this.GCDStartTimer = timer;
		this.GCDEndTimer = timer+GCD;
	}
	
	public int getStamina() {
		return this.stamina;
	}
	
	public void setStamina(int stamina) {
		this.stamina = Math.max(0, Math.min(stamina, this.maxStaminaEffective));
	}
	
	public int getMaxStaminaEffective() {
		return this.maxStaminaEffective;
	}
	
	public void setMaxStaminaEffective(int maxStamina) {
		this.maxStaminaEffective = maxStamina;
	}
	
	public int getGoldGained() {
		return this.goldGained;
	}
	
	public int getExpGained() {
		return this.expGained;
	}
	
	public int getMana() {
		return this.mana;
	}
	
	public void setMana(int mana) {
		this.mana = Math.max(0, mana);
	}
	
	public int getMaxManaEffective() {
		return this.maxManaEffective;
	}
	
	public void setMaxManaEffective(int maxMana) {
		this.maxManaEffective = maxMana;
	}
	
	public UnitType getUnitType() {
		return this.unitType;
	}
	
	public long getSpellCD(int spellID) {
		if(this.spellCDMap.containsKey(spellID)) {
			return this.spellCDMap.get(spellID);
		}
		return 0;
	}
	
	public void setSpellCD(int spellID, long timer) {
		this.spellCDMap.put(spellID, timer);
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public void setLevel(int level) {
		this.level = level;
		//CharacterMgr.setExperience(this.id, Player.getExpNeeded(this.level));
	}
	
	public int getid() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getArmorEffective() {
		return this.armorEffective;
	}
	 
	public void setArmorEffective(int armor) {
		this.armorEffective = armor;
	}
	
	public float getArmorPercentageReduction(int enemyLevel) {
		if(this.level <= 59) {
			return (this.armorEffective/(85*enemyLevel+this.armorEffective+400))*100;
		}
		return (this.armorEffective/(467.5f*enemyLevel+this.armorEffective-22167.5f))*100;
	}
	
	public int getCriticalEffective() {
		return this.criticalEffective;
	}
	
	public void setCriticalEffective(int critical) {
		this.criticalEffective = critical;
	}
	
	public int getStrengthEffective() {
		return this.strengthEffective;
	}
	
	public void setStrengthEffective(int strength) {
		this.strengthEffective = strength;
	}
	
	public Unit getTarget() {
		return this.target;
	}
	
	public void setTarget(Unit unit) {
		this.target = unit;
	}
}

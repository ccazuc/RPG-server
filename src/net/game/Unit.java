package net.game;

import net.game.manager.CharacterMgr;

public class Unit {

	public final static int GCD = 1500;
	protected int level;
	protected String name;
	protected int maxStamina;
	protected int stamina;
	protected int maxMana;
	protected int mana;
	protected int armor;
	protected int damage;
	protected int critical;
	protected int strength;
	protected UnitType unitType;
	protected int id;
	private int expGained;
	private int goldGained;
	private long GCDStartTimer;
	private long GCDEndTimer;
	
	public Unit(UnitType unitType, int id, int stamina, int maxStamina, int mana, int maxMana, int level, String name, int armor, int critical, int strength, int expGained, int goldGained) {
		this.stamina = stamina;
		this.maxStamina = maxStamina;
		this.mana = mana;
		this.maxMana = maxMana;
		this.stamina = 3500;       //debug
		this.maxStamina = 5000;
		this.mana = 6000;
		this.maxMana = 7500;
		setLevel(level);
		this.name = name;
		this.id = id;
		this.armor = armor;
		this.critical = critical;
		this.strength = strength;
		this.unitType = unitType;
		this.goldGained = goldGained;
		this.expGained = expGained;
	}
	
	public Unit(UnitType unitType) {
		this.unitType = unitType;
	}
	
	public long getGCDStartTimer() {
		return this.GCDStartTimer;
	}
	
	public long getGCDEndTimer() {
		return this.GCDEndTimer;
	}
	
	public void startGCD(long timer) {
		this.GCDStartTimer = timer;
		this.GCDEndTimer = timer+GCD;
	}
	
	public int getStamina() {
		return this.stamina;
	}
	
	public void setStamina(int stamina) {
		this.stamina = Math.max(0, Math.min(stamina, this.maxStamina));
	}
	
	public int getMaxStamina() {
		return this.maxStamina;
	}
	
	public void setMaxStamina(int maxStamina) {
		this.maxStamina = maxStamina;
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
	
	public int getMaxMana() {
		return this.maxMana;
	}
	
	public void setMaxMana(int maxMana) {
		this.maxMana = maxMana;
	}
	
	public UnitType getUnitType() {
		return this.unitType;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public void setLevel(int level) {
		this.level = level;
		CharacterMgr.setExperience(this.id, Player.getExpNeeded(this.level));
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
	
	public int getArmor() {
		return this.armor;
	}
	 
	public void setArmor(int armor) {
		this.armor = armor;
	}
	
	public int getCritical() {
		return this.critical;
	}
	
	public void setCritical(int critical) {
		this.critical = critical;
	}
	
	public int getStrength() {
		return this.strength;
	}
	
	public void setStrength(int strength) {
		this.strength = strength;
	}
	
}

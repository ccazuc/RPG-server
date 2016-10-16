package net.game.item.gem;

import net.game.item.Item;
import net.game.item.ItemType;

public class Gem extends Item {
	
	private int strength;
	private int stamina;
	private int mana;
	private int armor;
	private int critical;
	private GemColor color;

	public Gem(Gem gem) {
		super(gem.id, gem.sprite_id, gem.itemType, gem.name, gem.quality, gem.sellPrice, 1, 1);
		this.strength = gem.strength;
		this.critical = gem.critical;
		this.stamina = gem.stamina;
		this.armor = gem.armor;
		this.color = gem.color;
		this.mana = gem.mana;
	}
	
	public Gem(int id, String sprite_id, String name, int quality, GemColor color, int strength, int stamina, int armor, int mana, int critical, int sellPrice) {
		super(id, sprite_id, ItemType.GEM, name, quality, sellPrice, 1, 1);
		this.strength = strength;
		this.critical = critical;
		this.stamina = stamina;
		this.armor = armor;
		this.color = color;
		this.mana = mana;
	}
	
	public int getBonusValue(GemBonusType type) {
		if(type == GemBonusType.STAMINA) {
			return this.stamina;
		}
		if(type == GemBonusType.ARMOR) {
			return this.armor;
		}
		if(type == GemBonusType.CRITICAL) {
			return this.critical;
		}
		if(type == GemBonusType.MANA) {
			return this.mana;
		}
		if(type == GemBonusType.STRENGTH) {
			return this.strength;
		}
		return 0;
	}
	
	public int getStrength() {
		return this.strength;
	}
	
	public int getStamina() {
		return this.stamina;
	}
	
	public int getArmor() {
		return this.armor;
	}
	
	public int getCritical() {
		return this.critical;
	}
	
	public int getMana() {
		return this.mana;
	}
	
	public GemColor getColor() {
		return this.color;
	}
}
package net.game.item.potion;

import net.game.item.Item;
import net.game.item.ItemType;

public class Potion extends Item {

	private int doHeal;
	private int doMana;
	private int level;

	public Potion(Potion potion) {
		super(potion.id, potion.sprite_id, potion.itemType, potion.name, 1, potion.sellPrice, potion.maxStack, potion.amount);
		this.doHeal = potion.doHeal;
		this.doMana = potion.doMana;
		
	}
	
	public Potion(int id, String sprite_id, String name, int level, int doHeal, int doMana, int sellPrice, int amount) {
		super(id, sprite_id, ItemType.POTION, name, 1, sellPrice, 200, amount);
		this.doHeal = doHeal;
		this.doMana = doMana;
		this.level = level;
	}
	
	public int getPotionHeal() {
		return this.doHeal;
	}
	
	public int getPotionMana() {
		return this.doMana;
	}
	
	public int getLevel() {
		return this.level;
	}
}

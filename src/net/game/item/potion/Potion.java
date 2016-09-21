package net.game.item.potion;

import net.game.item.Item;
import net.game.item.ItemType;

public class Potion extends Item {

	protected int doHeal;
	protected int doMana;
	protected int level;

	public Potion(Potion potion) {
		super(potion.id, potion.sprite_id, potion.itemType, potion.name, 1, potion.sellPrice, potion.maxStack);
		this.doHeal = potion.doHeal;
		this.doMana = potion.doMana;
		
	}
	
	public Potion(int id, String sprite_id, String name, int level, int doHeal, int doMana, int sellPrice) {
		super(id, sprite_id, ItemType.POTION, name, 1, sellPrice, 200);
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

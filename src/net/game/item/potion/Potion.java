package net.game.item.potion;

import net.game.item.Item;
import net.game.item.ItemQuality;
import net.game.item.ItemType;

public class Potion extends Item {

	private final int doHeal;
	private final int doMana;

	public Potion(Potion potion) {
		super(potion.id, potion.sprite_id, potion.itemType, potion.name, potion.level, potion.quality.getValue(), potion.sellPrice, potion.maxStack, potion.amount);
		this.doHeal = potion.doHeal;
		this.doMana = potion.doMana;
		this.level = potion.level;
	}
	
	public Potion(int id, String sprite_id, String name, byte level, int doHeal, int doMana, int sellPrice, int amount) {
		super(id, sprite_id, ItemType.POTION, name, level, ItemQuality.COMMON.getValue(), sellPrice, 200, amount);
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
}

package net.game.item.gem;

import net.game.item.Item;
import net.game.item.ItemType;

public class Gem extends Item {
	
	private GemBonusType stat1Type;
	private int stat1Value;
	private GemBonusType stat2Type;
	private int stat2Value;
	private GemBonusType stat3Type;
	private int stat3Value;
	private GemColor color;

	public Gem(Gem gem) {
		super(gem.id, gem.sprite_id, gem.itemType, gem.name, gem.quality, gem.sellPrice, 1, 1);
		this.stat1Type = gem.stat1Type;
		this.stat1Value = gem.stat1Value;
		this.stat2Type = gem.stat2Type;
		this.stat2Value = gem.stat2Value;
		this.stat3Type = gem.stat3Type;
		this.stat3Value = gem.stat3Value;
		this.color = gem.color;
	}
	
	public Gem(int id, String sprite_id, String name, byte quality, GemColor color, int sellPrice, GemBonusType stat1Type, int stat1Value, GemBonusType stat2Type, int stat2Value, GemBonusType stat3Type, int stat3Value) {
		super(id, sprite_id, ItemType.GEM, name, quality, sellPrice, 1, 1);
		this.stat1Type = stat1Type;
		this.stat1Value = stat1Value;
		this.stat2Type = stat2Type;
		this.stat2Value = stat2Value;
		this.stat3Type = stat3Type;
		this.stat3Value = stat3Value;
		this.color = color;
	}
	
	public int getBonusValue(GemBonusType type) {
		int value = 0;
		if(this.stat1Type == type) {
			value+= this.stat1Value;
		}
		if(this.stat2Type == type) {
			value+= this.stat2Value;
		}
		if(this.stat3Type == type) {
			value+= this.stat3Value;
		}
		return value;
	}
	
	public GemColor getColor() {
		return this.color;
	}
	
	public GemBonusType getBonus1Type() {
		return this.stat1Type;
	}
	
	public int getBonus1Value() {
		return this.stat1Value;
	}
	
	public GemBonusType getBonus2Type() {
		return this.stat2Type;
	}
	
	public int getBonus2Value() {
		return this.stat2Value;
	}
	
	public GemBonusType getBonus3Type() {
		return this.stat3Type;
	}
	
	public int getBonus3Value() {
		return this.stat3Value;
	}
}
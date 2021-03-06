package net.game.item;

import net.game.item.bag.ContainerManager;
import net.game.item.gem.GemManager;
import net.game.item.potion.PotionManager;
import net.game.item.stuff.StuffManager;
import net.game.item.weapon.WeaponManager;
import net.utils.StringUtils;

public class Item implements Cloneable {

	protected ItemType itemType;
	protected String sprite_id;
	protected int sellPrice;
	protected int maxStack;
	protected String name;
	protected String lowerCaseName;
	protected ItemQuality quality;
	protected int id;
	protected int amount;
	protected byte level;
	
	public Item(int id, String sprite_id, ItemType itemType, String name, byte level, byte quality, int sellPrice, int maxStack, int amount) {
		this.sellPrice = sellPrice;
		this.sprite_id = sprite_id;
		this.maxStack = maxStack;
		this.itemType = itemType;
		this.level = level;
		this.quality = ItemQuality.values()[quality];
		this.name = name;
		this.lowerCaseName = StringUtils.toLowerCase(this.name);
		this.id = id;
		this.amount = amount;
	}
	
	public Item() {}

	public int getId() {
		return this.id;
	}
	
	public byte getLevel() {
		return this.level;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public int getAmount() {
		return this.amount;
	}
	
	public int getMaxStack() {
		return this.maxStack;
	}
	
	public String getSpriteId() {
		return this.sprite_id;
	}
	
	public boolean equals(Item item) {
		return item != null && item.getId() == this.id;
	}
	
	public String getStuffName() {
		return this.name;
	}
	
	public String getLowerCaseName() {
		return this.lowerCaseName;
	}
	
	public boolean isStackable() {
		return this.itemType == ItemType.ITEM || this.itemType == ItemType.POTION;
	}
	
	public ItemQuality getQuality() {
		return this.quality;
	}
	
	public int getSellPrice() {
		return this.sellPrice;
	}
	
	public ItemType getItemType() {
		return this.itemType;
	}
	
	public boolean isStuff() {
		return this.itemType == ItemType.STUFF;
	}
	
	public boolean isWeapon() {
		return this.itemType == ItemType.WEAPON;
	}
	
	public boolean isItem() {
		return this.itemType == ItemType.ITEM;
	}
	
	public boolean isPotion() {
		return this.itemType == ItemType.POTION;
	}
	
	public boolean isGem() {
		return this.itemType == ItemType.GEM;
	}
	
	public boolean isContainer() {
		return this.itemType == ItemType.CONTAINER;
	}
	
	public static Item getItemClone(int id) {
		if(ContainerManager.exists(id)) {
			return ContainerManager.getClone(id);
		}
		if(StuffManager.exists(id)) {
			return StuffManager.getClone(id);
		}
		if(WeaponManager.exists(id)) {
			return WeaponManager.getClone(id);
		}
		if(GemManager.exists(id)) {
			return GemManager.getClone(id);
		}
		if(PotionManager.exists(id)) {
			return PotionManager.getClone(id);
		}
		return null;
	}
	
	public static Item getItem(int id) {
		if(ContainerManager.exists(id)) {
			return ContainerManager.getContainer(id);
		}
		if(StuffManager.exists(id)) {
			return StuffManager.getStuff(id);
		}
		if(WeaponManager.exists(id)) {
			return WeaponManager.getWeapon(id);
		}
		if(GemManager.exists(id)) {
			return GemManager.getGem(id);
		}
		if(PotionManager.exists(id)) {
			return PotionManager.getPotion(id);
		}
		return null;
	}
	
	public static boolean exists(int id) {
		if(ContainerManager.exists(id)) {
			return true;
		}
		if(StuffManager.exists(id)) {
			return true;
		}
		if(WeaponManager.exists(id)) {
			return true;
		}
		if(GemManager.exists(id)) {
			return true;
		}
		if(PotionManager.exists(id)) {
			return true;
		}
		return false;
	}
}
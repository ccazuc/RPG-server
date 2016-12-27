package net.game.item.stuff;

import net.game.ClassType;
import net.game.Wear;
import net.game.item.Item;
import net.game.item.ItemType;
import net.game.item.gem.Gem;
import net.game.item.gem.GemBonusType;
import net.game.item.gem.GemColor;
import net.game.item.weapon.WeaponSlot;
import net.game.item.weapon.WeaponType;

public class Stuff extends Item {

	private Gem[] equippedGem = new Gem[3];
	private boolean gemBonusActivated;
	private GemBonusType gemBonusType;
	private ClassType[] classType;
	private WeaponType weaponType;
	private WeaponSlot weaponSlot;
	private int gemBonusValue;
	private GemColor color1;
	private GemColor color2;
	private GemColor color3;
	private StuffType type;
	private int critical;
	private int strength;
	private int stamina;
	private Wear wear;
	private int price;
	private int armor;
	private int level;
	private int mana;

	public Stuff(Stuff stuff) {
		super(stuff.id, stuff.sprite_id, stuff.itemType, stuff.name, stuff.quality, stuff.sellPrice, 1, 1);
		this.gemBonusValue = stuff.gemBonusValue;
		this.gemBonusType = stuff.gemBonusType;
		this.classType = stuff.classType;
		this.critical = stuff.critical;
		this.strength = stuff.strength;
		this.stamina = stuff.stamina;
		this.color1 = stuff.color1;
		this.color2 = stuff.color2;
		this.color3 = stuff.color3;
		this.level = stuff.level;
		this.armor = stuff.armor;
		this.type = stuff.type;
		this.wear = stuff.wear;
		this.mana = stuff.mana;
	}
	
	public Stuff(StuffType type, ClassType[] classType, String sprite_id, int id, String name, int quality, GemColor color1, GemColor color2, GemColor color3, GemBonusType gemBonusType, int gemBonusValue, int level, Wear wear, int critical, int strength, int stamina, int armor, int mana, int sellPrice) {
		super(id, sprite_id, ItemType.STUFF, name, quality, sellPrice, 1, 1);
		this.gemBonusValue = gemBonusValue;
		this.gemBonusType = gemBonusType;
		this.classType = classType;
		this.critical = critical;
		this.strength = strength;
		this.stamina = stamina;
		this.color1 = color1;
		this.color2 = color2;
		this.color3 = color3;
		this.level = level;
		this.armor = armor;
		this.type = type;
		this.wear = wear;
		this.mana = mana;
	}

	public Stuff(Stuff weapon, @SuppressWarnings("unused") int i) { //weapon constructor
		super(weapon.id, weapon.sprite_id, weapon.itemType, weapon.name, weapon.quality, weapon.sellPrice, 1, 1);
		this.gemBonusValue = weapon.gemBonusValue;
		this.gemBonusType = weapon.gemBonusType;
		this.weaponType = weapon.weaponType;
		this.weaponSlot = weapon.weaponSlot;
		this.classType = weapon.classType;
		this.critical = weapon.critical;
		this.strength = weapon.strength;
		this.stamina = weapon.stamina;
		this.color1 = weapon.color1;
		this.color2 = weapon.color2;
		this.color3 = weapon.color3;
		this.level = weapon.level;
		this.armor = weapon.armor;
		this.mana = weapon.mana;
	}
	
	public Stuff(int id, String name, String sprite_id, ClassType[] classType, WeaponType weaponType, WeaponSlot weaponSlot, int quality, GemColor color1, GemColor color2, GemColor color3, GemBonusType gemBonusType, int gemBonusValue, int level, int armor, int stamina, int mana, int critical, int strength, int sellPrice) {
		super(id, sprite_id, ItemType.WEAPON, name, quality, sellPrice, 1, 1);
		this.gemBonusValue = gemBonusValue;
		this.gemBonusType = gemBonusType;
		this.weaponType = weaponType;
		this.weaponSlot = weaponSlot;
		this.classType = classType;
		this.critical = critical;
		this.strength = strength;
		this.stamina = stamina;
		this.color1 = color1;
		this.color2 = color2;
		this.color3 = color3;
		this.level = level;
		this.armor = armor;
		this.mana = mana;
	}
	
	public boolean checkBonusTypeActivated() {
		if(this.color1 != GemColor.NONE || this.color2 != GemColor.NONE || this.color3 != GemColor.NONE) {
			if(this.equippedGem[0] != null && isBonusActivated(this.equippedGem[0].getColor(), this.color1)) {
				if(this.color2 != GemColor.NONE) {
					if(this.equippedGem[1] != null && isBonusActivated(this.equippedGem[1].getColor(), this.color2)) {
						if(this.color3 != GemColor.NONE) {
							if(this.equippedGem[2] != null && isBonusActivated(this.equippedGem[2].getColor(), this.color3)) {
								this.gemBonusActivated = true;
								return true;
							}
						}
						else {
							this.gemBonusActivated = true;
							return true;
						}
					}
				}
				else {
					this.gemBonusActivated = true;
					return true;
				}
			}
		}
		this.gemBonusActivated = false;
		return false;
	}
	
	public int getStatsFromGems(GemBonusType type) {
		int stats = 0;
		if(this.equippedGem[0] != null ) {
			stats+= this.equippedGem[0].getBonusValue(type);
		}
		if(this.equippedGem[1] != null) {
			stats+= this.equippedGem[1].getBonusValue(type);
		}
		if(this.equippedGem[2] != null) {
			stats+= this.equippedGem[2].getBonusValue(type);
		}
		return stats;
	}
	
	public boolean getGemBonusActivated() {
		return this.gemBonusActivated;
	}
	
	public WeaponSlot getWeaponSlot() {
		return this.weaponSlot;
	}

	public WeaponType getWeaponType() {
		return this.weaponType;
	}
	
	public GemColor getGemSlot1() {
		return this.color1;
	}
	
	public GemColor getGemSlot2() {
		return this.color2;
	}
	
	public GemColor getGemSlot3() {
		return this.color3;
	}
	
	public GemBonusType getGemBonusType() {
		return this.gemBonusType;
	}
	
	public int getGemBonusValue() {
		return this.gemBonusValue;
	}
	
	public void setEquippedGem(int slot, Gem gem) {
		this.equippedGem[slot] = gem;
		if(gem != null) {
			checkBonusTypeActivated();
		}
	}
	
	public Gem getEquippedGem(int slot) {
		return this.equippedGem[slot];
	}
	
	public int getEquippedGemID(int slot) {
		return this.equippedGem[slot] == null ? 0 : this.equippedGem[slot].getId();
	}
	public boolean isBonusActivated(GemColor gemColor, GemColor slotColor) {
		if(gemColor == GemColor.BLUE && slotColor == GemColor.BLUE) {
			return true;
		}
		if(gemColor == GemColor.YELLOW && slotColor == GemColor.YELLOW)  {
			return true;
		}
		if(gemColor == GemColor.RED && slotColor == GemColor.RED) {
			return true;
		}
		if(gemColor == GemColor.GREEN && (slotColor == GemColor.BLUE || slotColor == GemColor.YELLOW)) {
			return true;
		}
		if(gemColor == GemColor.ORANGE && (slotColor == GemColor.RED || slotColor == GemColor.YELLOW)) {
			return true;
		}
		if(gemColor == GemColor.PURPLE && (slotColor == GemColor.RED || slotColor == GemColor.BLUE)) {
			return true;
		}
		return false;
	}
	
	public String convTypeToString() {
		if(this.weaponType == WeaponType.BOW) {
			return "Bow";
		}
		if(this.weaponType == WeaponType.CROSSBOW) {
			return "Crossbow";
		}
		if(this.weaponType == WeaponType.DAGGER) {
			return "Dagger";
		}
		if(this.weaponType == WeaponType.FISTWEAPON) {
			return "Fist weapon";
		}
		if(this.weaponType == WeaponType.GUN) {
			return "Gun";
		}
		if(this.weaponType == WeaponType.ONEHANDEDAXE) {
			return "One handed axe";
		}
		if(this.weaponType == WeaponType.ONEHANDEDMACE) {
			return "One handed mace";
		}
		if(this.weaponType == WeaponType.ONEHANDEDSWORD) {
			return "One handed sword";
		}
		if(this.weaponType == WeaponType.POLEARM) {
			return "Polearm";
		}
		if(this.weaponType == WeaponType.STAFF) {
			return "Staff";
		}
		if(this.weaponType == WeaponType.THROWN) {
			return "Thrown";
		}
		if(this.weaponType == WeaponType.TWOHANDEDAXE) {
			return "Two handed axe";
		}
		if(this.weaponType == WeaponType.TWOHANDEDMACE) {
			return "Two handed mace";
		}
		if(this.weaponType == WeaponType.TWOHANDEDSWORD) {
			return "Two handed sword";
		}
		if(this.weaponType == WeaponType.WAND) {
			return "Wand";
		}
		return "";
	}
	
	public int getCritical() {
		return this.critical;
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
	
	public int getMana() {
		return this.mana;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public Wear getWear() {
		return this.wear;
	}
	
	public StuffType getType() {
		return this.type;
	}
	
	public ClassType[] getClassType() {
		return this.classType;
	}
	
	public ClassType getClassType(int i) {
		if(i >= 0 && i < this.classType.length) {
			return this.classType[i];
		}
		return null;
	}
	
	public String convStuffTypeToString() {
		if(this.type == StuffType.HEAD) {
			return "Head";
		}
		if(this.type == StuffType.NECKLACE) {
			return "Necklace";
		}
		if(this.type == StuffType.SHOULDERS) {
			return "Shoulders";
		}
		if(this.type == StuffType.BACK) {
			return "Back";
		}
		if(this.type == StuffType.CHEST) {
			return "Chest";
		}
		if(this.type == StuffType.WRISTS) {
			return "Wrists";
		}
		if(this.type == StuffType.GLOVES) {
			return "Gloves";
		}
		if(this.type == StuffType.BELT) {
			return "Belt";
		}
		if(this.type == StuffType.LEGGINGS) {
			return "Leggings";
		}
		if(this.type == StuffType.BOOTS) {
			return "Boots";
		}
		if(this.type == StuffType.RING) {
			return "Ring";
		}
		if(this.type == StuffType.TRINKET) {
			return "Trinket";
		}
		if(this.type == StuffType.MAINHAND) {
			return "MainHand";
		}
		if(this.type == StuffType.OFFHAND) {
			return "OffHand";
		}
		return "Ranged";
	}
	
	public String convClassTypeToString(int i) {
		if(i < this.classType.length) {
			if(this.classType[i] == ClassType.GUERRIER) {
				return "Warrior";
			}
			if(this.classType[i] == ClassType.HUNTER) {
				return "Hunter";
			}
			if(this.classType[i] == ClassType.MAGE) {
				return "Mage";
			}
			if(this.classType[i] == ClassType.PALADIN) {
				return "Paladin";
			}
			if(this.classType[i] == ClassType.PRIEST) {
				return "Priest";
			}
			if(this.classType[i] == ClassType.ROGUE) {
				return "Rogue";
			}
			if(this.classType[i] == ClassType.SHAMAN) {
				return "Shaman";
			}
			if(this.classType[i] == ClassType.DRUID) {
				return "Druid";
			}
			return "Warlock";
		}
		return null;
	}
	
	public String convWearToString() {
		if(this.wear == Wear.CLOTH) {
			return "Cloth";
		}
		if(this.wear == Wear.LEATHER) {
			return "Leather";
		}
		if(this.wear == Wear.MAIL) {
			return "Mail";
		}
		if(this.wear == Wear.PLATE) {
			return "Plate";
		}
		return "";
	}
	
	public boolean canEquipTo(ClassType type) {
		int i = 0;
		while(i < this.classType.length) {
			if(type == this.classType[i]) {
				return true;
			}
			i++;
		}
		return false;
	}
	
	public String convSlotToString() {
		if(this.weaponSlot == WeaponSlot.MAINHAND) {
			return "Main Hand";
		}
		if(this.weaponSlot == WeaponSlot.OFFHAND) {
			return "Off Hand";
		}
		if(this.weaponSlot == WeaponSlot.RANGED) {
			return "Ranged";
		}
 		return "";
 	}
	
	public String convWeaponTypeToString() {
		if(this.weaponType == WeaponType.BOW) {
			return "Bow";
		}
		if(this.weaponType == WeaponType.CROSSBOW) {
			return "Crossbow";
		}
		if(this.weaponType == WeaponType.DAGGER) {
			return "Dagger";
		}
		if(this.weaponType == WeaponType.FISTWEAPON) {
			return "Fist weapon";
		}
		if(this.weaponType == WeaponType.GUN) {
			return "Gun";
		}
		if(this.weaponType == WeaponType.ONEHANDEDAXE) {
			return "One handed axe";
		}
		if(this.weaponType == WeaponType.ONEHANDEDMACE) {
			return "One handed mace";
		}
		if(this.weaponType == WeaponType.ONEHANDEDSWORD) {
			return "One handed sword";
		}
		if(this.weaponType == WeaponType.POLEARM) {
			return "Polearm";
		}
		if(this.weaponType == WeaponType.STAFF) {
			return "Staff";
		}
		if(this.weaponType == WeaponType.THROWN) {
			return "Thrown";
		}
		if(this.weaponType == WeaponType.TWOHANDEDAXE) {
			return "Two handed axe";
		}
		if(this.weaponType == WeaponType.TWOHANDEDMACE) {
			return "Two handed mace";
		}
		if(this.weaponType == WeaponType.TWOHANDEDSWORD) {
			return "Two handed sword";
		}
		if(this.weaponType == WeaponType.WAND) {
			return "Wand";
		}
		return "";
	}
	
	public boolean isHead() {
		return this.type == StuffType.HEAD;
	}
	
	public boolean isNecklace() {
		return this.type == StuffType.NECKLACE;
	}
	
	public boolean isShoulders() {
		return this.type == StuffType.SHOULDERS;
	}
	
	public boolean isChest() {
		return this.type == StuffType.CHEST;
	}
	
	public boolean isBack() {
		return this.type == StuffType.BACK;
	}
	
	public boolean isWrists() {
		return this.type == StuffType.WRISTS;
	}
	
	public boolean isGloves() {
		return this.type == StuffType.GLOVES;
	}
	
	public boolean isBelt() {
		return this.type == StuffType.BELT;
	}
	
	public boolean isLeggings() {
		return this.type == StuffType.LEGGINGS;
	}
	
	public boolean isBoots() {
		return this.type == StuffType.BOOTS;
	}
	
	public boolean isRing() {
		return this.type == StuffType.RING;
	}
	
	public boolean isTrinket() {
		return this.type == StuffType.TRINKET;
	}
	
	public boolean isMainHand() {
		return this.weaponSlot == WeaponSlot.MAINHAND;
	}
	
	public boolean isOffHand() {
		return this.weaponSlot == WeaponSlot.OFFHAND;
	}
	
	public boolean isRanged() {
		return this.weaponSlot == WeaponSlot.RANGED;
	}
	
	public boolean equals(Stuff item) {
		return item != null && item.getId() == this.id;
	}
	
	public int getPrice() {
		return this.price;
	}
}

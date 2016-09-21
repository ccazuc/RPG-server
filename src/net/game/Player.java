package net.game;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;

import net.Server;
import net.connection.ConnectionManager;
import net.game.item.Item;
import net.game.item.ItemManager;
import net.game.item.ItemType;
import net.game.item.bag.Bag;
import net.game.item.stuff.Stuff;
import net.game.item.stuff.StuffManager;
import net.game.item.weapon.WeaponManager;
import net.game.item.weapon.WeaponType;
import net.game.profession.Profession;
import net.game.profession.ProfessionManager;
import net.game.shortcut.Shortcut;
import net.game.spell.Spell;
import net.game.spell.SpellBarManager;

public class Player {

	
	private ProfessionManager professionManager = new ProfessionManager();
	private SpellBarManager spellBarManager = new SpellBarManager();
	private ItemManager itemManager = new ItemManager();
	private ConnectionManager connectionManager;
	private Profession secondProfession;
	private Profession firstProfession;
	private WeaponType[] weaponType;
	private Spell[] spellUnlocked;
	private int numberYellowGem;
	private Shortcut[] shortcut;
	private Bag bag = new Bag();
	private int numberBlueGem;
	private Shortcut[] spells;
	private int defaultArmor;
	private ClassType classe;
	private int numberRedGem;
	private int characterId;
	private int maxStamina;
	private int goldGained;
	private boolean logged;
	private int accountId;
	private Stuff[] stuff;
	private int expGained;
	private int critical;
	private int strength;
	private int stamina;
	private float armor;
	private int maxMana;
	private int baseExp;
	private String name;
	private int level;
	private Wear wear;
	private int mana;
	private int gold;
	private int exp;
	//private int tailorExp;
	
	public Player(SocketChannel socket) {
		this.connectionManager = new ConnectionManager(this, socket);
	}
	
	public String getName() {
		return this.name;
	}
	
	public ConnectionManager getConnectionManager() {
		return this.connectionManager;
	}
	
	public boolean isLoggedIn() {
		return this.logged;
	}
	
	public int getCharacterId() {
		return this.characterId;
	}
	
	public void setCharacterId(int id) {
		this.characterId = id;
	}
	
	public int getAccountId() {
		return this.accountId;
	}
	
	public void setAccountId(int id) {
		this.accountId = id;
	}
	
	public void loadItemSQL() {
		try {
			this.itemManager.getBagItems(this);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setItemSQL() {
		try {
			this.itemManager.setBagItems(this);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void loadEquippedBagSQL() {
		try {
			this.itemManager.getEquippedBags(this);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setEquippedBagSQL() {
		try {
			this.itemManager.setEquippedBags(this);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void loadEquippedItemSQL() {
		try {
			this.itemManager.getEquippedItems(this);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setEquippedItemSQL() {
		try {
			this.itemManager.setEquippedItems(this);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void event() {
		if(this.bag.getBagChange()) {
			updateBagItem();
			this.bag.setBagChange(false);
		}
	}
	
	public void close() {
		this.connectionManager.getConnection().close();
		Server.removeNonLoggedPlayer(this);
		Server.removeLoggedPlayer(this);
	}
	
	/*private int checkNumberFreeSlotBag() {
		int i = 0;
		int number = 0;
		while(i < this.bag.getBag().length) {
			if(this.bag.getBag(i) == null) {
				number++;
			}
			i++;
		}
		return number;
	}*/

	public boolean canWearWeapon(Stuff stuff) {
		int i = 0;
		while(i < this.weaponType.length) {
			if(this.weaponType[i] == stuff.getWeaponType()) {
				return true;
			}
			i++;
		}
		return false;
	}
	
	public boolean addItem(Item item, int amount) throws SQLException {
		int i = 0;
		if(!item.isStackable()) {
			while(i < this.bag.getBag().length && amount > 0) {
				if(this.bag.getBag(i) == null) {
					this.bag.setBag(i, item);
					this.bag.setBagChange(true);
					amount --;
				}
				i++;
			}
			this.itemManager.setBagItems(this);
		}
		else {
			while(i < this.bag.getBag().length) {
				if(this.bag.getBag(i) != null && this.bag.getBag(i).equals(item)) {
					this.bag.setBag(i, item, this.bag.getNumberBagItem(this.bag.getBag(i))+amount);
					this.bag.setBagChange(true);
					this.itemManager.setBagItems(this);
					return true;
				}
				i++;
			}
			i = 0;
			while(i < this.bag.getBag().length) {
				if(this.bag.getBag(i) == null) {
					this.bag.setBag(i, item, amount);
					this.bag.setBagChange(true);
					this.itemManager.setBagItems(this);
					return true;
				}
				i++;
			}
		}
		return false;
	}
	
	public void updateBagItem() {
		this.bag.getItemList().clear();
		int i = 0;
		while(i < this.bag.getBag().length) {
			if(this.bag.getBag(i) != null) {
				if(this.bag.getItemList().containsKey(this.bag.getBag(i).getId())) {
					if(this.bag.getBag(i).isStackable()) {
						this.bag.getItemList().put(this.bag.getBag(i).getId(), this.bag.getNumberBagItem(this.bag.getBag(i))+this.bag.getItemList().get(this.bag.getBag(i).getId()));
					}
					else {
						this.bag.getItemList().put(this.bag.getBag(i).getId(), this.bag.getItemList().get(this.bag.getBag(i).getId())+1);
					}
				}
				else {
					if(this.bag.getBag(i).isStackable()) {
						this.bag.getItemList().put(this.bag.getBag(i).getId(), this.bag.getNumberBagItem(this.bag.getBag(i)));
					}
					else {
						this.bag.getItemList().put(this.bag.getBag(i).getId(), 1);
					}
				}
			}
			i++;
		}
	}
	
	public void addMultipleUnstackableItem(int id, int number) throws SQLException {
		int i = 0;
		ItemType type;
		if(WeaponManager.exists(id) || StuffManager.exists(id)) {
			if(WeaponManager.exists(id)) {
				type = ItemType.WEAPON;
			}
			else {
				type = ItemType.STUFF;
			}
			while(i < this.bag.getBag().length && number > 0) {
				if(this.bag.getBag(i) == null) {
					if(type == ItemType.WEAPON) {
						this.bag.setBag(i, WeaponManager.getClone(id));
					}
					else {
						this.bag.setBag(i, StuffManager.getClone(id));
					}
					this.bag.setBagChange(true);
					number--;
				}
				
				i++;
			}
			this.itemManager.setBagItems(this);
		}
	}
	
	public void deleteItem(Item item, int amount) throws SQLException {
		int i = 0;
		if(!item.isStackable()) {
			while(i < this.bag.getBag().length && amount > 0) {
				if(this.bag.getBag(i) != null && this.bag.getBag(i).equals(item)) {
					this.bag.setBag(i, null);
					this.bag.setBagChange(true);
					amount--;
				}
				i++;
			}
			this.itemManager.setBagItems(this);
		}
		else {
			while(i < this.bag.getBag().length && amount > 0) {
				if(this.bag.getBag(i) != null && this.bag.getBag(i).equals(item)) {
					int temp = amount;
					amount = amount-this.bag.getNumberBagItem(this.bag.getBag(i));
					this.bag.setBag(i, this.bag.getBag(i), Math.max(0, this.bag.getNumberBagItem(this.bag.getBag(i))-temp));
					this.bag.setBagChange(true);
				}
				i++;
			}
			this.itemManager.setBagItems(this);
		}
	}
	
	public void setEquippedBag(int i, Bag bag) {
		if(i < this.bag.getEquippedBag().length) {
			if(bag != null) {
				int length = this.bag.getBag().length;
				Item[] tempBag = this.bag.getBag().clone();
				if(this.bag.getEquippedBag(i) != null) {
					int tempBagSize = this.bag.getEquippedBagSize(i);
					this.bag.setEquippedBag(i, bag);
					if(tempBagSize >= this.bag.getEquippedBagSize(i)) {
						this.bag.setBag(new Item[length+(tempBagSize-this.bag.getEquippedBagSize(i))]);
					}
					else {
						this.bag.setBag(new Item[length+(this.bag.getEquippedBagSize(i)-tempBagSize)]);
					}
				}
				else {
					this.bag.setEquippedBag(i, bag);
					this.bag.setBag(new Item[length+this.bag.getEquippedBagSize(i)]);
				}
				int j = 0;
				while(j < tempBag.length && j < this.bag.getBag().length) {
					this.bag.setBag(j, tempBag[j]);
					j++;
				}
			}
			else {
				this.bag.setEquippedBag(i, bag);
			}
		}
	}
	
	public void setFirstProfession(Profession profession) {
		this.firstProfession = profession;
	}
	
	public Profession getFirstProfession() {
		return this.firstProfession;
	}
	
	public Profession getSecondProfession() {
		return this.secondProfession;
	}
	
	public void setSecondProfession(Profession profession) {
		this.secondProfession = profession;
	}
	
	public void setStuffArmor(int armor) {
		this.armor+= armor;
	}

	public void setStuffStrength(int strengh) {
		this.strength+= strengh;
	}
	
	public void setStuffStamina(int stamina) {
		this.maxStamina+= stamina;
		this.stamina+= stamina;
	}
	
	public void setStuffCritical(int critical) {
		this.critical+= critical;
	}
	
	public void setStuffMana(int mana) {
		this.maxMana+= mana;
		this.mana+= mana;
	}
	
	public void setNumberRedGem(int nb) {
		this.numberRedGem = nb;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public int getNumberRedGem() {
		return this.numberRedGem;
	}
	
	public void setNumberBlueGem(int nb) {
		this.numberBlueGem = nb;
	}
	
	public int getNumberBlueGem() {
		return this.numberBlueGem;
	}
	
	public void setNumberYellowGem(int nb) {
		this.numberYellowGem = nb;
	}
	
	public int getNumberYellowGem() {
		return this.numberYellowGem;
	}
	
	public float getArmor() {
		return this.armor;
	}
	
	public void setStamina(double d) {
		this.stamina = (int)Math.max(d, 0);
	}
	
	public int getMaxStamina() {
		return this.maxStamina;
	}
	
	public int getStamina() {
		return this.stamina;
	}
	
	public Wear getWear() {
		return this.wear;
	}
	
	public int getCritical() {
		return this.critical;
	}
	
	public int getStrength() {
		return this.strength;
	}
	
	public void setMana(int mana) {
		this.mana = Math.max(mana, 0);
	}
	
	public int getMana() {
		return this.mana;
	}
	
	public int getMaxMana() {
		return this.maxMana;
	}

	public Shortcut[] getSpells() {
		return this.spells;
	}

	public Shortcut getSpells(int i) {
		return this.spells[i];
	}
	
	public void setSpells(int i, Shortcut spell) {
		this.spells[i] = spell;
	}
	
	public Spell[] getSpellUnlocked() {
		return this.spellUnlocked;
	}
	
	public Spell getSpellUnlocked(int i) {
		return this.spellUnlocked[i];
	}
	
	public Stuff[] getStuff() {
		return this.stuff;
	}
	
	public Stuff getStuff(int i) {
		return this.stuff[i];
	}
	
	public void setStuff(int i, Item tempItem) {
		if(tempItem == null) {
			this.stuff[i] = null;
		}
		else if(tempItem.getItemType() == ItemType.STUFF || tempItem.getItemType() == ItemType.WEAPON) {
			this.stuff[i] = (Stuff)tempItem;
		}
	}
	
	public Shortcut getShortcut(int i) {
		return this.shortcut[i];
	}
	
	public Shortcut[] getShortcut() {
		return this.shortcut;
	}

	public void setSpellUnlocked(int i, Spell spell) {
		this.spellUnlocked[i] = spell;
	}
	
	public Bag getBag() {
		return this.bag;
	}
	
	public int getExp() {
		return this.exp;
	}
	
	public int getBaseExp() {
		return this.baseExp;
	}
	
	public int getExpGained() {
		return this.expGained;
	}
	
	public void setArmor(float number) {
		this.armor = (Math.round(100*(this.armor+number))/100.f);
	}
	
	public int getDefaultArmor() {
		return this.defaultArmor;
	}
	
	public int getGold() {
		return this.gold;
	}
	
	public int getGoldGained() {
		return this.goldGained;
	}
	
	public void setExp(int baseExp, int expGained ) {
		this.exp = baseExp+expGained;
	}
	
	public void setMaxStamina(int stamina) {
		this.maxStamina = stamina;
	}
	
	public void setMaxMana(int mana) {
		this.maxMana = mana;
	}
	
	public int getNumberItem(Item item) {
		if(item.isStackable() && this.bag.getNumberStack().containsKey(item)) {
			return this.bag.getNumberStack().get(item);
		}
		return 0;
	}
	
	public void setNumberItem(Item item, int number) {
		if(item.isStackable()) {
			this.bag.getNumberStack().put(item, number);
		}
	}

	public boolean canEquipStuff(Stuff stuff) {
		if(this.level >= stuff.getLevel() && canWear(stuff) && stuff.canEquipTo(this.classe)) {
			return true;
		}
		return false;
	}
	
	public boolean canEquipWeapon(Stuff weapon) {
		if(this.level >= weapon.getLevel() && canWearWeapon(weapon) && weapon.canEquipTo(this.classe)) {
			return true;
		}
		return false;
	}

	public boolean canWear(Stuff stuff) {
		if(stuff != null) {
			if(this.wear == Wear.PLATE) {
				return true;
			}
			if(this.wear == Wear.MAIL) {
				if(stuff.getWear() == Wear.PLATE) {
					return false;
				}
				return true;
			}
			if(this.wear == Wear.LEATHER) {
				if(stuff.getWear() == Wear.PLATE || stuff.getWear() == Wear.MAIL) {
					return false;
				}
				return true;
			}
			if(this.wear == Wear.CLOTH) {
				if(stuff.getWear() == Wear.CLOTH || stuff.getWear() == Wear.NONE) {
					return true;
				}
				return false;
			}
		}
		return false;
	}
	
	public WeaponType[] getWeaponType() {
		return this.weaponType;
	}
	
	public WeaponType getweaponType(int i) {
		if(i < this.weaponType.length) {
			return this.weaponType[i];
		}
		return null;
	}
	
	public void loadSpellBar() throws SQLException {
		this.spellBarManager.loadSpellBar(this);
	}
}

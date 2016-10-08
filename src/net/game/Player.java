package net.game;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.ArrayList;

import net.Server;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.item.Item;
import net.game.item.ItemManager;
import net.game.item.ItemType;
import net.game.item.bag.Bag;
import net.game.item.bag.Container;
import net.game.item.gem.Gem;
import net.game.item.potion.Potion;
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
	private CharacterManager characterManager = new CharacterManager();
	private ConnectionManager connectionManager;
	private ArrayList<Integer> itemSentToClient = new ArrayList<Integer>();
	private Profession secondProfession;
	private Profession firstProfession;
	private WeaponType[] weaponType;
	private Spell[] spellUnlocked;
	private int numberYellowGem;
	private Shortcut[] shortcut;
	private Bag bag = new Bag();
	private boolean pingStatus;
	private int numberBlueGem;
	private Shortcut[] spells;
	private int defaultArmor;
	private ClassType classe;
	private int numberRedGem;
	private int characterId;
	private int accountRank;
	private long pingTimer;
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
	private String name;
	private int level;
	private Race race;
	private Wear wear;
	private int mana;
	private int gold;
	private int exp;
	//private int tailorExp;

	private final static String warrior = "Warrior";
	private final static String hunter = "Hunter";
	private final static String mage = "Mage";
	private final static String paladin = "Paladin";
	private final static String priest = "Priest";
	private final static String rogue = "Rogue";
	private final static String shaman = "Shaman";
	private final static String warlock = "Warlock";
	private final static String druid = "Warlock";
	
	public Player(SocketChannel socket) {
		this.connectionManager = new ConnectionManager(this, socket);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getIpAdresse() {
		return this.connectionManager.getIpAdress();
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
	
	public void addItemSentToClient(int id) {
		this.itemSentToClient.add(id);
	}
	
	public void addItemSentToClient(Item item) {
		this.itemSentToClient.add(item.getId());
	}
	
	public boolean itemHasBeenSendToClient(int id) {
		int i = 0;
		while(i < this.itemSentToClient.size()) {
			if(this.itemSentToClient.get(i) == id) {
				return true;
			}
			i++;
		}
		return false;
	}
	
	public boolean itemHasBeenSendToClient(Item item) {
		int i = 0;
		while(i < this.itemSentToClient.size()) {
			if(this.itemSentToClient.get(i) == item.getId()) {
				return true;
			}
			i++;
		}
		return false;
	}
	
	public void sendStats() {
		this.connectionManager.getConnection().writeByte(PacketID.LOAD_STATS);
		this.connectionManager.getConnection().writeInt(this.exp);
		this.connectionManager.getConnection().writeInt(this.gold);
	}
	
	public void initTable() {
		this.spells = new Shortcut[36];
		this.stuff = new Stuff[19];
		this.spellUnlocked = new Spell[19];
	}
	
	public void loadBagItemSQL() {
		try {
			this.itemManager.getBagItems(this);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setBagItemSQL() {
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
	
	public void loadCharacterInfoSQL() {
		try {
			this.characterManager.loadCharacterInfo(this);
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
		if(amount == 1) {
			return addSingleItem(item, amount);
		}
		else if(amount > 1) {
			if(item.isStackable()) {
				return addSingleItem(item, amount);
			}
			else {
				return addMultipleUnstackableItem(item, amount);
			}
		}
		return false;
	}
	
	private boolean addSingleItem(Item item, int amount) throws SQLException {
		int i = 0;
		boolean returns = false;
		if(!item.isStackable()) {
			while(i < this.bag.getBag().length && amount > 0) {
				if(this.bag.getBag(i) == null) {
					this.bag.setBag(i, item);
					this.bag.setBagChange(true);
					amount --;
					returns = true;
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
		return returns;
	}
	
	@SuppressWarnings("unused")
	private boolean addMultipleUnstackableItem(int id, int number) throws SQLException {
		int i = 0;
		boolean returns = false;
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
					returns = true;
					this.bag.setBagChange(true);
					number--;
				}
				
				i++;
			}
			this.itemManager.setBagItems(this);
		}
		return returns;
	}
	
	private boolean addMultipleUnstackableItem(Item item, int number) throws SQLException {
		int i = 0;
		boolean returns = false;
		while(i < this.bag.getBag().length && number > 0) {
			if(this.bag.getBag(i) == null) {
				if(item.isStuff() || item.isWeapon()) {
					this.bag.setBag(i, new Stuff((Stuff)item));
					number--;
					this.bag.setBagChange(true);
					returns = true;
				}
				else if(item.isGem()) {
					this.bag.setBag(i, new Gem((Gem)item));
					number--;
					this.bag.setBagChange(true);
					returns = true;
				}
				else if(item.isPotion()) {
					this.bag.setBag(i, new Potion((Potion)item));
					number--;
					this.bag.setBagChange(true);
					returns = true;
				}
			}
			i++;
		}
		this.itemManager.setBagItems(this);
		return returns;
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
	
	public void setEquippedBag(int i, Container bag) {
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
	
	public void setAccountRank(int rank) {
		this.accountRank = rank;
	}
	
	public int getAccountRank() {
		return this.accountRank;
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
	
	public void setExperience(int exp) {
		this.exp = exp;
		this.level = getLevel(this.exp);
	}
	
	public int getExperience() {
		return this.exp;
	}
	
	public int getGold() {
		return this.gold;
	}
	
	public void setGold(int gold) {
		this.gold = gold;
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
		else if(tempItem.isStuff() || tempItem.isWeapon()) {
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
	
	public int getExpGained() {
		return this.expGained;
	}
	
	public void setArmor(float number) {
		this.armor = (Math.round(100*(this.armor+number))/100.f);
	}
	
	public int getDefaultArmor() {
		return this.defaultArmor;
	}
	
	public int getGoldGained() {
		return this.goldGained;
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
	
	public ClassType getClasse() {
		return this.classe;
	}
	
	public void setClasse(ClassType classe) {
		this.classe = classe;
	}
	
	public Race getRace() {
		return this.race;
	}
	
	public void setRace(Race race) {
		this.race = race;
	}
	
	public void loadSpellBar() throws SQLException {
		this.spellBarManager.loadSpellBar(this);
	}

	public boolean getPingStatus() {
		return this.pingStatus;
	}

	public void setPingStatus(boolean pingStatus) {
		this.pingStatus = pingStatus;
	}

	public long getPingTimer() {
		return this.pingTimer;
	}

	public void setPingTimer(long pingTimer) {
		this.pingTimer = pingTimer;
	}
	
	public static String convClassTypeToString(ClassType type) {
		if(type == ClassType.DRUID) {
			return druid;
		}
		if(type == ClassType.GUERRIER) {
			return warrior;
		}
		if(type == ClassType.HUNTER) {
			return hunter;
		}
		if(type == ClassType.MAGE) {
			return mage;
		}
		if(type == ClassType.PALADIN) {
			return paladin;
		}
		if(type == ClassType.PRIEST) {
			return priest;
		}
		if(type == ClassType.ROGUE) {
			return rogue;
		}
		if(type == ClassType.SHAMAN) {
			return shaman;
		}
		if(type == ClassType.WARLOCK) {
			return warlock;
		}
		return null;
	}
	
	public static int getLevel(int exp) {
		if(exp <= 400) {
			return 1;
		}
		else if(exp <= 900) {
			return 2;
		}
		else if(exp <= 1400) {
			return 3;
		}
		else if(exp <= 2100) {
			return 4;
		}
		else if(exp <= 2800) {
			return 5;
		}
		else if(exp <= 3600) {
			return 6;
		}
		else if(exp <= 4500) {
			return 7;
		}
		else if(exp <= 5400) {
			return 8;
		}
		else if(exp <= 6500) {
			return 9;
		}
		else if(exp <= 7600) {
			return 10;
		}
		else if(exp <= 8700) {
			return 11;
		}
		else if(exp <= 9800) {
			return 12;
		}
		else if(exp <= 11000) {
			return 13;
		}
		else if(exp <= 12300) {
			return 14;
		}
		else if(exp <= 13600) {
			return 15;
		}
		else if(exp <= 15000) {
			return 16;
		}
		else if(exp <= 16400) {
			return 17;
		}
		else if(exp <= 17800) {
			return 18;
		}
		else if(exp <= 19300) {
			return 19;
		}
		else if(exp <= 20800) {
			return 20;
		}
		else if(exp <= 22400) {
			return 21;
		}
		else if(exp <= 24000) {
			return 22;
		}
		else if(exp <= 25500) {
			return 23;
		}
		else if(exp <= 27200) {
			return 24;
		}
		else if(exp <= 28900) {
			return 25;
		}
		else if(exp <= 30500) {
			return 26;
		}
		else if(exp <= 32200) {
			return 27;
		}
		else if(exp <= 33900) {
			return 28;
		}
		else if(exp <= 36300) {
			return 29;
		}
		else if(exp <= 38800) {
			return 30;
		}
		else if(exp <= 41600) {
			return 31;
		}
		else if(exp <= 44600) {
			return 32;
		}
		else if(exp <= 48000) {
			return 33;
		}
		else if(exp <= 51400) {
			return 34;
		}
		else if(exp <= 55000) {
			return 35;
		}
		else if(exp <= 58700) {
			return 36;
		}
		else if(exp <= 62400) {
			return 37;
		}
		else if(exp <= 66200) {
			return 38;
		}
		else if(exp <= 70200) {
			return 39;
		}
		else if(exp <= 74300) {
			return 40;
		}
		else if(exp <= 78500) {
			return 41;
		}
		else if(exp <= 82800) {
			return 42;
		}
		else if(exp <= 87100) {
			return 43;
		}
		else if(exp <= 91600) {
			return 44;
		}
		else if(exp <= 96300) {
			return 45;
		}
		else if(exp <= 101000) {
			return 46;
		}
		else if(exp <= 105800) {
			return 47;
		}
		else if(exp <= 110700) {
			return 48;
		}
		else if(exp <= 115700) {
			return 49;
		}
		else if(exp <= 120900) {
			return 50;
		}
		else if(exp <= 126100) {
			return 51;
		}
		else if(exp <= 131500) {
			return 52;
		}
		else if(exp <= 137000) {
			return 53;
		}
		else if(exp <= 142500) {
			return 54;
		}
		else if(exp <= 148200) {
			return 55;
		}
		else if(exp <= 154000) {
			return 56;
		}
		else if(exp <= 159900) {
			return 57;
		}
		else if(exp <= 165800) {
			return 58;
		}
		else if(exp <= 172000) {
			return 59;
		}
		else if(exp <= 290000) {
			return 60;
		}
		else if(exp <= 317000) {
			return 61;
		}
		else if(exp <= 349000) {
			return 62;
		}
		else if(exp <= 386000) {
			return 63;
		}
		else if(exp <= 428000) {
			return 64;
		}
		else if(exp <= 475000) {
			return 65;
		}
		else if(exp <= 527000) {
			return 66;
		}
		else if(exp <= 585000) {
			return 67;
		}
		else if(exp <= 648000) {
			return 68;
		}
		else if(exp <= 717000) {
			return 69;
		}
		return 70;
	}
}

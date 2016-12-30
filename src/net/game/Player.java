package net.game;

import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import net.Server;
import net.command.item.CommandSetItem;
import net.command.player.CommandFriend;
import net.command.player.CommandGuild;
import net.command.player.CommandLogoutCharacter;
import net.command.player.CommandParty;
import net.command.player.CommandTrade;
import net.connection.Connection;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.guild.Guild;
import net.game.guild.GuildMgr;
import net.game.item.DragItem;
import net.game.item.Item;
import net.game.item.ItemManager;
import net.game.item.bag.Bag;
import net.game.item.bag.Container;
import net.game.item.gem.Gem;
import net.game.item.gem.GemBonusType;
import net.game.item.potion.Potion;
import net.game.item.stuff.Stuff;
import net.game.item.weapon.WeaponType;
import net.game.manager.CharacterMgr;
import net.game.manager.FriendMgr;
import net.game.profession.Profession;
import net.game.shortcut.Shortcut;
import net.game.spell.Spell;
import net.game.spell.SpellBarManager;
import net.game.spell.SpellManager;

public class Player extends Unit {

	private Unit target = new Unit(UnitType.NPC, 100, 10000, 10000, 3000, 3000, 1, "", 0, 0, 0, 150, 150);
	//private ProfessionManager professionManager = new ProfessionManager();
	private ArrayList<Integer> itemSentToClient = new ArrayList<Integer>();
	private SpellBarManager spellBarManager = new SpellBarManager();
	private GuildMgr guildManager = new GuildMgr(this);
	private final static int MAXIMUM_AMOUNT_FRIENDS = 20; 
	private ItemManager itemManager = new ItemManager();
	private HashMap<Integer, Spell> spellUnlocked;
	private ArrayList<Integer> playerWhoAreFriend;
	private ConnectionManager connectionManager;
	private ArrayList<Integer> friendList;
	private ArrayList<Integer> ignoreList;
	private Profession secondProfession;
	private Profession firstProfession;
	private AccountRank accountRank;
	private WeaponType[] weaponType;
	private boolean hasInitParty;
	private int numberYellowGem;
	private Shortcut[] shortcut;
	private Bag bag = new Bag();
	private String accountName;
	private boolean pingStatus;
	private Player playerTrade;
	private Player playerParty;
	private int numberBlueGem;
	private Shortcut[] spells;
	private boolean isOnline;
	private int guildRequest;
	private int defaultArmor;
	private ClassType classe;
	private int numberRedGem;
	private int characterId;
	private long pingTimer;
	private boolean logged;
	private boolean isGMOn;
	private int accountId;
	private Stuff[] stuff;
	private Guild guild;
	private float armor;
	private Party party;
	private Trade trade;
	private Race race;
	private Wear wear;
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
		super(UnitType.PLAYER);
		this.connectionManager = new ConnectionManager(this, socket);
		this.stamina = 5000;
		this.maxStamina = 8000;
		this.mana = 8000;
		this.maxMana = 11000;
	}
	
	public String getIpAdress() {
		return this.connectionManager.getIpAdress();
	}
	
	public ConnectionManager getConnectionManager() {
		return this.connectionManager;
	}
	
	public HashMap<Integer, Spell> getSpellUnlocked() {
		return this.spellUnlocked;
	}
	
	public Spell getSpellUnlocked(int id) {
		return this.spellUnlocked.get(id);
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
	
	public Trade getTrade() {
		return this.trade;
	}
	
	public void setTrade(Trade trade) {
		this.trade = trade;
	}
	
	public void initTrade(Player self, Player other) {
		this.trade = new Trade(self, other);
	}
	
	public int getAccountId() {
		return this.accountId;
	}
	
	public void setAccountId(int id) {
		this.accountId = id;
	}
	
	public String getAccountName() {
		return this.accountName;
	}
	
	public void setAccountName(String name) {
		this.accountName = name;
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
	
	public void setOffline() {
		this.isOnline = false;
	}
	
	public void setOnline() {
		this.isOnline = true;
	}
	
	public boolean isOnline() {
		return this.isOnline;
	}
	public void sendStats() {
		this.connectionManager.getConnection().startPacket();
		this.connectionManager.getConnection().writeShort(PacketID.LOAD_STATS);
		this.connectionManager.getConnection().writeInt(this.characterId);
		this.connectionManager.getConnection().writeInt(this.exp);
		this.connectionManager.getConnection().writeInt(this.gold);
		this.connectionManager.getConnection().writeInt(this.accountRank.getValue());
		this.connectionManager.getConnection().endPacket();
		this.connectionManager.getConnection().send();
	}
	
	public void initTable() {
		this.spells = new Shortcut[36];
		this.stuff = new Stuff[19];
		this.spellUnlocked = new HashMap<Integer, Spell>();
		this.playerWhoAreFriend = new ArrayList<Integer>();
		this.friendList = new ArrayList<Integer>();
		this.ignoreList = new ArrayList<Integer>();
		this.bag = new Bag();
	}
	
	public void loadGuild() {
		try {
			this.guildManager.loadGuild();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
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
		this.itemManager.setBagItems(this);
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
			CharacterMgr.loadCharacterInfo(this);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void loadFriendList() {
		try {
			CharacterMgr.loadFriendList(this);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void loadSpellUnlocked() {
		try {
			CharacterMgr.loadSpellUnlocked(this);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateLastLoginTimer() {
		CharacterMgr.updateLastLoginTimer(this);
	}
	
	public void event() {
		if(this.bag.getBagChange()) {
			updateBagItem();
		}
	}
	
	public void notifyFriendOnline() {
		int i = 0;
		if(FriendMgr.containsKey(this.characterId)) {
			int length = FriendMgr.getFriendMap().get(this.characterId).size();
			while(i < length) {
				if(Server.getInGamePlayerList().containsKey(FriendMgr.getFriendMap().get(this.characterId).get(i))) {
					CommandFriend.notifyFriendOnline(Server.getInGameCharacter(FriendMgr.getFriendMap().get(this.characterId).get(i)), this);
				}
				i++;
			}
		}
	}
	
	public void notifyFriendOffline() {
		int i = 0;
		if(FriendMgr.containsKey(this.characterId)) {
			int length = FriendMgr.getFriendMap().get(this.characterId).size();
			while(i < length) {
				if(Server.getInGamePlayerList().containsKey(FriendMgr.getFriendMap().get(this.characterId).get(i))) {
					CommandFriend.notifyFriendOffline(Server.getInGameCharacter(FriendMgr.getFriendMap().get(this.characterId).get(i)), this);
				}
				i++;
			}
		}
	}
	
	public boolean isFriendWith(Player player) {
		int i = 0;
		while(i < this.friendList.size()) {
			if(this.friendList.get(i) == player.getCharacterId()) {
				return true;
			}
			i++;
		}
		return false;
	}
	
	public boolean isFriendWith(int character_id) {
		int i = 0;
		while(i < this.friendList.size()) {
			if(this.friendList.get(i) == character_id) {
				return true;
			}
			i++;
		}
		return false;
	}
	
	public int getItemBagSlot(Item item) {
		int i = 0;
		while(i < this.bag.getBag().length) {
			if(this.bag.getBag(i) == item) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public int getItemInventorySlot(Item item) {
		int i = 0;
		while(i < this.stuff.length) {
			if(this.stuff[i] == item) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public void close() {
		//long timer = System.nanoTime();
		if(Server.getInGamePlayerList().containsKey(this.characterId)) {
			notifyFriendOffline();
			if(this.guild != null) {
				CommandGuild.notifyOfflinePlayer(this);
			}
			FriendMgr.getFriendMap().remove(this.getCharacterId());
		}
		FriendMgr.getFriendMap().remove(this.characterId);
		CommandLogoutCharacter.setPlayerOfflineInDB(this);
		if(this.trade != null || this.playerTrade != null) {
			CommandTrade.closeTrade(this);
		}
		if(this.party != null || this.playerParty != null) {
			CommandParty.leaveParty(this);
		}
		this.connectionManager.getConnection().close();
		Server.removeNonLoggedPlayer(this);
		Server.removeLoggedPlayer(this);
		Server.removeInGamePlayer(this);
		//System.out.println("Player close took "+(System.nanoTime()-timer)/1000+" �s.");
	}
	
	public void updateBagItem() {
		this.bag.getItemList().clear();
		int i = 0;
		while(i < this.bag.getBag().length) {
			if(this.bag.getBag(i) != null) {
				if(this.bag.getItemList().containsKey(this.bag.getBag(i).getId())) {
					if(this.bag.getBag(i).isStackable()) {
						this.bag.getItemList().put(this.bag.getBag(i).getId(), this.bag.getBag(i).getAmount()+this.bag.getItemList().get(this.bag.getBag(i).getId()));
					}
					else {
						this.bag.getItemList().put(this.bag.getBag(i).getId(), this.bag.getItemList().get(this.bag.getBag(i).getId())+1);
					}
				}
				else {
					if(this.bag.getBag(i).isStackable()) {
						this.bag.getItemList().put(this.bag.getBag(i).getId(), this.bag.getBag(i).getAmount());
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
	
	/*public ArrayList<Player> getFriendList() {
		return this.friendList;
	}
	
	public ArrayList<Player> getIgnoreList() {
		return this.ignoreList;
	}*/
	
	public ArrayList<Integer> getFriendList() {
		return this.friendList;
	}
	
	public ArrayList<Integer> getIgnoreList() {
		return this.ignoreList;
	}
	
	public ArrayList<Integer> getPlayerWhoAreFriend() {
		return this.playerWhoAreFriend;
	}
	
	public boolean addFriend(int id) {
		if(this.friendList.size() < MAXIMUM_AMOUNT_FRIENDS) {
			this.friendList.add(id);
			return true;
		}
		return false;
	}
	
	public boolean removeFriend(int id) {
		int i = 0;
		while(i < this.friendList.size()) {
			if(this.friendList.get(i) == id) {
				this.friendList.remove(i);
				return true;
			}
			i++;
		}
		return false;
	}
	
	public void addIgnore(int id) {
		this.ignoreList.add(id);
	}
	
	public Unit getTarget() {
		return this.target;
	}

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
	
	public boolean addItem(Item item, int amount) {
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
	
	private boolean addSingleItem(Item item, int amount) {
		if(item == null) {
			return false;
		}
		int i = 0;
		boolean returns = false;
		if(!item.isStackable()) {
			while(i < this.bag.getBag().length && amount > 0) {
				if(this.bag.getBag(i) == null) {
					this.bag.setBag(i, item);
					CommandSetItem.addItem(this, DragItem.BAG, item.getId(), i, 1);
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
					this.bag.setBag(i, item, this.bag.getBag(i).getAmount()+amount);
					CommandSetItem.setAmount(this, DragItem.BAG, i, this.bag.getBag(i).getAmount());
					this.itemManager.setBagItems(this);
					return true;
				}
				i++;
			}
			i = 0;
			while(i < this.bag.getBag().length) {
				if(this.bag.getBag(i) == null) {
					this.bag.setBag(i, item, amount);
					CommandSetItem.addItem(this, DragItem.BAG, item.getId(), i, amount);;
					this.itemManager.setBagItems(this);
					return true;
				}
				i++;
			}
		}
		return returns;
	}
	
	/*@SuppressWarnings("unused")
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
					number--;
				}
				
				i++;
			}
			this.itemManager.setBagItems(this);
		}
		return returns;
	}*/
	
	private boolean addMultipleUnstackableItem(Item item, int number) {
		if(item == null) {
			return false;
		}
		int i = 0;
		boolean returns = false;
		while(i < this.bag.getBag().length && number > 0) {
			if(this.bag.getBag(i) == null) {
				if(item.isStuff() || item.isWeapon()) {
					this.bag.setBag(i, new Stuff((Stuff)item));
					CommandSetItem.addItem(this, DragItem.BAG, item.getId(), i, 1);
					number--;
					returns = true;
				}
				else if(item.isGem()) {
					this.bag.setBag(i, new Gem((Gem)item));
					CommandSetItem.addItem(this, DragItem.BAG, item.getId(), i, 1);
					number--;
					returns = true;
				}
				else if(item.isPotion()) {
					this.bag.setBag(i, new Potion((Potion)item));
					CommandSetItem.addItem(this, DragItem.BAG, item.getId(), i, 1);
					number--;
					returns = true;
				}
				else if(item.isContainer()) {
					this.bag.setBag(i, new Container((Container)item));
					CommandSetItem.addItem(this, DragItem.BAG, item.getId(), i, 1);
					number--;
					returns = true;
				}
			}
			i++;
		}
		//this.itemManager.setBagItems(this);
		return returns;
	}
	
	public void deleteIdenticalItem(Item item) {
		if(item == null) {
			return;
		}
		int i = 0;
		while(i < this.bag.getBag().length) {
			if(this.bag.getBag(i) == item) {
				this.bag.setBag(i, null);
				CommandSetItem.setNull(this, DragItem.BAG, i);
				return;
			}
			i++;
		}
	}
	
	public void deleteItem(Item item, int amount) {
		if(item == null) {
			return;
		}
		int i = 0;
		if(!item.isStackable()) {
			while(i < this.bag.getBag().length && amount > 0) {
				if(this.bag.getBag(i) != null && this.bag.getBag(i).equals(item)) {
					this.bag.setBag(i, null);
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
					amount = amount-this.bag.getBag(i).getAmount();
					this.bag.setBag(i, this.bag.getBag(i), Math.max(0, this.bag.getBag(i).getAmount())-temp);
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
	
	public void resetDatas() {
		this.stamina = 0;
		this.maxStamina = 0;
		this.mana = 0;
		this.strength = 0;
		this.critical = 0;
		this.armor = 0;
		this.bag = null;
		this.characterId = 0;
		this.classe = null;
		this.damage = 0;
		this.defaultArmor = 0;
		this.exp = 0;
		this.firstProfession = null;
		this.gold = 0;
		this.level = 1;
		this.name = "";
		this.numberBlueGem = 0;
		this.numberRedGem = 0;
		this.numberYellowGem = 0;
		this.race = null;
		this.secondProfession = null;
		this.spellUnlocked.clear();
		this.target = null;
		this.wear = null;
	}
	
	public void setGuildRequest(int id) {
		this.guildRequest = id;
	}
	
	public int getGuildRequest() {
		return this.guildRequest;
	}
	
	public Guild getGuild() {
		return this.guild;
	}
	
	public void setGuild(Guild guild) {
		this.guild = guild;
	}
	
	public void setHasInitParty(boolean we) {
		this.hasInitParty = we;
	}
	
	public boolean hasInitParty() {
		return this.hasInitParty;
	}
	
	public void setPlayerParty(Player player) {
		this.playerParty = player;
	}
	
	public Player getPlayerParty() {
		return this.playerParty;
	}
	
	public void setParty(Party party) {
		this.party = party;
	}
	
	public Party getParty() {
		return this.party;
	}
	
	public void setWeaponType(WeaponType[] type) {
		this.weaponType = type;
	}
	
	public Player getPlayerTrade() {
		return this.playerTrade;
	}
	
	public void setPlayerTrade(Player player) {
		this.playerTrade = player;
	}
	
	public Connection getConnection() {
		return this.connectionManager.getConnection();
	}
	
	public void setAccountRank(AccountRank rank) {
		this.accountRank = rank;
	}
	
	public AccountRank getAccountRank() {
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
	
	public void setStuffArmor(Stuff stuff) {
		this.armor+= stuff.getArmor()+stuff.getStatsFromGems(GemBonusType.ARMOR);
	}

	public void setStuffStrength(Stuff stuff) {
		this.strength+= stuff.getStrength()+stuff.getStatsFromGems(GemBonusType.STRENGTH);
	}
	
	public void setStuffStamina(Stuff stuff) {
		this.maxStamina+= stuff.getStamina()+stuff.getStatsFromGems(GemBonusType.STAMINA);
		this.stamina+= stuff.getStamina()+stuff.getStatsFromGems(GemBonusType.STAMINA);
	}
	
	public void setStuffCritical(Stuff stuff) {
		this.critical+= stuff.getCritical()+stuff.getStatsFromGems(GemBonusType.CRITICAL);
	}
	
	public void setStuffMana(Stuff stuff) {
		this.maxMana+= stuff.getMana()+stuff.getStatsFromGems(GemBonusType.MANA);
		this.mana+= stuff.getMana()+stuff.getStatsFromGems(GemBonusType.MANA);
	}
	
	public void setNumberRedGem(int nb) {
		this.numberRedGem = nb;
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
	
	@Override
	public void setLevel(int level) {
		this.level = level;
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
	
	public void setStamina(double d) {
		this.stamina = (int)Math.max(d, 0);
	}
	
	public Wear getWear() {
		return this.wear;
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
	
	public Stuff[] getStuff() {
		return this.stuff;
	}
	
	public Stuff getStuff(int i) {
		return this.stuff[i];
	}
	
	public void setStuff(int i, Item tempItem) {
		if(this.stuff[i] != null) {
			
		}
		this.stuff[i] = (Stuff)tempItem;
	}
	
	public Shortcut getShortcut(int i) {
		return this.shortcut[i];
	}
	
	public Shortcut[] getShortcut() {
		return this.shortcut;
	}

	public void addUnlockedSpell(int id) {
		if(SpellManager.exists(id)) {
			this.spellUnlocked.put(id, SpellManager.getBookSpell(id));
		}
	}
	
	public Bag getBag() {
		return this.bag;
	}
	
	public int getExp() {
		return this.exp;
	}
	
	public void setArmor(float number) {
		this.armor = (Math.round(100*(this.armor+number))/100.f);
	}
	
	public int getDefaultArmor() {
		return this.defaultArmor;
	}

	public boolean canEquipStuff(Stuff stuff) {
		return this.level >= stuff.getLevel() && canWear(stuff) && stuff.canEquipTo(this.classe);
	}
	
	public boolean canEquipWeapon(Stuff weapon) {
		if(this.level >= weapon.getLevel() && canWearWeapon(weapon) && weapon.canEquipTo(this.classe)) {
			return true;
		}
		return false;
	}

	public boolean canWear(Stuff stuff) {
		if(stuff == null) {
			return false;
		}
		if(this.wear == Wear.PLATE) {
			return true;
		}
		if(this.wear == Wear.MAIL) {
			return stuff.getWear() == Wear.PLATE;
		}
		if(this.wear == Wear.LEATHER) {
			return stuff.getWear() == Wear.PLATE || stuff.getWear() == Wear.MAIL;
		}
		if(this.wear == Wear.CLOTH) {
			return stuff.getWear() == Wear.CLOTH || stuff.getWear() == Wear.NONE;
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
	
	public void loadSpellBar()  {
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
	
	public boolean isGMOn() {
		return this.isGMOn;
	}
	
	public void setGMOn(boolean we) {
		this.isGMOn = we;
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
	
	public static ClassType convStringToClassType(String classType) {
		if(classType.equals(warrior)) {
			return ClassType.GUERRIER;
		}
		if(classType.equals(druid)) {
			return ClassType.DRUID;
		}
		if(classType.equals(hunter)) {
			return ClassType.HUNTER;
		}
		if(classType.equals(mage)) {
			return ClassType.MAGE;
		}
		if(classType.equals(paladin)) {
			return ClassType.PALADIN;
		}
		if(classType.equals(priest)) {
			return ClassType.PRIEST;
		}
		if(classType.equals(rogue)) {
			return ClassType.ROGUE;
		}
		if(classType.equals(shaman)) {
			return ClassType.SHAMAN;
		}
		if(classType.equals(warlock)) {
			return ClassType.WARLOCK;
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
	
	public static int getExpNeeded(int level) {
		if(level == 0) {
			return 0;
		}
		if(level == 1) {
			return 400;
		}
		if(level == 2) {
			return 900;
		}
		if(level == 3) {
			return 1400;
		}
		if(level == 4) {
			return 2100;
		}
		if(level == 5) {
			return 2800;
		}
		if(level == 6) {
			return 3600;
		}
		if(level == 7) {
			return 4500;
		}
		if(level == 8) {
			return 5400;
		}
		if(level == 9) {
			return 6500;
		}
		if(level == 10) {
			return 7600;
		}
		if(level == 11) {
			return 8700;
		}
		if(level == 12) {
			return 9800;
		}
		if(level == 13) {
			return 11000;
		}
		if(level == 14) {
			return 12300;
		}
		if(level == 15) {
			return 13600;
		}
		if(level == 16) {
			return 15000;
		}
		if(level == 17) {
			return 16400;
		}
		if(level == 18) {
			return 17800;
		}
		if(level == 19) {
			return 19300;
		}
		if(level == 20) {
			return 20800;
		}
		if(level == 21) {
			return 22400;
		}
		if(level == 22) {
			return 24000;
		}
		if(level == 23) {
			return 25500;
		}
		if(level == 24) {
			return 27200;
		}
		if(level == 25) {
			return 28900;
		}
		if(level == 26) {
			return 30500;
		}
		if(level == 27) {
			return 32200;
		}
		if(level == 28) {
			return 33900;
		}
		if(level == 29) {
			return 36300;
		}
		if(level == 30) {
			return 38800;
		}
		if(level == 31) {
			return 41600;
		}
		if(level == 32) {
			return 44600;
		}
		if(level == 33) {
			return 48000;
		}
		if(level == 34) {
			return 51400;
		}
		if(level == 35) {
			return 55000;
		}
		if(level == 36) {
			return 58700;
		}
		if(level == 37) {
			return 62400;
		}
		if(level == 38) {
			return 66200;
		}
		if(level == 39) {
			return 70200;
		}
		if(level == 40) {
			return 74300;
		}
		if(level == 41) {
			return 78500;
		}
		if(level == 42) {
			return 82800;
		}
		if(level == 43) {
			return 87100;
		}
		if(level == 44) {
			return 91600;
		}
		if(level == 45) {
			return 96300;
		}
		if(level == 46) {
			return 101000;
		}
		if(level == 47) {
			return 105800;
		}
		if(level == 48) {
			return 110700;
		}
		if(level == 49) {
			return 115700;
		}
		if(level == 50) {
			return 120900;
		}
		if(level == 51) {
			return 126100;
		}
		if(level == 52) {
			return 131500;
		}
		if(level == 53) {
			return 137000;
		}
		if(level == 54) {
			return 142500;
		}
		if(level == 55) {
			return 148200;
		}
		if(level == 56) {
			return 154000;
		}
		if(level == 57) {
			return 159900;
		}
		if(level == 58) {
			return 165800;
		}
		if(level == 59) {
			return 172000;
		}
		if(level == 60) {
			return 290000;
		}
		if(level == 61) {
			return 317000;
		}
		if(level == 62) {
			return 349000;
		}
		if(level == 63) {
			return 386000;
		}
		if(level == 64) {
			return 428000;
		}
		if(level == 65) {
			return 475000;
		}
		if(level == 66) {
			return 527000;
		}
		if(level == 67) {
			return 585000;
		}
		if(level == 68) {
			return 648000;
		}
		if(level == 69) {
			return 717000;
		}
		if(level == 70) {
			return 800000; //TODO check real value
		}
		return -1;
	}
}

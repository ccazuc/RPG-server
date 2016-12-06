package net.game.manager;

import java.sql.SQLException;
import java.util.ArrayList;

import jdo.JDOStatement;
import net.Server;
import net.game.ClassType;
import net.game.Player;
import net.game.Race;
import net.game.item.weapon.WeaponType;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;

public class CharacterManager {

	private Player player;
	private static JDOStatement loadCharacterInfo;
	private static JDOStatement loadSpellUnlocked;
	private static JDOStatement loadRank;
	private static JDOStatement loadWeaponType;
	private static JDOStatement setOnline;
	private static JDOStatement loadPlayerFriend;
	private static JDOStatement loadFriend;
	private static JDOStatement checkOnlinePlayers;
	private static JDOStatement loadCharacterNameFromID;
	private static JDOStatement searchPlayer;
	private final static SQLRequest updateLastOnlineTimer = new SQLRequest("UPDATE `character` SET last_login_timer = ? WHERE character_id = ?", "Update last online timer") {
		
		@Override
		public void gatherData() {
			try {
				this.statement.clear();
				SQLDatas datas = this.datasList.get(0);
				this.statement.putLong(datas.getLValue());
				this.statement.putInt(datas.getIValue1());
				this.statement.execute();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}; 
	private static String rogue = "ROGUE";
	private static String mage = "MAGE";
	private static String druid = "DRUID";
	private static String paladin = "PALADIN";
	private static String priest = "PRIEST";
	private static String warlock = "WARLOCK";
	private static String hunter = "HUNTER";
	private static String shaman = "SHAMAN";
	private static String warrior = "WARRIOR";
	
	private static String human = "HUMAN";
	private static String dwarf = "DWARF";
	private static String gnome = "GNOME";
	private static String draenei = "DRAENEI";
	private static String nightElf = "NIGHTELF";
	private static String bloodElf = "BLOODELF";
	private static String orc = "ORC";
	private static String troll = "TROLL";
	private static String undead = "UNDEAD";
	private static String tauren = "TAUREN";
	
	public CharacterManager(Player player) {
		this.player = player;
	}
	
	public void loadCharacterInfo() throws SQLException {
		if(loadCharacterInfo == null) {
			loadCharacterInfo = Server.getJDO().prepare("SELECT name, class, race, experience, gold FROM `character` WHERE character_id = ?");
			loadRank = Server.getJDO().prepare("SELECT rank FROM account WHERE id = ?");
			loadWeaponType = Server.getJDO().prepare("SELECT weapon_type FROM player WHERE id = ?");
			setOnline = Server.getJDO().prepare("UPDATE `character` SET online = 1 WHERE character_id = ?");
		}
		loadCharacterInfo.clear();
		loadCharacterInfo.putInt(this.player.getCharacterId());
		loadCharacterInfo.execute();
		if(loadCharacterInfo.fetch()) {
			this.player.setName(loadCharacterInfo.getString());
			this.player.setClasse(convStringToClasse(loadCharacterInfo.getString()));
			this.player.setRace(convStringToRace(loadCharacterInfo.getString()));
			this.player.setExperience(loadCharacterInfo.getInt());
			this.player.setGold(loadCharacterInfo.getInt());
		}
		loadRank.clear();
		loadRank.putInt(this.player.getAccountId());
		loadRank.execute();
		if(loadRank.fetch()) {
			this.player.setAccountRank(loadRank.getInt());
		}
		loadWeaponType.clear();
		loadWeaponType.putString(convClasseToString(this.player.getClasse()));
		loadWeaponType.execute();
		if(loadWeaponType.fetch()) {
			int weaponType = loadWeaponType.getInt();
			this.player.setWeaponType(getWeaponTypes((short)weaponType));
		}
		setOnline.clear();
		setOnline.putInt(this.player.getCharacterId());
		setOnline.execute();
		int i = 0;
		while(i < this.player.getStuff().length) {
			if(this.player.getStuff(i) != null) {
				this.player.setStuffStamina(this.player.getStuff(i));
				this.player.setStuffCritical(this.player.getStuff(i));
				this.player.setStuffArmor(this.player.getStuff(i));
				this.player.setStuffMana(this.player.getStuff(i));
				this.player.setStuffStrength(this.player.getStuff(i));
			}
			i++;
		}
	}
	
	/*public void loadFriendList(Player player) throws SQLException {
		if(loadPlayerFriend == null) {
			//loadPlayerFriend = Server.getJDO().prepare("SELECT character_id FROM friend WHERE friend_id = ? AND online = 1");
			loadPlayerFriend = Server.getJDO().prepare("SELECT `friend`.`character_id`, `character`.`online` FROM `friend`, `character` WHERE `friend`.`friend_id` = ? AND `character`.`character_id` = `friend`.`friend_id` AND `character`.`online` = 1");
			loadFriend = Server.getJDO().prepare("SELECT friend_id FROM friend WHERE character_id = ?");
		}
		ArrayList<Integer> temp = new ArrayList<Integer>();
		loadPlayerFriend.clear();
		loadPlayerFriend.putInt(player.getCharacterId());
		loadPlayerFriend.execute();
		while(loadPlayerFriend.fetch()) {
			int id = loadPlayerFriend.getInt();
			temp.add(id);
			System.out.println("Friend id: "+id);
		}
		Server.getFriendMap().put(player.getCharacterId(), temp);
		
		loadFriend.clear();
		loadFriend.putInt(player.getCharacterId());
		loadFriend.execute();
		while(loadFriend.fetch()) {
			int id = loadFriend.getInt();
			player.getFriendList().add(id);
			if(Server.getInGamePlayerList().containsKey(id)) {
				Server.getFriendMap().get(id).add(player.getCharacterId());
			}
		}
	}*/
	
	public void loadFriendList() throws SQLException {
		if(loadPlayerFriend == null) {
			loadPlayerFriend = Server.getJDO().prepare("SELECT character_id FROM social_friend WHERE friend_id = ?");
			loadFriend = Server.getJDO().prepare("SELECT friend_id FROM social_friend WHERE character_id = ?");
		}
		loadPlayerFriend.clear();
		loadPlayerFriend.putInt(this.player.getCharacterId());
		loadPlayerFriend.execute();
		while(loadPlayerFriend.fetch()) {
			int id = loadPlayerFriend.getInt();
			if(!FriendManager.containsKey(this.player.getCharacterId())) {
				FriendManager.getFriendMap().put(this.player.getCharacterId(), new ArrayList<Integer>());
			}
			FriendManager.getFriendMap().get(this.player.getCharacterId()).add(id);
			//System.out.println("Friended id: "+id);
		}
		
		loadFriend.clear();
		loadFriend.putInt(this.player.getCharacterId());
		loadFriend.execute();
		while(loadFriend.fetch()) {
			int id = loadFriend.getInt();
			this.player.getFriendList().add(id);
		}
	}
	
	public void updateLastLoginTimer() {
		//updateLastOnlineTimer.setId(player.getCharacterId());
		//updateLastOnlineTimer.setValue(System.currentTimeMillis());
		updateLastOnlineTimer.addDatas(new SQLDatas(this.player.getCharacterId(), System.currentTimeMillis()));
		Server.addNewRequest(updateLastOnlineTimer);
	}
	
	public static void checkOnlinePlayers() throws SQLException {
		if(checkOnlinePlayers == null) {
			checkOnlinePlayers = Server.getJDO().prepare("UPDATE `character` SET online = 0 WHERE online = 1");
		}
		checkOnlinePlayers.clear();
		checkOnlinePlayers.execute();
	}
	
	public void loadSpellUnlocked() throws SQLException {
		if(loadSpellUnlocked == null) {
			loadSpellUnlocked = Server.getJDO().prepare("SELECT id FROM character_spell_unlocked WHERE character_id = ?");
		}
		loadSpellUnlocked.clear();
		loadSpellUnlocked.putInt(this.player.getCharacterId());
		loadSpellUnlocked.execute();
		while(loadSpellUnlocked.fetch()) {
			this.player.addUnlockedSpell(loadSpellUnlocked.getInt());
		}
	}
	
	public static String loadCharacterNameFromID(int id) {
		try {
			if(loadCharacterNameFromID == null) {
				loadCharacterNameFromID = Server.getJDO().prepare("SELECT name FROM `character` WHERE character_id = ?");
			}
			loadCharacterNameFromID.clear();
			loadCharacterNameFromID.putInt(id);
			loadCharacterNameFromID.execute();
			if(loadCharacterNameFromID.fetch()) {
				return loadCharacterNameFromID.getString();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static int playerExistsInDB(String name) {
		int id = -1;
		try {
			if(searchPlayer == null) {
				searchPlayer = Server.getJDO().prepare("SELECT character_id FROM `character` WHERE name = ?");
			}
			searchPlayer.clear();
			searchPlayer.putString(name);
			searchPlayer.execute();
			if(searchPlayer.fetch()) {
				id = searchPlayer.getInt();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return id;
	}
	
	public static WeaponType[] getWeaponTypes(short type) {
		int i = 0;
		int count = 0;
		while(i < 15) {
			if((type & (1 << i)) != 0) {
				count++;
			}
			i++;
		}
		WeaponType[] tempWeaponsType = new WeaponType[count];
		count = 0;
		if((type & (1 << 0)) != 0) {
			tempWeaponsType[count] = WeaponType.DAGGER;
			count++;
		}
		if((type & (1 << 1)) != 0) {
			tempWeaponsType[count] = WeaponType.FISTWEAPON;
			count++;
		}
		if((type & (1 << 2)) != 0) {
			tempWeaponsType[count] = WeaponType.ONEHANDEDAXE;
			count++;
		}
		if((type & (1 << 3)) != 0) {
			tempWeaponsType[count] = WeaponType.TWOHANDEDAXE;
			count++;
		}
		if((type & (1 << 4)) != 0) {
			tempWeaponsType[count] = WeaponType.ONEHANDEDMACE;
			count++;
		}
		if((type & (1 << 5)) != 0) {
			tempWeaponsType[count] = WeaponType.TWOHANDEDMACE;
			count++;
		}
		if((type & (1 << 6)) != 0) {
			tempWeaponsType[count] = WeaponType.ONEHANDEDSWORD;
			count++;
		}
		if((type & (1 << 7)) != 0) {
			tempWeaponsType[count] = WeaponType.TWOHANDEDSWORD;
			count++;
		}
		if((type & (1 << 8)) != 0) {
			tempWeaponsType[count] = WeaponType.POLEARM;
			count++;
		}
		if((type & (1 << 9)) != 0) {
			tempWeaponsType[count] = WeaponType.STAFF;
			count++;
		}
		if((type & (1 << 10)) != 0) {
			tempWeaponsType[count] = WeaponType.BOW;
			count++;
		}
		if((type & (1 << 11)) != 0) {
			tempWeaponsType[count] = WeaponType.CROSSBOW;
			count++;
		}
		if((type & (1 << 12)) != 0) {
			tempWeaponsType[count] = WeaponType.GUN;
			count++;
		}
		if((type & (1 << 13)) != 0) {
			tempWeaponsType[count] = WeaponType.WAND;
			count++;
		}
		if((type & (1 << 14)) != 0) {
			tempWeaponsType[count] = WeaponType.THROWN;
			count++;
		}
		i = 0;
		while(i < count) {
			i++;
		}
		return tempWeaponsType;
	}
	
	private static ClassType convStringToClasse(String classe) {
		if(classe.equals(rogue)) {
			return ClassType.ROGUE;
		}
		if(classe.equals(mage)) {
			return ClassType.MAGE;
		}
		if(classe.equals(druid)) {
			return ClassType.DRUID;
		}
		if(classe.equals(paladin)) {
			return ClassType.PALADIN;
		}
		if(classe.equals(priest)) {
			return ClassType.PRIEST;
		}
		if(classe.equals(warlock)) {
			return ClassType.WARLOCK;
		}
		if(classe.equals(warrior)) {
			return ClassType.GUERRIER;
		}
		if(classe.equals(hunter)) {
			return ClassType.HUNTER;
		}
		if(classe.equals(shaman)) {
			return ClassType.SHAMAN;
		}
		return null;
	}
	
	private static Race convStringToRace(String race) {
		if(race.equals(bloodElf)) {
			return Race.BLOODELF;
		}
		if(race.equals(orc)) {
			return Race.ORC;
		}
		if(race.equals(troll)) {
			return Race.TROLL;
		}
		if(race.equals(tauren)) {
			return Race.TAUREN;
		}
		if(race.equals(undead)) {
			return Race.UNDEAD;
		}
		if(race.equals(gnome)) {
			return Race.GNOME;
		}
		if(race.equals(dwarf)) {
			return Race.DWARF;
		}
		if(race.equals(human)) {
			return Race.HUMAN;
		}
		if(race.equals(draenei)) {
			return Race.DRAENEI;
		}
		if(race.equals(nightElf)) {
			return Race.NIGHTELF;
		}
		return null;
	}
	
	private static String convClasseToString(ClassType classe) {
		if(classe == ClassType.DRUID) {
			return "Druid";
		}
		if(classe == ClassType.GUERRIER) {
			return  "Guerrier";
		}
		if(classe == ClassType.HUNTER) {
			return "Hunter";
		}
		if(classe == ClassType.MAGE) {
			return "Mage";
		}
		if(classe == ClassType.PALADIN) {
			return "Paladin";
		}
		if(classe == ClassType.PRIEST) {
			return "Priest";
		}
		if(classe == ClassType.ROGUE) {
			return "Rogue";
		}
		if(classe == ClassType.SHAMAN) {
			return "Shaman";
		}
		if(classe == ClassType.WARLOCK) {
			return "Warlock";
		}
		return "";
	}
}
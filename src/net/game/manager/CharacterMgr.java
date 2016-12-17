package net.game.manager;

import java.sql.SQLException;
import java.util.ArrayList;

import jdo.JDOStatement;
import net.Server;
import net.game.AccountRank;
import net.game.ClassType;
import net.game.Player;
import net.game.Race;
import net.game.item.weapon.WeaponType;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;

public class CharacterMgr {

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
	private static JDOStatement checkPlayerAccount;
	private static JDOStatement loadCharacterIdFromName;
	private final static SQLRequest updateLastOnlineTimer = new SQLRequest("UPDATE `character` SET last_login_timer = ? WHERE character_id = ?", "Update last online timer") {
		
		@Override
		public void gatherData() {
			try {
				this.statement.clear();
				SQLDatas datas = this.datasList.get(0);
				this.statement.putLong(datas.getLValue1());
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
	
	public CharacterMgr(Player player) {
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
			this.player.setAccountRank(AccountRank.values()[loadRank.getInt()-1]);
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
			if(!FriendMgr.containsKey(this.player.getCharacterId())) {
				FriendMgr.getFriendMap().put(this.player.getCharacterId(), new ArrayList<Integer>());
			}
			FriendMgr.getFriendMap().get(this.player.getCharacterId()).add(id);
		}
		
		loadFriend.clear();
		loadFriend.putInt(this.player.getCharacterId());
		loadFriend.execute();
		while(loadFriend.fetch()) {
			int id = loadFriend.getInt();
			this.player.getFriendList().add(id);
		}
	}
	
	public static boolean checkPlayerAccount(int account_id, int player_id) {
		try {
			if(checkPlayerAccount == null) {
				checkPlayerAccount = Server.getJDO().prepare("SELECT COUNT(character_id) FROM `character` WHERE account_id = ? AND character_id = ?");
			}
			checkPlayerAccount.clear();
			checkPlayerAccount.putInt(account_id);
			checkPlayerAccount.putInt(player_id);
			checkPlayerAccount.execute();
			if(checkPlayerAccount.fetch()) {
				return true;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void updateLastLoginTimer() {
		updateLastOnlineTimer.addDatas(new SQLDatas(this.player.getCharacterId(), System.currentTimeMillis()));
		Server.addNewSQLRequest(updateLastOnlineTimer);
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
	
	public static int loadCharacterIDFromName(String name) {
		try {
			if(loadCharacterIdFromName == null) {
				loadCharacterIdFromName = Server.getJDO().prepare("SELECT character_id FROM `character` WHERE name= ?");
			}
			loadCharacterIdFromName.clear();
			loadCharacterIdFromName.putString(name);
			loadCharacterIdFromName.execute();
			if(loadCharacterIdFromName.fetch()) {
				return loadCharacterIdFromName.getInt();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static int playerExistsInDB(String name) {
		try {
			if(searchPlayer == null) {
				searchPlayer = Server.getJDO().prepare("SELECT character_id FROM `character` WHERE name = ?");
			}
			searchPlayer.clear();
			searchPlayer.putString(name);
			searchPlayer.execute();
			if(searchPlayer.fetch()) {
				return searchPlayer.getInt();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return -1;
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
		return null;
	}
}

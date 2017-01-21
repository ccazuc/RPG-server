package net.game.manager;

import java.sql.SQLException;
import java.util.ArrayList;

import jdo.JDOStatement;
import net.Server;
import net.command.item.CommandSendContainer;
import net.command.player.CommandFriend;
import net.command.player.CommandGuild;
import net.command.player.CommandIgnore;
import net.command.player.CommandSendPlayer;
import net.command.player.CommandSendTarget;
import net.command.player.spell.CommandAura;
import net.command.player.spell.CommandSpellUnlocked;
import net.game.AccountRank;
import net.game.aura.AppliedAura;
import net.game.aura.AuraMgr;
import net.game.item.weapon.WeaponType;
import net.game.spell.Spell;
import net.game.unit.ClassType;
import net.game.unit.Player;
import net.game.unit.Race;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;
import net.thread.sql.SQLRequestPriority;
import net.thread.sql.SQLTask;

public class CharacterMgr {

	private static JDOStatement loadCharacterInfo;
	private static JDOStatement loadRank;
	private static JDOStatement loadWeaponType;
	private static JDOStatement setOnline;
	private static JDOStatement loadPlayerFriend;
	private static JDOStatement loadFriend;
	private static JDOStatement checkOnlinePlayers;
	private static JDOStatement loadCharacterNameFromID;
	private static JDOStatement searchPlayer;
	private static JDOStatement checkPlayerAccount;
	private static JDOStatement loadCharacterIDFromName;
	private static JDOStatement loadCharacterIDAndNameFromNamePattern;
	private static JDOStatement removeCharacter;
	private static JDOStatement removeBag;
	private static JDOStatement removeContainer;
	private static JDOStatement removeStuff;
	private static JDOStatement removeSpellbar;
	private static JDOStatement setExperience;
	private static JDOStatement getExperience;
	private static JDOStatement loadAuras;
	private static JDOStatement removeAuras;
	private static JDOStatement saveAuras;
	private static JDOStatement loadSpellsUnlocked;
	private static JDOStatement saveSpellsUnlocked;
	private static SQLRequest asyncSetExperience = new SQLRequest("UPDATE `character` SET experience = ? WHERE character_id ?", "Set experience", SQLRequestPriority.HIGH) {
		
		@Override
		public void gatherData() {
			try {
				this.statement.clear();
				SQLDatas datas = this.datasList.get(0);
				this.statement.putInt(datas.getIValue1());
				this.statement.putInt(datas.getIValue2());
				this.statement.execute();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
	private static SQLRequest asyncRemoveCharacter = new SQLRequest("DELETE FROM `character` WHERE character_id = ?", "Remove character", SQLRequestPriority.HIGH) {
		
		@Override
		public void gatherData() {
			try {
				this.statement.clear();
				SQLDatas datas = this.datasList.get(0);
				this.statement.putInt(datas.getIValue1());
				this.statement.execute();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
	private static SQLRequest asyncRemoveBag = new SQLRequest("DELETE FROM bag WHERE character_id = ?", "Remove bag", SQLRequestPriority.HIGH) {
		
		@Override
		public void gatherData() {
			try {
				this.statement.clear();
				SQLDatas datas = this.datasList.get(0);
				this.statement.putInt(datas.getIValue1());
				this.statement.execute();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
	private static SQLRequest asyncRemoveContainer = new SQLRequest("DELETE FROM character_containers WHERE character_id = ?", "Remove container", SQLRequestPriority.HIGH) {
		
		@Override
		public void gatherData() {
			try {
				this.statement.clear();
				SQLDatas datas = this.datasList.get(0);
				this.statement.putInt(datas.getIValue1());
				this.statement.execute();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
	private static SQLRequest asyncRemoveStuff = new SQLRequest("DELETE FROM character_stuff WHERE character_id = ?", "Remove stuff", SQLRequestPriority.HIGH) {
		
		@Override
		public void gatherData() {
			try {
				this.statement.clear();
				SQLDatas datas = this.datasList.get(0);
				this.statement.putInt(datas.getIValue1());
				this.statement.execute();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
	private static SQLRequest asyncRemoveSpellbar = new SQLRequest("DELETE FROM spellbar WHERE character_id = ?", "Remove spellbar", SQLRequestPriority.HIGH) {
		
		@Override
		public void gatherData() {
			try {
				this.statement.clear();
				SQLDatas datas = this.datasList.get(0);
				this.statement.putInt(datas.getIValue1());
				this.statement.execute();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
	private final static SQLRequest updateLastOnlineTimer = new SQLRequest("UPDATE `character` SET last_login_timer = ? WHERE character_id = ?", "Update last online timer", SQLRequestPriority.LOW) {
		
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
	private final static SQLTask fullyLoadCharacter = new SQLTask("Fully load character") {
	
		@Override
		public void gatherData() {
			Player player = this.datasList.get(0).getPlayer();
			int id = this.datasList.get(0).getIValue1();
			player.setOnline();
			player.setUnitID(id);
			player.initTable();
			player.loadCharacterInfoSQL();
			CommandSendPlayer.write(player);
			CommandSendTarget.sendTarget(player, player.getTarget());
			player.sendStats();
			loadSpellsUnlocked(player);
			CommandSpellUnlocked.initSpellUnlocked(player);
			player.loadEquippedBagSQL();
			CommandSendContainer.sendContainer(player);
			player.loadEquippedItemSQL();
			player.loadBagItemSQL();
			player.loadFriendList();
			player.updateLastLoginTimer();
			CommandFriend.loadFriendList(player);
			player.notifyFriendOnline();
			loadAuras(player);
			IgnoreMgr.loadIgnoreList(player.getUnitID());
			CommandIgnore.ignoreInit(player.getConnection(), player);
			player.loadGuild();
			if(player.getGuild() != null) {
				CommandGuild.initGuildWhenLogin(player);
				CommandGuild.sendGuildEventWhenLogin(player);
				player.getGuild().getMember(player.getUnitID()).setOnlineStatus(true);
				CommandGuild.notifyOnlinePlayer(player);
			}
			Server.addInGamePlayer(player);
			Server.removeLoggedPlayer(player);
		}
	};
	private final static SQLTask fullySaveCharacter = new SQLTask("Fully save character") {
	
		@Override
		public void gatherData() {
			Player player = this.datasList.get(0).getPlayer();
			saveAuras(player);
			saveSpellsUnlocked(player);
			
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
	
	public static void fullyLoadCharacter(Player player, int id) {
		fullyLoadCharacter.addDatas(new SQLDatas(player, id));
		Server.executeHighPrioritySQLTask(fullyLoadCharacter);
	}
	
	public static void fullySaveCharacter(Player player) {
		fullySaveCharacter.addDatas(new SQLDatas(player));
		Server.executeHighPrioritySQLTask(fullySaveCharacter);
	}
	
	public static void loadCharacterInfo(Player player) {
		try {
			if(loadCharacterInfo == null) {
				loadCharacterInfo = Server.getAsyncHighPriorityJDO().prepare("SELECT name, class, race, experience, gold FROM `character` WHERE character_id = ?");
				loadRank = Server.getAsyncHighPriorityJDO().prepare("SELECT rank FROM account WHERE id = ?");
				loadWeaponType = Server.getAsyncHighPriorityJDO().prepare("SELECT weapon_type FROM player WHERE id = ?");
				setOnline = Server.getAsyncHighPriorityJDO().prepare("UPDATE `character` SET online = 1 WHERE character_id = ?");
			}
			loadCharacterInfo.clear();
			int id = player.getUnitID();
			loadCharacterInfo.putInt(id);
			loadCharacterInfo.execute();
			if(loadCharacterInfo.fetch()) {
				player.setName(loadCharacterInfo.getString());
				player.setClasse(convStringToClasse(loadCharacterInfo.getString()));
				player.setRace(convStringToRace(loadCharacterInfo.getString()));
				player.setExperience(loadCharacterInfo.getInt());
				player.setGold(loadCharacterInfo.getInt());
			}
			loadRank.clear();
			loadRank.putInt(player.getAccountId());
			loadRank.execute();
			if(loadRank.fetch()) {
				player.setAccountRank(AccountRank.values()[loadRank.getInt()-1]);
			}
			loadWeaponType.clear();
			loadWeaponType.putString(convClasseToString(player.getClasse()));
			loadWeaponType.execute();
			if(loadWeaponType.fetch()) {
				int weaponType = loadWeaponType.getInt();
				player.setWeaponType(getWeaponTypes((short)weaponType));
			}
			setOnline.clear();
			setOnline.putInt(player.getUnitID());
			setOnline.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		int i = 0;
		while(i < player.getStuff().length) {
			if(player.getStuff(i) != null) {
				player.setStuffStamina(player.getStuff(i));
				player.setStuffCritical(player.getStuff(i));
				player.setStuffArmor(player.getStuff(i));
				player.setStuffMana(player.getStuff(i));
				player.setStuffStrength(player.getStuff(i));
			}
			i++;
		}
	}
	
	static void loadAuras(Player player) {
		try {
			if(loadAuras == null) {
				loadAuras = Server.getJDO().prepare("SELECT aura_id, caster_id, time_left, number_stack FROM character_aura WHERE character_id = ?");
			}
			loadAuras.clear();
			loadAuras.putInt(player.getUnitID());
			loadAuras.execute();
			boolean hasAura = false;
			while(loadAuras.fetch()) {
				int auraID = loadAuras.getInt();
				int casterID = loadAuras.getInt();
				long time_left = loadAuras.getLong();
				byte number_stack = loadAuras.getByte();
				player.setAura(new AppliedAura(AuraMgr.getAura(auraID), time_left, number_stack, casterID));
				hasAura = true;
			}
			if(hasAura) {
				player.calcAllStats();
				CommandAura.initAura(player, player);
				removeAuras(player);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveAuras(Player player) {
		try {
			if(saveAuras == null) {
				saveAuras = Server.getAsyncHighPriorityJDO().prepare("INSERT INTO character_aura (character_id, aura_id, caster_id, time_left, number_stack) VALUES(?, ?, ?, ?, ?)");
			}
			int i = player.getAuraList().size();
			while(--i >= 0) {
				saveAuras.clear();
				saveAuras.putInt(player.getUnitID());
				saveAuras.putInt(player.getAuraList().get(i).getAura().getId());
				saveAuras.putInt(player.getAuraList().get(i).getCasterID());
				saveAuras.putLong(player.getAuraList().get(i).getEndTimer()-Server.getLoopTickTimer());
				saveAuras.putByte(player.getAuraList().get(i).getNumberStack());
				saveAuras.execute();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void removeAuras(Player player) {
		try {
			if(removeAuras == null) {
				removeAuras = Server.getJDO().prepare("DELETE FROM character_aura WHERE character_id = ?");
			}
			removeAuras.clear();
			removeAuras.putInt(player.getUnitID());
			removeAuras.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadSpellsUnlocked(Player player) {
		try {
			if(loadSpellsUnlocked == null) {
				loadSpellsUnlocked = Server.getAsyncHighPriorityJDO().prepare("SELECT spell_id FROM spell_unlocked WHERE character_id = ?");
			}
			loadSpellsUnlocked.clear();
			loadSpellsUnlocked.putInt(player.getUnitID());
			loadSpellsUnlocked.execute();
			while(loadSpellsUnlocked.fetch()) {
				player.addUnlockedSpell(loadSpellsUnlocked.getInt());
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveSpellsUnlocked(Player player) {
		try {
			if(saveSpellsUnlocked == null) {
				saveSpellsUnlocked = Server.getAsyncHighPriorityJDO().prepare("INSERT INTO spell_unlocked (character_id, spell_id) VALUES (?, ?)");
			}
			for(Spell spell : player.getSpellUnlocked().values()) {
				saveSpellsUnlocked.clear();
				saveSpellsUnlocked.putInt(player.getUnitID());
				saveSpellsUnlocked.putInt(spell.getSpellId());
				saveSpellsUnlocked.execute();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadFriendList(Player player) {
		try {
			if(loadPlayerFriend == null) {
				loadPlayerFriend = Server.getAsyncHighPriorityJDO().prepare("SELECT character_id FROM social_friend WHERE friend_id = ?");
				loadFriend = Server.getAsyncHighPriorityJDO().prepare("SELECT friend_id FROM social_friend WHERE character_id = ?");
			}
			loadPlayerFriend.clear();
			loadPlayerFriend.putInt(player.getUnitID());
			loadPlayerFriend.execute();
			while(loadPlayerFriend.fetch()) {
				int id = loadPlayerFriend.getInt();
				if(!FriendMgr.containsKey(player.getUnitID())) {
					FriendMgr.getFriendMap().put(player.getUnitID(), new ArrayList<Integer>());
				}
				FriendMgr.getFriendMap().get(player.getUnitID()).add(id);
			}
			
			loadFriend.clear();
			loadFriend.putInt(player.getUnitID());
			loadFriend.execute();
			while(loadFriend.fetch()) {
				int id = loadFriend.getInt();
				player.getFriendList().add(id);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
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
	
	public static void updateLastLoginTimer(Player player) {
		updateLastOnlineTimer.addDatas(new SQLDatas(player.getUnitID(), Server.getLoopTickTimer()));
		Server.executeSQLRequest(updateLastOnlineTimer);
	}
	
	public static void checkOnlinePlayers() throws SQLException {
		if(checkOnlinePlayers == null) {
			checkOnlinePlayers = Server.getJDO().prepare("UPDATE `character` SET online = 0 WHERE online = 1");
		}
		checkOnlinePlayers.clear();
		checkOnlinePlayers.execute();
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
			if(loadCharacterIDFromName == null) {
				loadCharacterIDFromName = Server.getJDO().prepare("SELECT character_id FROM `character` WHERE name= ?");
			}
			loadCharacterIDFromName.clear();
			loadCharacterIDFromName.putString(name);
			loadCharacterIDFromName.execute();
			if(loadCharacterIDFromName.fetch()) {
				return loadCharacterIDFromName.getInt();
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
	
	public static ArrayList<SQLDatas> loadCharacterIDAndNameFromNamePattern(String pattern) { //TODO find why CONTAINS doesn't work
		try {
			if(loadCharacterIDAndNameFromNamePattern == null) {
				loadCharacterIDAndNameFromNamePattern = Server.getJDO().prepare("SELECT character_id, name FROM `character` WHERE CONTAINS(name, ?)");
			}
			ArrayList<SQLDatas> list = null;
			loadCharacterIDAndNameFromNamePattern.clear();
			loadCharacterIDAndNameFromNamePattern.putString(pattern);
			loadCharacterIDAndNameFromNamePattern.execute();
			boolean init = false;
			while(loadCharacterIDAndNameFromNamePattern.fetch()) {
				if(!init) {
					 list = new ArrayList<SQLDatas>();
					 init = true;
				}
				System.out.println("FETCH");
				list.add(new SQLDatas(loadCharacterIDAndNameFromNamePattern.getInt(), loadCharacterIDAndNameFromNamePattern.getString()));
			}
			return list;
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void asynDeleteCharacterByName(String name) {
		int id = loadCharacterIDFromName(name);
		if(id == -1) {
			return;
		}
		asynDeleteCharacterByID(id);
	}
	
	public static void asynDeleteCharacterByID(int id) {
		asyncRemoveCharacter.addDatas(new SQLDatas(id));
		Server.executeSQLRequest(asyncRemoveCharacter);
		asyncRemoveBag.addDatas(new SQLDatas(id));
		Server.executeSQLRequest(asyncRemoveBag);
		asyncRemoveContainer.addDatas(new SQLDatas(id));
		Server.executeSQLRequest(asyncRemoveContainer);
		asyncRemoveStuff.addDatas(new SQLDatas(id));
		Server.executeSQLRequest(asyncRemoveStuff);
		asyncRemoveSpellbar.addDatas(new SQLDatas(id));
		Server.executeSQLRequest(asyncRemoveSpellbar);
	}
	
	public static void deleteCharacterByName(String name) {
		int id = loadCharacterIDFromName(name);
		if(id == -1) {
			return;
		}
		deleteCharacterByID(id);
	}
	
	public static void deleteCharacterByID(int id) {
		try {
			if(removeCharacter == null) {
				removeCharacter = Server.getJDO().prepare("DELETE FROM `character` WHERE character_id = ?");
				removeBag = Server.getJDO().prepare("DELETE FROM bag WHERE character_id = ?");
				removeContainer = Server.getJDO().prepare("DELETE FROM character_containers WHERE character_id = ?");
				removeStuff = Server.getJDO().prepare("DELETE FROM character_stuff WHERE character_id = ?");
				removeSpellbar = Server.getJDO().prepare("DELETE FROM spellbar WHERE character_id = ?");
			}
			removeCharacter.clear();
			removeCharacter.putInt(id);
			removeCharacter.execute();
			removeBag.clear();
			removeBag.putInt(id);
			removeBag.execute();
			removeContainer.clear();
			removeContainer.putInt(id);
			removeContainer.execute();
			removeStuff.clear();
			removeStuff.putInt(id);
			removeStuff.execute();
			removeSpellbar.clear();
			removeSpellbar.putInt(id);
			removeSpellbar.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void setExperience(int id, int experience) {
		try {
			if(setExperience == null) {
				setExperience = Server.getJDO().prepare("UPDATE `character` SET experience = ? WHERE character_id = ?");
			}
			setExperience.clear();
			setExperience.putInt(experience);
			setExperience.putInt(id);
			setExperience.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void asynSetExperience(int id, int experience) {
		asyncSetExperience.addDatas(new SQLDatas(id, experience));
		Server.executeSQLRequest(asyncSetExperience);
	}
	
	public static int getExperience(int id) {
		try {
			if(getExperience == null) {
				getExperience = Server.getJDO().prepare("SELECT experience FROM `character` WHERE character_id ?");
			}
			getExperience.clear();
			getExperience.putInt(id);
			getExperience.execute();
			if(getExperience.fetch()) {
				return getExperience.getInt();
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

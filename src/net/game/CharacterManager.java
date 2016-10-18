package net.game;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Server;

public class CharacterManager {

	private static JDOStatement loadCharacterInfo;
	private static JDOStatement loadSpellUnlocked;
	
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
	
	public void loadCharacterInfo(Player player) throws SQLException {
		if(loadCharacterInfo == null) {
			loadCharacterInfo = Server.getJDO().prepare("SELECT name, class, race, experience, gold FROM `character` WHERE character_id = ?");
		}
		loadCharacterInfo.clear();
		loadCharacterInfo.putInt(player.getCharacterId());
		loadCharacterInfo.execute();
		if(loadCharacterInfo.fetch()) {
			player.setName(loadCharacterInfo.getString());
			player.setClasse(convStringToClasse(loadCharacterInfo.getString()));
			player.setRace(convStringToRace(loadCharacterInfo.getString()));
			player.setExperience(loadCharacterInfo.getInt());
			player.setGold(loadCharacterInfo.getInt());
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
	
	public void loadSpellUnlocked(Player player) throws SQLException {
		if(loadSpellUnlocked == null) {
			loadSpellUnlocked = Server.getJDO().prepare("SELECT id FROM character_spell_unlocked WHERE character_id = ?");
		}
		loadSpellUnlocked.clear();
		loadSpellUnlocked.putInt(player.getCharacterId());
		loadSpellUnlocked.execute();
		while(loadSpellUnlocked.fetch()) {
			player.addUnlockedSpell(loadSpellUnlocked.getInt());
		}
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
}

package net.game.item.stuff;

import java.sql.SQLException;
import java.util.HashMap;

import jdo.JDOStatement;
import net.Server;
import net.game.Wear;
import net.game.item.gem.GemBonusType;
import net.game.item.gem.GemManager;
import net.game.unit.ClassType;
import net.game.item.gem.GemColor;

public class StuffManager {

	public final static String LOAD_STUFF_REQUEST = "SELECT id, type, name, class, wear, sprite_id, quality, gem_slot1, gem_slot2, gem_slot3, gem_bonus_type, gem_bonus_value, level, armor, stamina, mana, critical, strength, sellprice FROM item_stuff";
	private static HashMap<Integer, Stuff> stuffList = new HashMap<Integer, Stuff>();
	private static int numberStuffLoaded;
	private static JDOStatement loadStuff;
	
	public static void loadStuffs() throws SQLException {
		if(loadStuff == null) {
			loadStuff = Server.getJDO().prepare(LOAD_STUFF_REQUEST);
		}
		loadStuff.clear();
		loadStuff.execute();
		while(loadStuff.fetch()) {
			int id = loadStuff.getInt();
			String tempType = loadStuff.getString();
			StuffType type = getType(tempType);
			String name = loadStuff.getString();
			short classeTemp = loadStuff.getShort();
			ClassType[] classeType = getClasses(classeTemp);
			String tempWear = loadStuff.getString();
			Wear wear = getWear(tempWear);
			String sprite_id = loadStuff.getString();
			byte quality = loadStuff.getByte();
			String tempColor =loadStuff.getString();
			GemColor color1 = GemManager.convColor(tempColor);
			tempColor = loadStuff.getString();
			GemColor color2 = GemManager.convColor(tempColor);
			tempColor = loadStuff.getString();
			GemColor color3 = GemManager.convColor(tempColor);
			String tempBonusType = loadStuff.getString();
			GemBonusType bonusType = convBonusType(tempBonusType);
			int bonusValue = loadStuff.getInt();
			byte level = loadStuff.getByte();
			int armor = loadStuff.getInt();
			int stamina = loadStuff.getInt();
			int mana = loadStuff.getInt();
			int critical = loadStuff.getInt();
			int strength = loadStuff.getInt();
			int sellPrice = loadStuff.getInt();
			Stuff newPiece = new Stuff(type, classeType, sprite_id, id, name, quality, color1, color2, color3, bonusType, bonusValue, level, wear, critical, strength, stamina, armor, mana, sellPrice);
			stuffList.put(id, newPiece);
			numberStuffLoaded++;
		}
	}
	
	public static Stuff getStuff(int id) {
		if(stuffList.containsKey(id)) {
			return stuffList.get(id);
		}
		return null;
	}
	
	public static boolean exists(int id) {
		return stuffList.containsKey(id);
	}
	
	public static Stuff getClone(int id) {
		Stuff temp = getStuff(id);
		if(temp != null) {
			return new Stuff(temp);
		}
		return null;
	}
	
	public static ClassType[] getClasses(short type) {
		int i = 0;
		int count = 0;
		while(i < 9) {
			if((type & (1 << i)) != 0) {
				count++;
			}
			i++;
		}
		ClassType[] tempClasses = new ClassType[count];
		count = 0;
		if((type & (1 << 0)) != 0) {
			tempClasses[count] = ClassType.GUERRIER;
			count++;
		}
		if((type & (1 << 1)) != 0) {
			tempClasses[count] = ClassType.HUNTER;
			count++;
		}
		if((type & (1 << 2)) != 0) {
			tempClasses[count] = ClassType.MAGE;
			count++;
		}
		if((type & (1 << 3)) != 0) {
			tempClasses[count] = ClassType.DRUID;
			count++;
		}
		if((type & (1 << 4)) != 0) {
			tempClasses[count] = ClassType.PALADIN;
			count++;
		}
		if((type & (1 << 5)) != 0) {
			tempClasses[count] = ClassType.PRIEST;
			count++;
		}
		if((type & (1 << 6)) != 0) {
			tempClasses[count] = ClassType.ROGUE;
			count++;
		}
		if((type & (1 << 7)) != 0) {
			tempClasses[count] = ClassType.SHAMAN;
			count++;
		}
		if((type & (1 << 8)) != 0) {
			tempClasses[count] = ClassType.WARLOCK;
			count++;
		}
		return tempClasses;
	}
	
	public static StuffType getType(String type) {
		if(type.equals("HEAD")) {
			return StuffType.HEAD;
		}
		else if(type.equals("NECKLACE")) {
			return StuffType.NECKLACE;
		}
		else if(type.equals("SHOULDERS")) {
			return StuffType.SHOULDERS;
		}
		else if(type.equals("CHEST")) {
			return StuffType.CHEST;
		}
		else if(type.equals("BACK")) {
			return StuffType.BACK;
		}
		else if(type.equals("WRISTS")) {
			return StuffType.WRISTS;
		}
		else if(type.equals("GLOVES")) {
			return StuffType.GLOVES;
		}
		else if(type.equals("BELT")) {
			return StuffType.BELT;
		}
		else if(type.equals("LEGGINGS")) {
			return StuffType.LEGGINGS;
		}
		else if(type.equals("BOOTS")) {
			return StuffType.BOOTS;
		}
		else if(type.equals("RING")) {
			return StuffType.RING;
		}
		else if(type.equals("TRINKET")) {
			return StuffType.TRINKET;
		}
		else if(type.equals("MAINHAND")) {
			return StuffType.MAINHAND;
		}
		else if(type.equals("OFFHAND")) {
			return StuffType.OFFHAND;
		}
		else if(type.equals("RANGED")) {
			return StuffType.RANGED;
		}
		return null;
	}
	
	public static Wear getWear(String wear) {
		if(wear.equals("CLOTH")) {
			return Wear.CLOTH;
		}
		if(wear.equals("LEATHER")) {
			return Wear.LEATHER;
		}
		if(wear.equals("MAIL")) {
			return Wear.MAIL;
		}
		if(wear.equals("PLATE")) {
			return Wear.PLATE;
		}
		return Wear.NONE;
	}
	
	public static int getNumberStuffLoaded() {
		return numberStuffLoaded;
	}
	
	public static GemBonusType convBonusType(String bonus) {
		if(bonus.equals("STAMINA")) {
			return GemBonusType.STAMINA;
		}
		if(bonus.equals("STRENGTH")) {
			return GemBonusType.STRENGTH;
		}
		if(bonus.equals("ARMOR")) {
			return GemBonusType.ARMOR;
		}
		if(bonus.equals("CRITICAL")) {
			return GemBonusType.CRITICAL;
		}
		if(bonus.equals("MANA")) {
			return GemBonusType.MANA;
		}
		if(bonus.equals("NONE")) {
			return GemBonusType.NONE;
		}
		return null;
	}
}

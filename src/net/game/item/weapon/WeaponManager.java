package net.game.item.weapon;

import java.sql.SQLException;
import java.util.HashMap;

import jdo.JDOStatement;
import net.Server;
import net.game.item.gem.GemBonusType;
import net.game.item.gem.GemManager;
import net.game.item.gem.GemColor;
import net.game.item.stuff.Stuff;
import net.game.item.stuff.StuffManager;
import net.game.unit.ClassType;

public class WeaponManager {
	
	public final static String LOAD_WEAPON_REQUEST = "SELECT id, name, sprite_id, class, type, slot, quality, color1, color2, color3, gem_bonus_type, gem_bonus_value, level, armor, stamina, mana, critical, strength, sellprice FROM item_weapon";
	private static HashMap<Integer, Stuff> weaponList = new HashMap<Integer, Stuff>();
	private static JDOStatement loadWeapons;
	
	public static void loadWeapons() throws SQLException {
		if(loadWeapons == null) {
			loadWeapons = Server.getJDO().prepare(LOAD_WEAPON_REQUEST);
		}
		loadWeapons.clear();
		loadWeapons.execute();
		while(loadWeapons.fetch()) {
			int id = loadWeapons.getInt();
			String name = loadWeapons.getString();
			String sprite_id = loadWeapons.getString();
			short classeTemp = loadWeapons.getShort();
			ClassType[] classeType = StuffManager.getClasses(classeTemp);
			String tempType = loadWeapons.getString();
			WeaponType type = getType(tempType);
			String tempSlot = loadWeapons.getString();
			WeaponSlot slot = getSlot(tempSlot);
			byte quality = loadWeapons.getByte();
			String tempColor = loadWeapons.getString();
			GemColor color1 = GemManager.convColor(tempColor);
			tempColor = loadWeapons.getString();
			GemColor color2 = GemManager.convColor(tempColor);
			tempColor = loadWeapons.getString();
			GemColor color3 = GemManager.convColor(tempColor);
			String tempBonusType = loadWeapons.getString();
			GemBonusType bonusType = StuffManager.convBonusType(tempBonusType);
			int bonusValue = loadWeapons.getInt();
			byte level = loadWeapons.getByte();
			int armor = loadWeapons.getInt();
			int stamina = loadWeapons.getInt();
			int mana = loadWeapons.getInt();
			int critical = loadWeapons.getInt();
			int strength = loadWeapons.getInt();
			int sellPrice = loadWeapons.getInt();
			Stuff newPiece = new Stuff(id, name, sprite_id, classeType, type, slot, quality, color1, color2, color3, bonusType, bonusValue, level, armor, stamina, mana, critical, strength, sellPrice);
			weaponList.put(id, newPiece);
		}
	}
	public static Stuff getWeapon(int id) {
		if(weaponList.containsKey(id)) {
			return weaponList.get(id);
		}
		return null;
	}
	
	public static boolean exists(int id) {
		return weaponList.containsKey(id);
	}
	
	public static Stuff getClone(int id) {
		Stuff temp = getWeapon(id);
		if(temp != null) {
			return new Stuff(temp, 0);
		}
		return null;
	}
	
	public static WeaponType getType(String type) {
		if(type.equals("DAGGER")) {
			return WeaponType.DAGGER;
		}
		if(type.equals("FISTWEAPON")) {
			return WeaponType.FISTWEAPON;
		}
		if(type.equals("ONEHANDEDAXE")) {
			return WeaponType.ONEHANDEDAXE;
		}
		if(type.equals("TWOHANDEDAXE")) {
			return WeaponType.TWOHANDEDAXE;
		}
		if(type.equals("ONEHANDEDSWORD")) {
			return WeaponType.ONEHANDEDSWORD;
		}
		if(type.equals("TWOHANDEDSWORD")) {
			return WeaponType.TWOHANDEDSWORD;
		}
		if(type.equals("ONEHANDEDMACE")) {
			return WeaponType.ONEHANDEDMACE;
		}
		if(type.equals("TWOHANDEDMACE")) {
			return WeaponType.TWOHANDEDMACE;
		}
		if(type.equals("POLEARM")) {
			return WeaponType.POLEARM;
		}
		if(type.equals("STAFF")) {
			return WeaponType.STAFF;
		}
		if(type.equals("BOW")) {
			return WeaponType.BOW;
		}
		if(type.equals("CROSSBOW")) {
			return WeaponType.CROSSBOW;
		}
		if(type.equals("GUN")) {
			return WeaponType.GUN;
		}
		if(type.equals("THROWN")) {
			return WeaponType.THROWN;
		}
		if(type.equals("WAND")) {
			return WeaponType.WAND;
		}
		return null;
	}
	
	public static WeaponSlot getSlot(String slot) {
		if(slot.equals("OFFHAND")) {
			return WeaponSlot.OFFHAND;
		}
		if(slot.equals("MAINHAND")) {
			return WeaponSlot.MAINHAND;
		}
		if(slot.equals("RANGED")) {
			return WeaponSlot.RANGED;
		}
		if(slot.equals("BOTH")) {
			return WeaponSlot.BOTH;
		}
		return null;
	}
}

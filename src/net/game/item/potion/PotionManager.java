package net.game.item.potion;

import java.sql.SQLException;
import java.util.HashMap;

import jdo.JDOStatement;
import net.Server;

public class PotionManager {
	
	public final static String LOAD_POTION_REQUEST = "SELECT id, sprite_id, name, level, heal, mana, sellprice FROM item_potion";
	private static HashMap<Integer, Potion> potionList = new HashMap<Integer, Potion>();
	private static int numberPotionLoaded;
	private static JDOStatement loadPotions;
	
	public static void loadPotions() throws SQLException {
		if(loadPotions == null) {
			loadPotions = Server.getJDO().prepare(LOAD_POTION_REQUEST);
		}
		loadPotions.clear();
		loadPotions.execute();
		while(loadPotions.fetch()) {
			int id = loadPotions.getInt();
			String sprite_id = loadPotions.getString();
			String name = loadPotions.getString();
			byte level = loadPotions.getByte();
			int heal = loadPotions.getInt();
			int mana = loadPotions.getInt();
			int sellPrice = loadPotions.getInt();
			Potion newPotion = new Potion(id, sprite_id, name, level, heal, mana, sellPrice, 1);
			potionList.put(id, newPotion);
			numberPotionLoaded++;
		}
	}
	
	public static Potion getPotion(int id) {
		if(potionList.containsKey(id)) {
			return potionList.get(id);
		}
		return null;
	}
	
	public static Potion getClone(int id) {
		Potion temp = getPotion(id);
		if(temp != null) {
			return new Potion(temp);
		}
		return null;
	}
	
	public static boolean exists(int id) {
		return potionList.containsKey(id);
	}
	
	public static int getNumberPotionLoaded() {
		return numberPotionLoaded;
	}
}

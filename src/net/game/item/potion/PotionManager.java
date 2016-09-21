package net.game.item.potion;

import java.sql.SQLException;
import java.util.ArrayList;

import jdo.JDOStatement;
import net.Server;

public class PotionManager {
	
	private static ArrayList<Potion> potionList = new ArrayList<Potion>();
	private static int numberPotionLoaded;
	private static JDOStatement loadPotions;
	
	public static void loadPotions() throws SQLException {
		if(loadPotions == null) {
			loadPotions = Server.getJDO().prepare("SELECT id, sprite_id, name, level, heal, mana, sellprice FROM item_potion");
		}
		loadPotions.clear();
		loadPotions.execute();
		while(loadPotions.fetch()) {
			int id = loadPotions.getInt();
			String sprite_id = loadPotions.getString();
			String name = loadPotions.getString();
			int level = loadPotions.getInt();
			int heal = loadPotions.getInt();
			int mana = loadPotions.getInt();
			int sellPrice = loadPotions.getInt();
			Potion newPotion = new Potion(id, sprite_id, name, level, heal, mana, sellPrice);
			potionList.add(newPotion);
			numberPotionLoaded++;
		}
	}
	
	public static Potion getPotion(int id) {
		int i = 0;
		while(i < potionList.size()) {
			if(potionList.get(i).getId() == id) {
				return potionList.get(i);
			}
			i++;
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
		return getPotion(id) != null;
	}
	
	public static ArrayList<Potion> getPotionList() {
		return potionList;
	}
	
	public static int getNumberPotionLoaded() {
		return numberPotionLoaded;
	}
}

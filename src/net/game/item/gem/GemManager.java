package net.game.item.gem;

import java.sql.SQLException;
import java.util.HashMap;

import jdo.JDOStatement;
import net.Server;

public class GemManager {

	private static HashMap<Integer, Gem> gemList = new HashMap<Integer, Gem>();
	private static JDOStatement loadGems;
	
	public static void loadGems() throws SQLException {
		if(loadGems == null) {
			loadGems = Server.getJDO().prepare("SELECT id, sprite_id, name, quality, color, sellprice, pa, intellect, stamina, defense, mp5, mana, critical, spell_critical, spell_damage, heal FROM item_gem");
		}
		loadGems.clear();
		loadGems.execute();
		while(loadGems.fetch()) {
			int id = loadGems.getInt();
			String sprite_id = loadGems.getString();
			String name = loadGems.getString();
			int quality = loadGems.getInt();
			String tempColor = loadGems.getString();
			GemColor color = convColor(tempColor);
			int sellPrice = loadGems.getInt();
			int pa = loadGems.getInt();
			int intellect = loadGems.getInt();
			int stamina = loadGems.getInt();
			int defense = loadGems.getInt();
			int mp5 = loadGems.getInt();
			int mana = loadGems.getInt();
			int critical = loadGems.getInt();
			int spell_critical = loadGems.getInt();
			int spell_damage = loadGems.getInt();
			int heal = loadGems.getInt();
			gemList.put(id, new Gem(id, sprite_id, name, quality, color, pa, stamina, defense, mana, critical, sellPrice));
		}
	}
	
	public static GemColor convColor(String color) {
		if(color.equals("RED")) {
			return GemColor.RED;
		}
		if(color.equals("YELLOW")) {
			return GemColor.YELLOW;
		}
		if(color.equals("GREEN")) {
			return GemColor.GREEN;
		}
		if(color.equals("BLUE")) {
			return GemColor.BLUE;
		}
		if(color.equals("PURPLE")) {
			return GemColor.PURPLE;
		}
		if(color.equals("ORANGE")) {
			return GemColor.ORANGE;
		}
		if(color.equals("NONE")) {
			return GemColor.NONE;
		}
		return null;
	}
	
	public static Gem getGem(int id) {
		if(gemList.containsKey(id)) {
			return gemList.get(id);
		}
		return null;
	}
	
	public static Gem getClone(int id) {
		Gem temp = getGem(id);
		if(temp != null) {
			return new Gem(temp);
		}
		return null;
	}
	
	public static boolean exists(int id) {
		return gemList.containsKey(id);
	}
}

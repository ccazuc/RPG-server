package net.game.item.gem;

import java.sql.SQLException;
import java.util.HashMap;

import jdo.JDOStatement;
import net.Server;

public class GemManager {

	public final static String LOAD_GEM_REQUEST = "SELECT id, sprite_id, name, quality, color, sellprice, stat1Type, stat1Value, stat2Type, stat2Value, stat3Type, stat3Value FROM item_gem";
	private static HashMap<Integer, Gem> gemList = new HashMap<Integer, Gem>();
	private static JDOStatement loadGems;
	
	public static void loadGems() throws SQLException {
		if(loadGems == null) {
			loadGems = Server.getJDO().prepare(LOAD_GEM_REQUEST);
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
			GemBonusType stat1Type = convGemBonusType(loadGems.getString());
			int stat1Value = loadGems.getInt();
			GemBonusType stat2Type = convGemBonusType(loadGems.getString());
			int stat2Value = loadGems.getInt();
			GemBonusType stat3Type = convGemBonusType(loadGems.getString());
			int stat3Value = loadGems.getInt();
			gemList.put(id, new Gem(id, sprite_id, name, quality, color, sellPrice, stat1Type, stat1Value, stat2Type, stat2Value, stat3Type, stat3Value));
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
	
	public static GemBonusType convGemBonusType(String bonus) {
		if(bonus.equals("STRENGTH")) {
			return GemBonusType.STRENGTH;
		}
		if(bonus.equals("STAMINA")) {
			return GemBonusType.STAMINA;
		}
		if(bonus.equals("INTELLIGENCE")) {
			return GemBonusType.INTELLIGENCE;
		}
		if(bonus.equals("CRITICAL")) {
			return GemBonusType.CRITICAL;
		}
		if(bonus.equals("SPELL_CRITICAL")) {
			return GemBonusType.SPELL_CRITICAL;
		}
		if(bonus.equals("HASTE")) {
			return GemBonusType.HASTE;
		}
		if(bonus.equals("SPELL_HASTE")) {
			return GemBonusType.SPELL_HASTE;
		}
		if(bonus.equals("MP5")) {
			return GemBonusType.MP5;
		}
		if(bonus.equals("HEALING_POWER")) {
			return GemBonusType.HEALING_POWER;
		}
		if(bonus.equals("NONE")) {
			return GemBonusType.NONE;
		}
		System.out.println("Error GemManager:convGemBonusType value : "+bonus);
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

package net.game.item.bag;

import java.sql.SQLException;
import java.util.HashMap;

import jdo.JDOStatement;
import net.Server;

public class BagManager {
	
	private static HashMap<Integer, Bag> containerList = new HashMap<Integer, Bag>();
	private static JDOStatement loadBags;
	
	public static void loadBags() throws SQLException {
		if(loadBags == null) {
			loadBags = Server.getJDO().prepare("SELECT id, sprite_id, name, quality, size, sellprice FROM item_container");
		}
		loadBags.clear();
		loadBags.execute();
		while(loadBags.fetch()) {
			int id = loadBags.getInt();
			String sprite_id = loadBags.getString();
			String name = loadBags.getString();
			int quality = loadBags.getInt();
			int size = loadBags.getInt();
			int sellPrice = loadBags.getInt();
			Bag newPiece = new Bag(id, sprite_id, name, quality, size, sellPrice);
			containerList.put(id, newPiece);
		}
	}
	
	public static Bag getContainer(int id) {
		if(containerList.containsKey(id)) {
			return containerList.get(id);
		}
		return null;
	}
	
	public static boolean exists(int id) {
		return containerList.containsKey(id);
	}
	
	public static Bag getClone(int id) {
		Bag tempContainer = getContainer(id);
		if(tempContainer != null) {
			return new Bag(tempContainer);
		}
		return null;
	}
}

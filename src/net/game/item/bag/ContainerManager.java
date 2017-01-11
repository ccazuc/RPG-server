package net.game.item.bag;

import java.sql.SQLException;
import java.util.HashMap;

import jdo.JDOStatement;
import net.Server;

public class ContainerManager {
	
	public final static String LOAD_CONTAINER_REQUEST = "SELECT id, sprite_id, name, quality, size, sellprice FROM item_container";
	private static HashMap<Integer, Container> containerList = new HashMap<Integer, Container>();
	private static JDOStatement loadBags;
	
	public static void loadContainer() throws SQLException {
		if(loadBags == null) {
			loadBags = Server.getJDO().prepare(LOAD_CONTAINER_REQUEST);
		}
		loadBags.clear();
		loadBags.execute();
		while(loadBags.fetch()) {
			int id = loadBags.getInt();
			String sprite_id = loadBags.getString();
			String name = loadBags.getString();
			byte quality = loadBags.getByte();
			byte size = loadBags.getByte();
			int sellPrice = loadBags.getInt();
			containerList.put(id, new Container(id, sprite_id, name, quality, size, sellPrice));
		}
	}
	
	public static Container getContainer(int id) {
		if(containerList.containsKey(id)) {
			return containerList.get(id);
		}
		return null;
	}
	
	public static boolean exists(int id) {
		return containerList.containsKey(id);
	}
	
	public static Container getClone(int id) {
		Container tempContainer = getContainer(id);
		if(tempContainer != null) {
			return new Container(tempContainer);
		}
		return null;
	}
}

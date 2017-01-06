package net.game.profession;

import java.sql.SQLException;
import java.util.ArrayList;

import jdo.JDOStatement;
import net.Server;
import net.game.item.Item;
import net.game.unit.Player;

public class ProfessionManager {

	private static ArrayList<CraftableItem> craftableList = new ArrayList<CraftableItem>();
	private static ArrayList<Category> categoryList = new ArrayList<Category>();
	private static ArrayList<Profession> professionList = new ArrayList<Profession>();
	private  ArrayList<Integer> unlockedCraftList = new ArrayList<Integer>();
	private static JDOStatement loadUnlockedCraft;
	private static JDOStatement addUnlockedCraft;
	private static JDOStatement loadAllCraft;
	private static JDOStatement loadCraftableItem;
	private static JDOStatement loadCategory;
	
	public ProfessionManager() {}
	
	public void loadUnlockedCraft(Player player) throws SQLException {
		if(loadUnlockedCraft == null) {
			loadUnlockedCraft = Server.getJDO().prepare("SELECT craft_id FROM craft_unlocked WHERE character_id = ?");
		}
		loadUnlockedCraft.clear();
		loadUnlockedCraft.putInt(player.getCharacterId());
		loadUnlockedCraft.execute();
		while(loadUnlockedCraft.fetch()) {
			int id = loadUnlockedCraft.getInt();
			this.unlockedCraftList.add(id);
		}
	}
	
	public void addUnlockedCraft(Player player, int id) throws SQLException {
		if(!this.unlockedCraftList.contains(id)) {
			if(addUnlockedCraft == null) {
				addUnlockedCraft = Server.getJDO().prepare("INSERT INTO craft_unlocked (character_id, craft_id) VALUES (?, ?)");
			}
			this.unlockedCraftList.add(id);
			addUnlockedCraft.clear();
			addUnlockedCraft.putInt(player.getCharacterId());
			addUnlockedCraft.putInt(id);
			addUnlockedCraft.execute();
		}
	}

	public static void LoadAllCraft() throws SQLException {	
		if(loadAllCraft == null) {
			loadAllCraft = Server.getJDO().prepare("SELECT id, level, craft_time, item1, item2, item3, item4, item5, item6 FROM craft_item");
		}
		loadAllCraft.clear();
		loadAllCraft.execute();
		while(loadAllCraft.fetch()) {
			int id = loadAllCraft.getInt();
			int level = loadAllCraft.getInt();
			int craftTime = loadAllCraft.getInt();
			Item item = Item.getItem(id);
			String tempItem = loadAllCraft.getString();
			Item ressource1 = Item.getItem(Integer.valueOf(tempItem.split(":")[0]));
			int ressource1Amount = Integer.valueOf(tempItem.split(":")[1]);
			tempItem = loadAllCraft.getString();
			Item ressource2 = Item.getItem(Integer.valueOf(tempItem.split(":")[0]));
			int ressource2Amount = Integer.valueOf(tempItem.split(":")[1]);
			tempItem = loadAllCraft.getString();
			Item ressource3 = Item.getItem(Integer.valueOf(tempItem.split(":")[0]));
			int ressource3Amount = Integer.valueOf(tempItem.split(":")[1]);
			tempItem = loadAllCraft.getString();
			Item ressource4 = Item.getItem(Integer.valueOf(tempItem.split(":")[0]));
			int ressource4Amount = Integer.valueOf(tempItem.split(":")[1]);
			tempItem = loadAllCraft.getString();
			Item ressource5 = Item.getItem(Integer.valueOf(tempItem.split(":")[0]));
			int ressource5Amount = Integer.valueOf(tempItem.split(":")[1]);
			tempItem = loadAllCraft.getString();
			Item ressource6 = Item.getItem(Integer.valueOf(tempItem.split(":")[0]));
			int ressource6Amount = Integer.valueOf(tempItem.split(":")[1]);
			ArrayList<Item> itemList = new ArrayList<Item>();
			if(ressource1 != null) {
				itemList.add(ressource1);
			}
			if(ressource2 != null) {
				itemList.add(ressource2);
			}
			if(ressource3 != null) {
				itemList.add(ressource3);
			}
			if(ressource4 != null) {
				itemList.add(ressource4);
			}
			if(ressource5 != null) {
				itemList.add(ressource5);
			}
			if(ressource6 != null) {
				itemList.add(ressource6);
			}
			ArrayList<Integer> numberList = new ArrayList<Integer>();
			if(ressource1 != null) {
				numberList.add(ressource1Amount);
			}
			if(ressource2 != null) {
				numberList.add(ressource2Amount);
			}
			if(ressource3 != null) {
				numberList.add(ressource3Amount);
			}
			if(ressource4 != null) {
				numberList.add(ressource4Amount);
			}
			if(ressource5 != null) {
				numberList.add(ressource5Amount);
			}
			if(ressource6 != null) {
				numberList.add(ressource6Amount);
			}
			craftableList.add(new CraftableItem(id, level, craftTime, item, itemList, numberList));
			//craftableList.add(new CraftableItem(id, level, item, ressource1, ressource1Amount, ressource2, ressource2Amount, ressource3, ressource3Amount, ressource4, ressource4Amount, ressource5, ressource5Amount, ressource6, ressource6Amount));
		}
		if(loadCraftableItem == null) {
			loadCraftableItem = Server.getJDO().prepare("SELECT id, name, item1, item2, item3, item4, item5, item6, item7, item8, item9, item10 FROM craft_category");
		}
		loadCraftableItem.clear();
		loadCraftableItem.execute();
		while(loadCraftableItem.fetch()) {
			int id = loadCraftableItem.getInt();
			String name = loadCraftableItem.getString();
			CraftableItem item1 = getCraftableItem(loadCraftableItem.getInt());
			CraftableItem item2 = getCraftableItem(loadCraftableItem.getInt());
			CraftableItem item3 = getCraftableItem(loadCraftableItem.getInt());
			CraftableItem item4 = getCraftableItem(loadCraftableItem.getInt());
			CraftableItem item5 = getCraftableItem(loadCraftableItem.getInt());
			CraftableItem item6 = getCraftableItem(loadCraftableItem.getInt());
			CraftableItem item7 = getCraftableItem(loadCraftableItem.getInt());
			CraftableItem item8 = getCraftableItem(loadCraftableItem.getInt());
			CraftableItem item9 = getCraftableItem(loadCraftableItem.getInt());
			CraftableItem item10 = getCraftableItem(loadCraftableItem.getInt());
			categoryList.add(new Category(id, name, item1, item2, item3, item4, item5, item6, item7, item8, item9, item10));
		}
		if(loadCategory == null) {
			loadCategory = Server.getJDO().prepare("SELECT name, id, category1, category2, category3, category4, category5, category6, category7, category8 FROM craft_profession");
		}
		loadCategory.clear();
		loadCategory.execute();
		while(loadCategory.fetch()) {
			String professionName = loadCategory.getString();
			int professionId = loadCategory.getInt();
			Category category1 = getCategory(loadCategory.getInt());
			Category category2 = getCategory(loadCategory.getInt());
			Category category3 = getCategory(loadCategory.getInt());
			Category category4 = getCategory(loadCategory.getInt());
			Category category5 = getCategory(loadCategory.getInt());
			Category category6 = getCategory(loadCategory.getInt());
			Category category7 = getCategory(loadCategory.getInt());
			Category category8 = getCategory(loadCategory.getInt());
			professionList.add(new Profession(professionId, professionName, category1, category2, category3, category4, category5, category6, category7, category8));
		}
	}
	
	private static CraftableItem getCraftableItem(int id) {
		int i = 0;
		while(i < craftableList.size()) {
			if(craftableList.get(i).getId() == id) {
				return new CraftableItem(craftableList.get(i));
			}
			i++;
		}
		return null;
	}
	
	private static Category getCategory(int id) {
		int i = 0;
		while(i < categoryList.size()) {
			if(categoryList.get(i).getId() == id) {
				return categoryList.get(i);
			}
			i++;
		}
		return null;
	}
	
	public static Profession getProfession(int id) {
		int i = 0;
		while(i < professionList.size()) {
			if(professionList.get(i).getId() == id) {
				return professionList.get(i);
			}
			i++;
		}
		return null;
	}
	
	/*private boolean craftExists(int id) {
		int i = 0;
		while(i < this.unlockedCraftList.size()) {
			if(this.unlockedCraftList.get(i) == id) {
				return true;
			}
			i++;
		}
		return false;
	}*/
	
	public static ArrayList<Profession> getProfessionList() {
		return professionList;
	}
}

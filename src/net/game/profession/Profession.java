package net.game.profession;

import java.util.ArrayList;

import net.game.unit.Player;

public class Profession {

	private ArrayList<Category> categoryList = new ArrayList<Category>();
	private String name;
	private int id;
	private int playerLevel = 370;
	
	public Profession(int id, String name, Category category1, Category category2, Category category3, Category category4, Category category5, Category category6, Category category7, Category category8) {
		this.id = id;
		this.name = name;
		this.addCategory(category1);
		this.addCategory(category2);
		this.addCategory(category3);
		this.addCategory(category4);
		this.addCategory(category5);
		this.addCategory(category6);
		this.addCategory(category7);
		this.addCategory(category8);
	}
	
	public ArrayList<Category> getCategoryList() {
		return this.categoryList;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void addCategory(Category category) {
		if(category != null) {
			this.categoryList.add(category);
		}
	}
	
	public boolean canCraft(Player player, CraftableItem item) {
		int i = 0;
		while(i < item.getNeededItemList().size()) {
			if(player.getBag().getItemNumber(item.getNeededItem(i)) < item.getNeededItemNumber(i)) {
				return false;
			}
			i++;
		}
		return true;
	}
	
	public int getPlayerLevel() {
		return this.playerLevel;
	}
}

package net.game.profession;

import java.util.ArrayList;


public class Category {

	private ArrayList<CraftableItem> craftList = new ArrayList<CraftableItem>();
	private int id;
	private String name;
	private boolean expand = true;
	private boolean mouseHover;
	private boolean mouseDown;
	
	public Category(int id, String name, CraftableItem item1, CraftableItem item2, CraftableItem item3, CraftableItem item4, CraftableItem item5, CraftableItem item6, CraftableItem item7, CraftableItem item8, CraftableItem item9, CraftableItem item10) {
		this.name = name;
		this.id = id;
		this.addItem(item1);
		this.addItem(item2);
		this.addItem(item3);
		this.addItem(item4);
		this.addItem(item5);
		this.addItem(item6);
		this.addItem(item7);
		this.addItem(item8);
		this.addItem(item9);
		this.addItem(item10);
		this.orderList();
	}
	
	public void orderList() {
		int i = 0;
		while(i < this.craftList.size()-1) {
			if(this.craftList.get(i).getLevel() < this.craftList.get(i+1).getLevel()) {
				CraftableItem temp = this.craftList.get(i);
				this.craftList.set(i, this.craftList.get(i+1));
				this.craftList.set(i+1, temp);
				i = 0;
			}
			else {
				i++;
			}
		}
	}
	
	public ArrayList<CraftableItem> getCraftList() {
		return this.craftList;
	}
	
	public boolean getMouseDown() {
		return this.mouseDown;
	}
	
	public void setMouseDown(boolean down) {
		this.mouseDown = down;
	}
	
	public boolean getMouseHover() {
		return this.mouseHover;
	}
	
	public void setMouseHover(boolean hover) {
		this.mouseHover = hover;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setExpand(boolean expand) {
		this.expand = expand;
	}
	
	public boolean getExpand() {
		return this.expand;
	}
	
	public void addItem(CraftableItem item) {
		if(item != null) {
			this.craftList.add(item);
		}
	}
}

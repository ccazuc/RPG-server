package net.game.profession;

import java.util.ArrayList;

import net.game.item.Item;

public class CraftableItem {

	private int id;
	private int level;
	private Item item;
	private int craftTime;
	private boolean mouseHover;
	private boolean mouseDown;
	private ArrayList<Item> itemList;
	private ArrayList<Integer> numberList;
	
	public CraftableItem(int id, int level, int craftTime, Item item, ArrayList<Item> itemList, ArrayList<Integer> numberList) {	
		this.id = id;
		this.level = level;
		this.craftTime = craftTime;
		this.item = item;
		this.itemList = itemList;
		this.numberList = numberList;
	}
	
	public CraftableItem(CraftableItem craftableItem) {
		this.id = craftableItem.id;
		this.level = craftableItem.level;
		this.craftTime = craftableItem.craftTime;
		this.item = craftableItem.item;
		this.itemList = craftableItem.itemList;
		this.numberList = craftableItem.numberList;
	}
	
	public Item getNeededItem(int number) {
		return this.itemList.get(number);
	}
	
	public int getNeededItemNumber(int number) {
		return this.numberList.get(number);
	}
	
	public ArrayList<Item> getNeededItemList() {
		return this.itemList;
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getCraftLength() {
		return this.craftTime;
	}
	
	public boolean getMouseHover() {
		return this.mouseHover;
	}
	
	public void setMouseHover(boolean hover) {
		this.mouseHover = hover;
	}
	
	public boolean getMouseDown() {
		return this.mouseDown;
	}
	
	public void setMouseDown(boolean down) {
		this.mouseDown = down;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public Item getItem() {
		return this.item;
	}
}

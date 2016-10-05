package net.game.item.bag;

import java.util.HashMap;

import net.game.item.Item;


public class Bag extends Item implements Cloneable {
	
	private Item[] bag = new Item[16];
	private Container[] equippedBag = new Container[4];
	private boolean bagChange = true;
	
	private static HashMap<Item, Integer> numberStack = new HashMap<Item, Integer>();
	private static HashMap<Integer, Integer> itemList = new HashMap<Integer, Integer>();
	
	public Bag() {}
	
	public Item[] getBag() {
		return this.bag;
	}
	
	public void setBag(Item[] bag) {
		this.bag = bag;
	}
	
	public Container[] getEquippedBag() {
		return this.equippedBag;
	}
	
	public void setEquippedBag(int i, Container bag) {
		this.equippedBag[i] = bag;
	}
	
	public Container getEquippedBag(int i) {
		return this.equippedBag[i];
	}
	
	@Override
	public String getSpriteId()  {
		return this.sprite_id;
	}
	
	public String getSpriteId(int i) {
		if(i < this.equippedBag.length) {
			return this.equippedBag[i].getSpriteId();
		}
		return null;
	}
	
	public int getEquippedBagSize(int i) {
		if(i < this.equippedBag.length && this.equippedBag[i] != null) {
			return this.equippedBag[i].getSize();
		}
		return 0;
	}
	
	public int getNumberItemInBags(int id) {
		if(itemList.containsKey(id)) {
			return itemList.get(id);
		}
		return 0;
	}
	
	public HashMap<Item, Integer> getNumberStack() {
		return numberStack;
	}
	
	public HashMap<Integer, Integer> getItemList() {
		return itemList;
	}
	
	public int getItemNumber(Item item) {
		if(itemList.containsKey(item.getId())) {
			return itemList.get(item.getId());
		}
		return 0;
	}
	
	public Item getEquals(Item item) {
		int i = 0;
		while(i < numberStack.size()) {
			if(numberStack.get(i) != null && numberStack.get(i).equals(item)) {
				return getKey(item);
			}
			i++;
		}
		return null;
	}
	
	public static Item getKey(Item item){
	    for(Item key : numberStack.keySet()) {
	       if(key.getId() == item.getId()) {
	        	return key;
	        }
	    }
	    return null;
	}
	
	public int getNumberBagItem(Item item) {
		return numberStack.get(item);
	}
	
	public Item getBag(int i) {
		if(i >= 0 && i < this.bag.length && this.bag[i] != null) {
			return this.bag[i];
		}
		return null;
	}
	
	public void setBag(int i, Item stuff) {
		if(i >= 0 && i < this.bag.length) {
			this.bag[i] = stuff;
		}
	}
	
	public void setBag(int i, Item stuff, int number) {
		if(i >= 0 && i < this.bag.length) {
			if(number <= 0) {
				this.bag[i] = null;
				numberStack.remove(stuff);
			}
			else {
				this.bag[i] = stuff;
				numberStack.put(stuff, number);
			}
		}
	}
	
	@Override
	public int getId() {
		return this.id;
	}
	
	public void setBagChange(boolean we) {
		this.bagChange = we;
	}
	
	public boolean getBagChange() {
		return this.bagChange;
	}
}

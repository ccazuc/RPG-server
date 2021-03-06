package net.game.item.bag;

import java.util.HashMap;

import net.game.item.Item;
import net.thread.log.LogRunnable;


public class Bag extends Item implements Cloneable {
	
	private Item[] bag = new Item[16];
	private Container[] equippedBag = new Container[4];
	private boolean bagChange = true;
	private int numberFreeSlotBag;
	
	private HashMap<Integer, Integer> itemList = new HashMap<Integer, Integer>();
	
	public Bag() {}
	
	public Item[] getBag() {
		return this.bag;
	}
	
	public void setBag(Item[] bag) {
		this.bag = bag;
	}
	
	public void updateBagItem() {
		this.numberFreeSlotBag = 0;
		this.itemList.clear();
		int i = 0;
		while(i < this.bag.length) {
			if(this.bag[i] != null) {
				if(this.itemList.containsKey(this.bag[i].getId())) {
					if(this.bag[i].isStackable()) {
						this.itemList.put(this.bag[i].getId(), this.bag[i].getAmount()+this.itemList.get(this.bag[i].getId()));
					}
					else {
						this.itemList.put(this.bag[i].getId(), this.itemList.get(this.bag[i].getId())+1);
					}
				}
				else {
					if(this.bag[i].isStackable()) {
						this.itemList.put(this.bag[i].getId(), this.bag[i].getAmount());
					}
					else {
						this.itemList.put(this.bag[i].getId(), 1);
					}
				}
			}
			else {
				this.numberFreeSlotBag++;
			}
			i++;
		}
	}
	
	public int getNumberFreeSlotBag() {
		return this.numberFreeSlotBag;
	}
	
	public Container[] getEquippedBag() {
		return this.equippedBag;
	}
	
	public void setEquippedBag(int i, Container bag) {
		if(i < 0 || i >= this.equippedBag.length) {
			LogRunnable.addErrorLog("Error in Bag.setEquippedBag(), incorrect index: "+i);
			return;
		}
		int length = this.bag.length;
		Item[] tempBag = this.bag.clone();
		if(this.equippedBag[i] != null) {
			int tempBagSize = this.equippedBag[i].getSize();
			this.equippedBag[i] = bag;
			if(bag != null) {
				if(tempBagSize >= this.equippedBag[i].getSize()) {
					this.bag = new Item[length+(tempBagSize-this.equippedBag[i].getSize())];
				}
				else {
					this.bag = new Item[length+(this.equippedBag[i].getSize()-tempBagSize)];
				}
			}
			else {
				this.bag = new Item[length-tempBagSize];
			}
		}
		else {
			this.equippedBag[i] = bag;
			if(bag != null) {
				this.bag = new Item[length+this.equippedBag[i].getSize()];
			}
		}
		int j = 0;
		while(j < tempBag.length && j < this.bag.length) {
			this.bag[j] = tempBag[j];
			j++;
		}
	}
	
	public Container getEquippedBag(int i) {
		if (i < 0 || i >= this.equippedBag.length)
		{
			LogRunnable.addErrorLog("Error in Bag.getEquippedBag(), incorrect index: "+i);
			return (null);
		}
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
		if(this.itemList.containsKey(id)) {
			return this.itemList.get(id);
		}
		return 0;
	}
	
	public HashMap<Integer, Integer> getItemList() {
		return this.itemList;
	}
	
	public int getItemNumber(Item item) {
		if(this.itemList.containsKey(item.getId())) {
			return this.itemList.get(item.getId());
		}
		return 0;
	}
	
	public Item getBag(int i) {
		if(i >= 0 && i < this.bag.length) {
			return this.bag[i];
		}
		return null;
	}
	
	public void setBag(int i, Item stuff) {
		if(i < 0 || i >= this.bag.length) {
			return;
		}
		this.bag[i] = stuff;
		this.bagChange = true;
	}
	
	public void setBag(int i, Item stuff, int number) {
		if(i < 0 || i >= this.bag.length) {
			return;
		}
		if(number <= 0) {
			this.bag[i] = null;
		}
		else {
			this.bag[i] = stuff;
			this.bag[i].setAmount(number);
		}
		this.bagChange = true;
	}
	
	@Override
	public int getId() {
		return this.id;
	}
	
	public boolean getBagChange() {
		return this.bagChange;
	}
}

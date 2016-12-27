package net.game;

import net.command.player.CommandTrade;
import net.game.item.Item;

public class Trade {

	private Player tradeInit;
	private Player tradeTarget;
	private boolean tradeInitAccepted;
	private boolean tradeTargetAccepted;
	private Item[] tradeInitTable;
	private Item[] tradeTargetTable;
	
	public Trade(Player tradeInit, Player tradeTarget) {
		this.tradeInit = tradeInit;
		this.tradeTarget = tradeTarget;
		init();
	}
	
	public void init() {
		this.tradeInitTable = new Item[7];
		this.tradeTargetTable = new Item[7];
	}
	
	public void addItem(Player player, int slot, int amount, Item item) {
		if(slot >= 0 && slot <= 6) {
			if(player == this.tradeInit) {
				this.tradeInitTable[slot] = item;
				if(this.tradeInitTable[slot] != null) {
					this.tradeInitTable[slot].setAmount(amount);
				}
			}
			else if(player == this.tradeTarget) {
				this.tradeTargetTable[slot] = item;
				if(this.tradeTargetTable[slot] != null) {
					this.tradeTargetTable[slot].setAmount(amount);
				}
			}
		}
	}
	
	public void setTradeState(Player player, boolean we) {
		if(player == this.tradeInit) {
			this.tradeInitAccepted = we;
		}
		else if(player == this.tradeTarget) {
			this.tradeTargetAccepted = we;
		}
	}
	
	public void exchangeItem() {
		int i = 0;
		int number = 0;
		while(i < this.tradeInitTable.length-1) {
			if(this.tradeInitTable[i] != null) {
				number++;
			}
			i++;
		}
		if(number > this.tradeTarget.getBag().getNumberFreeSlotBag()) {
			//player has not enough free space
			return;
		}
		i = 0;
		number = 0;
		while(i < this.tradeTargetTable.length-1) {
			if(this.tradeTargetTable[i] != null)  {
				number++;
			}
			i++;
		}
		if(number > this.tradeInit.getBag().getNumberFreeSlotBag()) {
			//player has not enough free space
			return;
		}
		System.out.println("Exchange Item");
		CommandTrade.sendTradeItems(this.tradeInit);
	}
	
	public Item[] getTradeInitTable() {
		return this.tradeInitTable;
	}
	
	public Item[] getTradeTargetTable() {
		return this.tradeTargetTable;
	}
	
	public Item[] getTradeTable(Player player) {
		if(player == this.tradeInit) {
			return this.tradeInitTable;
		}
		if(player == this.tradeTarget) {
			return this.tradeTargetTable;
		}
		return null;
	}
	
	public boolean getTradeInitState() {
		return this.tradeInitAccepted;
	}
	
	public boolean getTradeTargetState() {
		return this.tradeTargetAccepted;
	}
 }

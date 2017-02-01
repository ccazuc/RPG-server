package net.game.auction;

import net.Server;
import net.game.item.Item;
import net.game.unit.Player;

public class AuctionEntry {

	private final int entryID;
	private final Item item;
	private final int buyoutPrice;
	private final int initialBidPrice;
	private int lastBidderID;
	private int bidPrice;
	private final int sellerID;
	private final String sellerName;
	private final long depositTimer;
	private final long auctionEndTimer;
	private final boolean canBeBuy;
	private boolean locked;
	
	public AuctionEntry(int entryID, Player seller, Item item, int buyoutPrice, int initialBidPrice, AuctionHouseDuration duration) {
		this.item = item;
		this.entryID = entryID;
		this.buyoutPrice = buyoutPrice;
		this.sellerID = seller.getUnitID();
		this.sellerName = seller.getName();
		this.initialBidPrice = initialBidPrice;
		this.depositTimer = Server.getLoopTickTimer();
		this.auctionEndTimer = Server.getLoopTickTimer()+duration.getDuration();
		this.canBeBuy = buyoutPrice > 0;
	}
	
	public AuctionEntry(int entryID, int sellerID, String sellerName, Item item, int buyoutPrice, int initialBidPrice, int bidPrice, int lastBidderID, int timeLeft, long depositTimer) {
		this.entryID = entryID;
		this.sellerID = sellerID;
		this.sellerName = sellerName;
		this.item = item;
		this.buyoutPrice = buyoutPrice;
		this.initialBidPrice = initialBidPrice;
		this.bidPrice = bidPrice;
		this.lastBidderID = lastBidderID;
		this.canBeBuy = buyoutPrice > 0;
		this.depositTimer = depositTimer;
		this.auctionEndTimer = depositTimer+timeLeft;
	}
	
	public AuctionHouseDuration getUpdatedDuration() {
		int timeLeft = (int)(this.auctionEndTimer-Server.getLoopTickTimer());
		return AuctionHouseDuration.getDuration(timeLeft);
	}
	
	public int getTimeLeft() {
		return (int)(this.auctionEndTimer-Server.getLoopTickTimer());
	}
	
	public boolean canBeBuy() {
		return this.canBeBuy;
	}
	
	public void lock() {
		this.locked = true;
	}
	
	public boolean isLocked() {
		return this.locked;
	}
	
	public int getLastBidderID() {
		return this.lastBidderID;
	}
	
	public void setLastBidderID(int bidderID) {
		this.lastBidderID = bidderID;
	}
	
	public void setBid(int bid) {
		this.bidPrice = bid;
	}
	
	public int getEntryID() {
		return this.entryID;
	}
	
	public String getSellerName() {
		return this.sellerName;
	}
	
	public long getDepositTimer() {
		return this.depositTimer;
	}
	
	public long getAuctionEndTimer() {
		return this.auctionEndTimer;
	}
	
	public int getItemID() {
		return this.item.getId();
	}
	
	public Item getItem() {
		return this.item;
	}
	
	public int getBuyoutPrice() {
		return this.buyoutPrice;
	}
	
	public int getSellerID() {
		return this.sellerID;
	}
	
	public int getInitialBidPrice() {
		return this.initialBidPrice;
	}
	
	public int getBidPrice() {
		return this.bidPrice;
	}
}

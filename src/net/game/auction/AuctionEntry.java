package net.game.auction;

import net.Server;
import net.game.item.Item;
import net.game.unit.Player;

public class AuctionEntry {

	private int id;
	private final Item item;
	private final int buyoutPrice;
	private final int initialBidPrice;
	private int lastBidderID;
	private int bidPrice;
	private final int sellerID;
	private final String sellerName;
	private final long depositTimer;
	private final long auctionEndTimer;
	
	public AuctionEntry(Player seller, Item item, int buyoutPrice, int initialBidPrice, AuctionHouseDuration duration) {
		this.item = item;
		this.buyoutPrice = buyoutPrice;
		this.sellerID = seller.getUnitID();
		this.sellerName = seller.getName();
		this.initialBidPrice = initialBidPrice;
		this.depositTimer = Server.getLoopTickTimer();
		this.auctionEndTimer = Server.getLoopTickTimer()+duration.getDuration();
	}
	
	public AuctionHouseDuration getUpdatedDuration() {
		int timeLeft = (int)(this.auctionEndTimer-Server.getLoopTickTimer());
		return AuctionHouseDuration.getDuration(timeLeft);
	}
	
	public int getLastBidderID() {
		return this.lastBidderID;
	}
	
	public void setLastBidderID(int bidderID) {
		this.lastBidderID = bidderID;
	}
	
	public int getID() {
		return this.id;
	}
	
	public void setID(int id) {
		this.id = id;
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

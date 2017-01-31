package net.thread.auctionhouse;

import net.game.auction.AuctionHouseDuration;
import net.game.auction.AuctionHouseMgr;
import net.game.item.Item;
import net.game.unit.Player;

public class SellItemRequest implements AuctionHouseTask {
	
	private final Player player;
	private final Item item;
	private final int buyoutPrice;
	private final int bidPrice;
	private final AuctionHouseDuration duration;

	public SellItemRequest(Player player, Item item, int buyoutPrice, int bidPrice, AuctionHouseDuration duration) {
		this.player = player;
		this.item = item;
		this.buyoutPrice = buyoutPrice;
		this.bidPrice = bidPrice;
		this.duration = duration;
	}
	
	@Override
	public void execute() {
		AuctionHouseMgr.addAuction(this.player, this.item, this.bidPrice, this.buyoutPrice, this.duration);
	}
}

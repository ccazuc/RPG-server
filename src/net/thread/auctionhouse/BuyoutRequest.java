package net.thread.auctionhouse;

import net.game.auction.AuctionEntry;
import net.game.auction.AuctionHouseMgr;
import net.game.unit.Player;

public class BuyoutRequest implements AuctionHouseTask {

	private final Player player;
	private final AuctionEntry entry;
	
	public BuyoutRequest(Player player, AuctionEntry entry) {
		this.player = player;
		this.entry = entry;
	}
	
	@Override
	public void execute() {
		AuctionHouseMgr.buyoutAuction(this.player, this.entry);
	}
}

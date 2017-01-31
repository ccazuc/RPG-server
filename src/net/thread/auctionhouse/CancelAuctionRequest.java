package net.thread.auctionhouse;

import net.game.auction.AuctionEntry;
import net.game.auction.AuctionHouseMgr;
import net.game.unit.Player;

public class CancelAuctionRequest implements AuctionHouseTask {

	private final AuctionEntry entry;
	private final Player player;
	
	public CancelAuctionRequest(Player player, AuctionEntry entry) {
		this.entry = entry;
		this.player = player;
	}
	
	@Override
	public void execute() {
		AuctionHouseMgr.cancelAuction(this.player, this.entry);
	}
}

package net.thread.auctionhouse;

import java.util.ArrayList;

import net.game.auction.AuctionHouseFilter;
import net.game.auction.AuctionHouseQualityFilter;
import net.game.auction.AuctionHouseSort;
import net.game.unit.Player;

public class AuctionHouseRunnable implements Runnable {

	private final static ArrayList<SearchRequest> searchRequestQueue = new ArrayList<SearchRequest>();
	private final static ArrayList<SearchRequest> searchRequestExecute = new ArrayList<SearchRequest>();
	
	@Override
	public void run() {
		boolean running = true;
		while(running) {
			
		}
	}
	
	public static void addSearchRequest(Player player, String search, byte minLevel, byte maxLevel, AuctionHouseQualityFilter qualityFilter, AuctionHouseSort sort, AuctionHouseFilter filter, boolean isUsable) {
		synchronized(searchRequestQueue) {
			searchRequestQueue.add(new SearchRequest(player, search, minLevel, maxLevel, qualityFilter, sort, filter, isUsable));
		}
	}
}

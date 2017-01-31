package net.thread.auctionhouse;

import java.util.ArrayList;

import net.game.auction.AuctionHouseDuration;
import net.game.auction.AuctionHouseFilter;
import net.game.auction.AuctionHouseQualityFilter;
import net.game.auction.AuctionHouseSort;
import net.game.item.Item;
import net.game.unit.Player;

public class AuctionHouseRunnable implements Runnable {

	private final static ArrayList<SearchRequest> searchRequestQueue = new ArrayList<SearchRequest>();
	private final static ArrayList<SearchRequest> searchRequestExecute = new ArrayList<SearchRequest>();
	private final static ArrayList<SellItemRequest> sellItemQueue = new ArrayList<SellItemRequest>();
	private final static ArrayList<SellItemRequest> sellItemExecute = new ArrayList<SellItemRequest>();
	private final static int LOOP_TIMER = 10;
	private static boolean shouldClose;
	
	@Override
	public void run() {
		boolean running = true;
		long delta;
		long time;
		while(running) {
			time = System.currentTimeMillis();
			synchronized(searchRequestQueue) {
				while(searchRequestQueue.size() > 0) {
					searchRequestExecute.add(searchRequestQueue.get(0));
					searchRequestQueue.remove(0);
				}
			}
			synchronized(sellItemQueue) {
				while(sellItemQueue.size() > 0) {
					sellItemExecute.add(sellItemQueue.get(0));
					sellItemQueue.remove(0);
				}
			}
			while(searchRequestExecute.size() > 0) {
				searchRequestExecute.get(0).execute();
				searchRequestExecute.remove(0);
			}
			while(sellItemExecute.size() > 0) {
				sellItemExecute.get(0).execute();
				sellItemExecute.remove(0);
			}
			delta = System.currentTimeMillis()-time;
			if(delta < LOOP_TIMER) {
				try {
					Thread.sleep((LOOP_TIMER-delta));
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(shouldClose && searchRequestQueue.size() == 0 && searchRequestExecute.size() == 0) {
				running = false;
			}
		}
	}
	
	public static void addSearchRequest(Player player, String search, short page, byte minLevel, byte maxLevel, AuctionHouseQualityFilter qualityFilter, AuctionHouseSort sort, AuctionHouseFilter filter, boolean isUsable) {
		synchronized(searchRequestQueue) {
			searchRequestQueue.add(new SearchRequest(player, search, page, minLevel, maxLevel, qualityFilter, sort, filter, isUsable));
		}
	}
	
	public static void sellItem(Player player, Item item, int buyoutPrice, int bidPrice, AuctionHouseDuration duration) {
		synchronized(sellItemQueue) {
			sellItemQueue.add(new SellItemRequest(player, item, buyoutPrice, bidPrice, duration));
		}
	}
	
	public static void close() {
		shouldClose = true;
	}
}

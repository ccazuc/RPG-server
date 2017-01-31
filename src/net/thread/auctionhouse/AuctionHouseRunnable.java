package net.thread.auctionhouse;

import java.util.ArrayList;

import net.game.auction.AuctionHouseFilter;
import net.game.auction.AuctionHouseQualityFilter;
import net.game.auction.AuctionHouseSort;
import net.game.unit.Player;

public class AuctionHouseRunnable implements Runnable {

	private final static ArrayList<SearchRequest> searchRequestQueue = new ArrayList<SearchRequest>();
	private final static ArrayList<SearchRequest> searchRequestExecute = new ArrayList<SearchRequest>();
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
			while(searchRequestExecute.size() > 0) {
				searchRequestExecute.get(0).execute();
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
	
	public static void close() {
		shouldClose = true;
	}
}

package net.thread.auctionhouse;

import java.util.ArrayList;

import net.game.auction.AuctionEntry;
import net.game.auction.AuctionHouseInitialDuration;
import net.game.auction.AuctionHouseFilter;
import net.game.auction.AuctionHouseQualityFilter;
import net.game.auction.AuctionHouseSort;
import net.game.item.Item;
import net.game.unit.Player;

public class AuctionHouseRunnable implements Runnable {

	private final static ArrayList<AuctionHouseTask> taskWaitingQueue = new ArrayList<AuctionHouseTask>();
	private final static ArrayList<AuctionHouseTask> taskQueue = new ArrayList<AuctionHouseTask>();
	private final static int LOOP_TIMER = 10;
	private static boolean shouldClose;
	
	@Override
	public void run() {
		boolean running = true;
		long delta;
		long time;
		while(running) {
			time = System.currentTimeMillis();
			synchronized(taskWaitingQueue) {
				while(taskWaitingQueue.size() > 0) {
					taskQueue.add(taskWaitingQueue.get(0));
					taskWaitingQueue.remove(0);
				}
			}
			while(taskQueue.size() > 0) {
				long timer = System.nanoTime();
				taskQueue.get(0).execute();
				delta = System.nanoTime()-timer;
				System.out.println("Auction task took "+(delta/1000)+" µs to execute.");
				taskQueue.remove(0);
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
			if(shouldClose && taskWaitingQueue.size() == 0 && taskQueue.size() == 0) {
				running = false;
			}
		}
	}
	
	public static void addSearchRequest(Player player, String search, short page, short minLevel, short maxLevel, AuctionHouseQualityFilter qualityFilter, AuctionHouseSort sort, AuctionHouseFilter filter, boolean exactWord, boolean isUsable) {
		synchronized(taskWaitingQueue) {
			taskWaitingQueue.add(new SearchRequest(player, search, page, minLevel, maxLevel, qualityFilter, sort, filter, exactWord, isUsable));
		}
	}
	
	public static void sellItem(Player player, Item item, int buyoutPrice, int bidPrice, AuctionHouseInitialDuration duration) {
		synchronized(taskWaitingQueue) {
			taskWaitingQueue.add(new SellItemRequest(player, item, buyoutPrice, bidPrice, duration));
		}
	}
	
	public static void buyoutItem(Player player, AuctionEntry entry) {
		synchronized(taskWaitingQueue) {
			taskWaitingQueue.add(new BuyoutRequest(player, entry));
		}
	}
	
	public static void cancelAuction(Player player, AuctionEntry entry) {
		synchronized(taskWaitingQueue) {
			taskWaitingQueue.add(new CancelAuctionRequest(player, entry));
		}
	}
	
	public void close() {
		shouldClose = true;
	}
}

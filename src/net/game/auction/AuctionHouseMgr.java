package net.game.auction;

import java.util.HashMap;
import java.util.LinkedList;

import net.config.ConfigMgr;
import net.game.item.Item;
import net.game.unit.Faction;
import net.game.unit.Player;
import net.thread.auctionhouse.SearchRequest;

public class AuctionHouseMgr {

	private final static HashMap<Byte, AuctionHouse> auctionHouseMap = new HashMap<Byte, AuctionHouse>();
	private static int amountEntry;
	public final static int NUMBER_RESULT_PER_PAGE = 50;
	
	public static void initAuctionHouseMgr() {
		if(ConfigMgr.ALLOW_INTERFACTION_AUCTION_HOUSE) {
			AuctionHouse tmp = new AuctionHouse();
			auctionHouseMap.put(Faction.ALLIANCE.getValue(), tmp);
			auctionHouseMap.put(Faction.HORDE.getValue(), tmp);
		}
		else {
			auctionHouseMap.put(Faction.ALLIANCE.getValue(), new AuctionHouse());
			auctionHouseMap.put(Faction.HORDE.getValue(), new AuctionHouse());
		}
	}
	
	public static LinkedList<AuctionEntry> getEntryList(Player player, SearchRequest request) {
		return auctionHouseMap.get(player.getFaction()).getEntryList(request);
	}
	
	public static void addAuction(Player player, Item item, int bidPrice, int buyoutPrice, AuctionHouseDuration duration) {
		AuctionHouse ah = auctionHouseMap.get(player.getFaction());
		if(ah == null) {
			System.out.println("**ERROR** AuctionHouse not found in AuctionHouseMgr.addAuction");
			return;
		}
		AuctionEntry entry = new AuctionEntry(generateEntryID(), player, item, buyoutPrice, bidPrice, duration);
		ah.addItem(entry);
		AuctionHouseDBMgr.addAuctionInDB(player, entry);
	}
	
	public static int calculateDepositPrice(Item item, AuctionHouseDuration duration) {
		return (int)(item.getAmount()*item.getSellPrice()*duration.getCoefficient());
	}
	
	public static int generateEntryID() {
		return ++amountEntry;
	}
}

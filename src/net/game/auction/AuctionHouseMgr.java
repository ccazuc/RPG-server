package net.game.auction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import net.Server;
import net.command.chat.CommandSendMessage;
import net.command.chat.MessageType;
import net.config.ConfigMgr;
import net.game.item.Item;
import net.game.log.Log;
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
		AuctionHouse ah = auctionHouseMap.get(player.getFaction().getValue());
		if(ah == null) {
			String error = "**ERROR** AuctionHouse not found in AuctionHouseMgr.getEntryList, faction : "+player.getFaction();
			System.out.println(error);
			Log.writePlayerLog(player, error);
			return null;
		}
		return ah.getEntryList(request);
	}
	
	public static void addAuction(Player player, Item item, int bidPrice, int buyoutPrice, AuctionHouseInitialDuration duration) {
		AuctionHouse ah = auctionHouseMap.get(player.getFaction().getValue());
		if(ah == null) {
			String error = "**ERROR** AuctionHouse not found in AuctionHouseMgr.addAuction, faction : "+player.getFaction();
			System.out.println(error);
			Log.writePlayerLog(player, error);
			return;
		}
		AuctionEntry entry = new AuctionEntry(generateEntryID(), player, item, buyoutPrice, bidPrice, duration);
		ah.addItem(entry);
		AuctionHouseDBMgr.addAuctionInDB(player, entry);
	}
	
	public static void addAuction(Faction faction, AuctionEntry entry) {
		AuctionHouse ah = auctionHouseMap.get(faction.getValue());
		if(ah == null) {
			String error = "**ERROR** AuctionHouse not found in AuctionHouseMgr.addAuction, faction : "+faction;
			System.out.println(error);
			return;
		}
		ah.addItem(entry);
	}
	
	public static void cancelAuction(Player player, AuctionEntry entry) {
		AuctionHouse ah = auctionHouseMap.get(player.getFaction().getValue());
		if(ah == null) {
			String error = "**ERROR** AuctionHouse not found in AuctionHouseMgr.cancelAuction, faction : "+player.getFaction();
			System.out.println(error);
			Log.writePlayerLog(player, error);
			return;
		}
		ah.removeItem(entry);
		AuctionHouseDBMgr.removeAuction(entry);
		Player buyer = null;
		if(entry.getBidPrice() != entry.getInitialBidPrice()) {
			buyer = Server.getInGameCharacter(entry.getLastBidderID());
		}
		if(buyer == null) {
			//TODO: add a mail with the money in the db
		}
		else {
			//TODO: send a mail with the money and add it in db
		}
	}
	
	public static ArrayList<AuctionEntry> getItemSoldByPlayerList(Player player) {
		AuctionHouse ah = auctionHouseMap.get(player.getFaction().getValue());
		if(ah == null) {
			String error = "**ERROR** AuctionHouse not found in AuctionHouseMgr.getItemSoldByPlayerList, faction : "+player.getFaction();
			System.out.println(error);
			Log.writePlayerLog(player, error);
			return null;
		}
		return ah.getItemSoldByPlayerList(player);
	}
	
	public static AuctionEntry getEntry(Player player, int entryID) {
		AuctionHouse ah = auctionHouseMap.get(player.getFaction().getValue());
		if(ah == null) {
			String error = "**ERROR** AuctionHouse not found in AuctionHouseMgr.getEntry, faction : "+player.getFaction();
			System.out.println(error);
			Log.writePlayerLog(player, error);
			return null;
		}
		return ah.getEntry(entryID);
	}
	
	public static void buyoutAuction(Player player, AuctionEntry entry) {
		AuctionHouse ah = auctionHouseMap.get(player.getFaction().getValue());
		if(ah == null) {
			String error = "**ERROR** AuctionHouse not found in AuctionHouseMgr.buyoutAuction, faction : "+player.getFaction();
			System.out.println(error);
			Log.writePlayerLog(player, error);
			return;
		}
		ah.removeItem(entry);
		AuctionHouseDBMgr.removeAuction(entry);
		Player seller = Server.getInGameCharacter(entry.getSellerID());
		if(seller != null) {
			CommandSendMessage.selfWithoutAuthor(seller.getConnection(), "A buyer has been found for your auction of ".concat(entry.getItem().getStuffName()), MessageType.SELF);
		}
		//TODO: send mail to the seller and buyer
	}
	
	public static int calculateDepositPrice(Item item, AuctionHouseInitialDuration duration) {
		return (int)(item.getAmount()*item.getSellPrice()*duration.getCoefficient());
	}
	
	public static void initEntryIDGeneration(int value) {
		amountEntry = value;
	}
	
	public static int generateEntryID() {
		return ++amountEntry;
	}
}

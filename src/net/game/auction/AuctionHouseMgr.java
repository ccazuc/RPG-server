package net.game.auction;

import java.util.HashMap;

import net.config.ConfigMgr;
import net.game.unit.Faction;

public class AuctionHouseMgr {

	private final static HashMap<Byte, AuctionHouse> auctionHouseMgrMap = new HashMap<Byte, AuctionHouse>();
	private static int amountEntry;
	
	public static void initAuctionHouseMgr() {
		if(ConfigMgr.ALLOW_INTERFACTION_AUCTION_HOUSE) {
			AuctionHouse tmp = new AuctionHouse();
			  auctionHouseMgrMap.put(Faction.ALLIANCE.getValue(), tmp);
			  auctionHouseMgrMap.put(Faction.HORDE.getValue(), tmp);
		}
		else {
			  auctionHouseMgrMap.put(Faction.ALLIANCE.getValue(), new AuctionHouse());
			  auctionHouseMgrMap.put(Faction.HORDE.getValue(), new AuctionHouse());
		}
	}
	
	public static AuctionHouse getAuctionHouse(Faction faction) {
		return auctionHouseMgrMap.get(faction.getValue());
	}
	
	public static int generateEntryID() {
		return ++amountEntry;
	}
}

package net.game.auction;

import java.util.HashMap;

import net.config.ConfigMgr;
import net.game.item.Item;
import net.game.log.Log;
import net.game.unit.Faction;
import net.game.unit.Player;

public class AuctionHouseMgr {

	private final static HashMap<Byte, AuctionHouse> auctionHouseMap = new HashMap<Byte, AuctionHouse>();
	private static int amountEntry;
	
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
	
	public static void addAuction(Player player, int bagSlot, int bidPrice, int buyoutPrice, AuctionHouseDuration duration) {
		AuctionHouse ah = auctionHouseMap.get(player.getFaction());
		Item item = player.getBag().getBag(bagSlot);
		if(item == null) {
			Log.writePlayerLog(player, "Tried to add an item into the AH whereas the slot is empty.");
			return;
		}
		AuctionEntry entry = new AuctionEntry(player, item, buyoutPrice, bidPrice, duration);
		ah.addItem(entry);
		AuctionHouseDBMgr.addAuctionInDB(player, entry);
	}
	
	public static int generateEntryID() {
		return ++amountEntry;
	}
}

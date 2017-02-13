package net.game.auction;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Server;
import net.game.item.Item;
import net.game.manager.CharacterMgr;
import net.game.unit.Faction;
import net.game.unit.Player;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;
import net.thread.sql.SQLRequestPriority;

public class AuctionHouseDBMgr {

	private static JDOStatement loadAllAuction;
	private static JDOStatement removeAuction;
	private static JDOStatement removeAuctionOnLoadAll;
	private final static SQLRequest addAuctionInDB = new SQLRequest("INSERT INTO auction_entry (entry_id, faction, item_id, seller_id, buyout_price, bid_price, initial_bid_price, last_bidder_id, time_left, deposit_timer) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", "Add auction in DB", SQLRequestPriority.HIGH) {
		
		@Override
		public void gatherData() {
			try {
				AuctionEntry entry = this.datasList.get(0).getEntry();
				this.statement.clear();
				this.statement.putInt(entry.getEntryID());
				this.statement.putByte(this.datasList.get(0).getFaction().getValue());
				this.statement.putInt(entry.getItemID());
				this.statement.putInt(entry.getSellerID());
				this.statement.putInt(entry.getBuyoutPrice());
				this.statement.putInt(entry.getBidPrice());
				this.statement.putInt(entry.getInitialBidPrice());
				this.statement.putInt(entry.getLastBidderID());
				this.statement.putInt(entry.getTimeLeft());
				this.statement.putLong(entry.getDepositTimer());
				this.statement.execute();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
	
	public static void addAuctionInDB(Player player, AuctionEntry entry) {
		addAuctionInDB.addDatas(new SQLDatas(entry, player.getFaction()));
		Server.executeSQLRequest(addAuctionInDB);
	}
	
	public static void removeAuction(AuctionEntry entry) {
		try {
			if(removeAuction == null) {
				removeAuction = Server.getAsyncHighPriorityJDO().prepare("DELETE FROM auction_entry WHERE entry_id = ?");
			}
			removeAuction.clear();
			removeAuction.putInt(entry.getEntryID());
			removeAuction.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void removeAuctionOnLoadAll(int entry_id) {
		try {
			if(removeAuctionOnLoadAll == null) {
				removeAuctionOnLoadAll = Server.getJDO().prepare("DELETE FROM auction_entry WHERE entry_id = ?");
			}
			removeAuctionOnLoadAll.clear();
			removeAuctionOnLoadAll.putInt(entry_id);
			removeAuctionOnLoadAll.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadAllAuction() {
		long timer = Server.getLoopTickTimer();
		try {
			if(loadAllAuction == null) {
				loadAllAuction = Server.getJDO().prepare("SELECT entry_id, faction, item_id, seller_id, buyout_price, bid_price, initial_bid_price, last_bidder_id, time_left, deposit_timer FROM auction_entry");
			}
			loadAllAuction.clear();
			loadAllAuction.execute();
			while(loadAllAuction.fetch()) {
				int entry_id = loadAllAuction.getInt();
				Faction faction = Faction.values()[loadAllAuction.getByte()];
				int item_id = loadAllAuction.getInt();
				int seller_id = loadAllAuction.getInt();
				int buyout_price = loadAllAuction.getInt();
				int bid_price = loadAllAuction.getInt();
				int initial_bid_price = loadAllAuction.getInt();
				int last_bidder_id = loadAllAuction.getInt();
				int time_left = loadAllAuction.getInt();
				long deposit_timer = loadAllAuction.getLong();
				String sellerName = CharacterMgr.loadCharacterNameFromID(seller_id);
				if(sellerName.length() == 0) {
					removeAuctionOnLoadAll(entry_id);
					continue;
				}
				Item item = Item.getItem(item_id);
				if(item == null) {
					removeAuctionOnLoadAll(entry_id);
					continue;
				}
				AuctionHouseMgr.addAuction(faction, new AuctionEntry(entry_id, seller_id, sellerName, item, buyout_price, initial_bid_price, bid_price, last_bidder_id, time_left, deposit_timer));
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Load all auctions took : "+(System.currentTimeMillis()-timer)+" ms.");
	}
}

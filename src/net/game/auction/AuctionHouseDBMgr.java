package net.game.auction;

import java.sql.SQLException;

import net.Server;
import net.game.unit.Player;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;
import net.thread.sql.SQLRequestPriority;

public class AuctionHouseDBMgr {

	private final static SQLRequest addAuctionInDB = new SQLRequest("INSERT INTO auction_entry (entry_id, faction, item_id, seller_id, buyout_price, bid_price, last_bidder_id, time_left) VALUES(?, ?, ?, ?, ?, ?, ?, ?)", "Add auction in DB", SQLRequestPriority.HIGH) {
		
		@Override
		public void gatherData() {
			try {
				SQLDatas datas = this.datasList.get(0);
				this.statement.clear();
				this.statement.putInt(datas.getIValue1());
				this.statement.putByte(datas.getFaction().getValue());
				this.statement.putInt(datas.getIValue2());
				this.statement.putInt(datas.getIValue3());
				this.statement.putInt(datas.getIValue4());
				this.statement.putInt(datas.getIValue5());
				this.statement.putInt(datas.getIValue6());
				this.statement.putInt(datas.getIValue7());
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
	public static void addAuctionInDB(Player player, AuctionEntry entry) {
		addAuctionInDB.addDatas(new SQLDatas(entry.getID(), player.getFaction(), entry.getItemID(), entry.getSellerID(), entry.getBuyoutPrice(), entry.getBidPrice(), entry.getLastBidderID(), (int)(entry.getAuctionEndTimer()-Server.getLoopTickTimer())));
		Server.executeSQLRequest(addAuctionInDB);
	}
}

package net.command.item;

import java.util.LinkedList;
import java.util.ListIterator;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.auction.AuctionEntry;
import net.game.auction.AuctionHouseFilter;
import net.game.auction.AuctionHouseMgr;
import net.game.auction.AuctionHouseQualityFilter;
import net.game.auction.AuctionHouseSort;
import net.game.unit.Player;
import net.thread.auctionhouse.AuctionHouseRunnable;

public class CommandAuction extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		short packetId = connection.readShort();
		if(packetId == PacketID.AUCTION_SEARCH_QUERY) {
			AuctionHouseFilter filter = AuctionHouseFilter.getFilter(connection.readByte());
			if(filter == AuctionHouseFilter.ERROR) {
				player.close();
				return;
			}
			AuctionHouseQualityFilter qualityFilter = AuctionHouseQualityFilter.getQualityFilter(connection.readByte());
			if(qualityFilter == AuctionHouseQualityFilter.ERROR) {
				player.close();
				return;
			}
			AuctionHouseSort sort = AuctionHouseSort.getSort(connection.readByte());
			if(sort == AuctionHouseSort.ERROR) {
				player.close();
				return;
			}
			short page = connection.readShort();
			boolean isUsable = connection.readBoolean();
			byte minLevel = connection.readByte();
			byte maxLevel = connection.readByte();
			String search = connection.readString();
			AuctionHouseRunnable.addSearchRequest(player, search, page, minLevel, maxLevel, qualityFilter, sort, filter, isUsable);
		}
	}
	
	public static void sendQuery(Player player, LinkedList<AuctionEntry> list, int startIndex) {
		ListIterator<AuctionEntry> ite = list.listIterator(startIndex);
		int i = -1;
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.AUCTION);
		connection.writeShort(PacketID.AUCTION_SEARCH_QUERY);
		connection.writeByte((byte)Math.min(list.size()-startIndex, (byte)50));
		AuctionEntry entry = null;
		while(ite.hasNext() && ++i < AuctionHouseMgr.NUMBER_RESULT_PER_PAGE) {
			entry = ite.next();
			connection.writeInt(entry.getID());
			connection.writeString(entry.getSellerName());
			connection.writeInt(entry.getItemID());
			connection.writeInt(entry.getItem().getAmount());
			connection.writeInt(entry.getBuyoutPrice());
			connection.writeInt(entry.getBidPrice());
			connection.writeByte(entry.getUpdatedDuration().getValue());
		}
		connection.endPacket();
		connection.send();
	}
}

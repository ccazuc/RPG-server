package net.command.item;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.auction.AuctionHouseFilter;
import net.game.auction.AuctionHouseQualityFilter;
import net.game.auction.AuctionHouseSort;
import net.game.unit.Player;

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
			boolean isUsable = connection.readBoolean();
			byte minLevel = connection.readByte();
			byte maxLevel = connection.readByte();
			String search = connection.readString();
		}
	}
}

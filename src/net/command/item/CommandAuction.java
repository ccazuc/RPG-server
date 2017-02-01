package net.command.item;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import net.command.Command;
import net.command.chat.CommandSendMessage;
import net.command.chat.MessageType;
import net.command.player.CommandSendRedAlert;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.DefaultRedAlert;
import net.game.auction.AuctionEntry;
import net.game.auction.AuctionHouseDuration;
import net.game.auction.AuctionHouseFilter;
import net.game.auction.AuctionHouseMgr;
import net.game.auction.AuctionHouseQualityFilter;
import net.game.auction.AuctionHouseSort;
import net.game.item.Item;
import net.game.log.Log;
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
		else if(packetId == PacketID.AUCTION_SELL_ITEM) {
			byte bagSlot = connection.readByte();
			int buyoutPrice = connection.readInt();
			int bidPrice = connection.readInt();
			AuctionHouseDuration duration = AuctionHouseDuration.getDuration(connection.readByte());
			if(duration == AuctionHouseDuration.ERROR) {
				player.close();
				return;
			}
			if(buyoutPrice < 0 || bidPrice <= 0) {
				player.close();
				return;
			}
			Item item = player.getBag().getBag(bagSlot);
			if(item == null) {
				Log.writePlayerLog(player, "Tried to sell an item whereas his bag his empty (CommandAuction.AUCTION_SELL_ITEM).");
				player.close();
				return;
			}
			int depositPrice = AuctionHouseMgr.calculateDepositPrice(item, duration);
			if(player.getGold() < depositPrice) {
				CommandSendRedAlert.write(player, DefaultRedAlert.NOT_ENOUGH_GOLD);
				return;
			}
			player.setGold(player.getGold()-depositPrice);
			player.getBag().setBag(bagSlot, null);
			AuctionHouseRunnable.sellItem(player, item, buyoutPrice, bidPrice, duration);
		}
		else if(packetId == PacketID.AUCTION_BUYOUT) {
			int entryID = connection.readInt();
			AuctionEntry entry = AuctionHouseMgr.getEntry(player, entryID);
			if(entry == null) {
				return;
			}
			if(entry.isLocked()) {
				//TODO: send item has been bought
				return;
			}
			if(!entry.canBeBuy()) {
				Log.writePlayerLog(player, "Tried to buy an auction with no buyout price.");
				return;
			}
			if(player.getGold() < entry.getBuyoutPrice()) {
				CommandSendRedAlert.write(player, DefaultRedAlert.NOT_ENOUGH_GOLD);
				return;
			}
			entry.lock();
			player.setGold(player.getGold()-entry.getBuyoutPrice());
			AuctionHouseRunnable.buyoutItem(player, entry);
		}
		else if(packetId == PacketID.AUCTION_MAKE_BID) {
			int entryID = connection.readInt();
			int bidValue = connection.readInt();
			AuctionEntry entry = AuctionHouseMgr.getEntry(player, entryID);
			if(entry == null) {
				return;
			}
			if(entry.isLocked()) {
				//TODO: send item has been bought
				return;
			}
			if(bidValue < entry.getBidPrice()) {
				//TODO: send error
				return;
			}
			if(player.getGold() < bidValue) {
				CommandSendRedAlert.write(player, DefaultRedAlert.NOT_ENOUGH_GOLD);
				return;
			}
			entry.setBid(bidValue);
			entry.setLastBidderID(player.getUnitID());
			player.setGold(player.getGold()-bidValue);
			CommandSendMessage.selfWithoutAuthor(connection, "Offer accepted.", MessageType.SELF);
		}
		else if(packetId == PacketID.AUCTION_CANCEL_SELL) {
			int entryID = connection.readInt();
			AuctionEntry entry = AuctionHouseMgr.getEntry(player, entryID);
			if(entry == null) {
				return;
			}
			if(entry.isLocked()) {
				//TODO: send item has been bought
				return;
			}
			if(entry.getSellerID() != player.getUnitID()) {
				player.close();
				Log.writePlayerLog(player, "tried to cancel someone else's auction (CommandAuction.AUCTION_CANCEL_SELL)");
				return;
			}
			int cost = 0;
			if(entry.getBidPrice() != entry.getInitialBidPrice()) {
				cost+= .05f*entry.getBidPrice();
			}
			//TODO: verify that the cost formula is correct
			player.setGold(player.getGold()-cost);
			entry.lock();
			AuctionHouseRunnable.cancelAuction(player, entry);
		}
	}
	
	public static void sendQuery(Player player, LinkedList<AuctionEntry> list, int startIndex) {
		ListIterator<AuctionEntry> ite = list.listIterator(startIndex);
		int i = -1;
		byte amountResult = (byte)Math.min(list.size()-startIndex, (byte)50);
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.AUCTION);
		connection.writeShort(PacketID.AUCTION_SEARCH_QUERY);
		connection.writeByte(amountResult);
		AuctionEntry entry = null;
		while(ite.hasNext() && ++i < amountResult) {
			entry = ite.next();
			connection.writeInt(entry.getEntryID());
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
	
	public static void initSellItem(Player player) {
		ArrayList<AuctionEntry> list = AuctionHouseMgr.getItemSoldByPlayerList(player);
		if(list == null || list.size() == 0) {
			return;
		}
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.AUCTION);
		connection.writeShort(PacketID.AUCTION_INIT_SELL_ITEM);
		connection.writeShort((short)list.size());
		int i = -1;
		AuctionEntry entry;
		while(++i < list.size()) {
			entry = list.get(i);
			connection.writeInt(entry.getEntryID());
			connection.writeInt(entry.getItemID());
			connection.writeInt(entry.getItem().getAmount());
			connection.writeByte(entry.getUpdatedDuration().getValue());
			connection.writeInt(entry.getBidPrice());
			connection.writeInt(entry.getBuyoutPrice());
		}
		connection.endPacket();
		connection.send();
	}
	
	public static void sendSellItem(Player player, AuctionEntry entry) {
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.AUCTION);
		connection.writeShort(PacketID.AUCTION_SELL_ITEM);
		connection.writeInt(entry.getEntryID());
		connection.writeInt(entry.getItemID());
		connection.writeInt(entry.getItem().getAmount());
		connection.writeByte(entry.getUpdatedDuration().getValue());
		connection.writeInt(entry.getBidPrice());
		connection.writeInt(entry.getBuyoutPrice());
		connection.endPacket();
		connection.send();
	}
	
	public static void madeBid(Player player, AuctionEntry entry) {
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.AUCTION);
		connection.writeShort(PacketID.AUCTION_MAKE_BID);
		connection.writeInt(entry.getEntryID());
		connection.writeInt(entry.getBidPrice());
		connection.endPacket();
		connection.send();
	}
}

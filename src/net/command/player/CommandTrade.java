package net.command.player;

import java.sql.SQLException;

import net.Server;
import net.command.Command;
import net.command.chat.CommandPlayerNotFound;
import net.command.chat.CommandSendMessage;
import net.command.chat.MessageType;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.Player;
import net.game.item.Item;
import net.game.manager.IgnoreMgr;

public class CommandTrade extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		short packetID = connection.readShort();
		if(packetID == PacketID.TRADE_NEW) { //declare a new trade
			String traded = connection.readString();
			if(!(traded.length() > 2)) {
				CommandPlayerNotFound.write(connection, traded);
				return;
			}
			traded = traded.substring(0, 1).toUpperCase()+traded.substring(1).toLowerCase();
			Player trade = Server.getInGameCharacterByName(traded);
			if(trade == null) {
				CommandPlayerNotFound.write(connection, traded);
				return;
			}
			if(trade == player) {
				CommandSendMessage.selfWithoutAuthor(connection, "Can't trade with yourself.", MessageType.SELF);
				return;
			}
			if(IgnoreMgr.isIgnored(trade.getCharacterId(), player.getCharacterId())) {
				CommandSendMessage.selfWithoutAuthor(connection, trade.getName()+IgnoreMgr.ignoreMessage, MessageType.SELF);
				return;
			}
			if(player.getPlayerTrade() == null && trade.getPlayerTrade() == null) { //players are not trading
			}
			else { //cancel current trade
				tradeCancel(trade.getPlayerTrade());
				trade.getPlayerTrade().setPlayerTrade(null);
			}
			trade.setPlayerTrade(player);
			player.setPlayerTrade(trade);
			CommandSendMessage.selfWithAuthor(trade.getConnection(), '['+player.getName()+"] wants to trade with you.", player.getName(), MessageType.SELF);
			write(PacketID.TRADE_REQUEST, trade, player.getName());
		}
		else if(packetID == PacketID.TRADE_NEW_CONFIRM) { //confirm the trade
			player.getPlayerTrade().getConnection().writeShort(PacketID.TRADE);
			player.getPlayerTrade().getConnection().writeShort(PacketID.TRADE_NEW_CONFIRM);
			player.getPlayerTrade().getConnection().send();
			player.getPlayerTrade().initTrade(player.getPlayerTrade(), player);
			player.setTrade(player.getPlayerTrade().getTrade());
		}
		else if(packetID == PacketID.TRADE_ADD_ITEM) { //add an item on trade's frame
			int id = connection.readInt();
			int slot = connection.readInt();
			int amount = connection.readInt();
			if(connection.readBoolean()) {
				if(player.getPlayerTrade().itemHasBeenSendToClient(id)) {
					int gem1Id = connection.readInt();
					int gem2Id = connection.readInt();
					int gem3Id = connection.readInt();
					if(player.getBag().getNumberItemInBags(id) >= amount && Item.exists(id)) {
						sendKnownItem(player.getPlayerTrade(), id, slot, amount, gem1Id, gem2Id, gem3Id);
					}
					else {
						writeAddItemError(player, slot); //c po b1 2 modifié lé paké
					}
				}
				else {
					if(player.getBag().getNumberItemInBags(id) >= amount && Item.exists(id)) {
						sendUnknownItem(player.getPlayerTrade(), id, slot, amount, connection.readInt(), connection.readInt(), connection.readInt());
					}
					else {
						writeAddItemError(player, slot); //c po b1 2 modifié lé paké
					}
				}
			}
			else {
				if(player.getPlayerTrade().itemHasBeenSendToClient(id)) {
					if(player.getBag().getNumberItemInBags(id) >= amount && Item.exists(id)) {
						sendKnownItem(player.getPlayerTrade(), id, slot, amount);
					}
					else {
						writeAddItemError(player, slot); //c po b1 2 modifié lé paké
					}
				}
				else {
					if(player.getBag().getNumberItemInBags(id) >= amount && Item.exists(id)) {
						sendUnknownItem(player.getPlayerTrade(), id, slot, amount);
					}
					else {
						writeAddItemError(player, slot); //c po b1 2 modifié lé paké
					}
				}
			}
		}
		else if(packetID == PacketID.TRADE_REMOVE_ITEM) {
			int slot = connection.readInt();
			if(player.getTrade() == null) {
				return;
			}
			if(slot >= 0 && slot <= 6) {
				player.getPlayerTrade().getConnection().writeShort(PacketID.TRADE);
				player.getPlayerTrade().getConnection().writeShort(PacketID.TRADE_REMOVE_ITEM);
				player.getPlayerTrade().getConnection().writeInt(slot);
				player.getPlayerTrade().getConnection().send();
				tradeUnaccept(player.getPlayerTrade());
			}
		}
		else if(packetID == PacketID.TRADE_ACCEPT) { //lock the trade
			if(player.getTrade() == null) {
				return;
			}
			player.getPlayerTrade().getConnection().writeShort(PacketID.TRADE);
			player.getPlayerTrade().getConnection().writeShort(PacketID.TRADE_ACCEPT);
			player.getPlayerTrade().getConnection().send();
			player.getTrade().setTradeState(player, true);
			if(player.getTrade().getTradeInitState() && player.getTrade().getTradeTargetState()) {
				try {
					player.getTrade().exchangeItem();
				} 
				catch (SQLException e) {
					e.printStackTrace();
				}
				closeTrade(player);
			}
		}
		else if(packetID == PacketID.TRADE_CLOSE) { //cancel the trade
			if(player.getTrade() == null) {
				return;
			}
			closeTrade(player);
		}
	}
	
	public static void closeTrade(Player player) {
		tradeCancel(player);
		tradeCancel(player.getPlayerTrade());
		player.setTrade(null);
		if(player.getPlayerTrade() != null) {
			player.getPlayerTrade().setTrade(null);
			player.getPlayerTrade().setPlayerTrade(null);
			player.setPlayerTrade(null);
		}
	}
	
	public static void sendTradeItems(Player tradeInit) throws SQLException {
		tradeItem(tradeInit, tradeInit.getTrade().getTradeTargetTable());
		tradeItem(tradeInit.getPlayerTrade(), tradeInit.getTrade().getTradeInitTable());
	}
	
	private static void tradeItem(Player player, Item[] table) throws SQLException {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.TRADE);
		player.getConnection().writeShort(PacketID.TRADE_SEND_ALL_ITEMS);
		int i = 0;
		while(i < 6) {
			if(table[i] != null) {
				if(player.itemHasBeenSendToClient(table[i].getId())) {
					player.getConnection().writeShort(PacketID.KNOWN_ITEM);
					player.getConnection().writeInt(table[i].getId());
					player.getConnection().writeInt(table[i].getAmount());
				}
				else {
					player.getConnection().writeShort(PacketID.UNKNOWN_ITEM);
					player.getConnection().writeItem(table[i]);
					player.getConnection().writeInt(table[i].getAmount());
				}
				player.addItem(table[i], table[i].getAmount());
			}
			else {
				player.getConnection().writeShort((byte)-1);
			}
			i++;
		}
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	private static void tradeUnaccept(Player player) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.TRADE);
		player.getConnection().writeShort(PacketID.TRADE_UNACCEPT);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	private static void write(short packetId, Player player, String name) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.TRADE);
		player.getConnection().writeShort(packetId);
		player.getConnection().writeString(name);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	private static void writeAddItemError(Player player, int slot) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.TRADE);
		player.getConnection().writeShort(PacketID.TRADE_ADD_ITEM_ERROR);
		player.getConnection().writeInt(slot);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	private static void sendUnknownItem(Player player, int id, int slot, int amount, int gem1Id, int gem2Id, int gem3Id) {
		if(slot >= 0 && slot <= 6) {
			player.getConnection().startPacket();
			player.getConnection().writeShort(PacketID.TRADE);
			player.getConnection().writeShort(PacketID.TRADE_ADD_ITEM);
			player.getConnection().writeShort(PacketID.UNKNOWN_ITEM);
			player.getConnection().writeItem(Item.getItem(id));
			player.getConnection().writeInt(slot);
			player.getConnection().writeInt(amount);
			player.getConnection().writeBoolean(true);
			player.getConnection().writeInt(gem1Id);
			player.getConnection().writeInt(gem2Id);
			player.getConnection().writeInt(gem3Id);
			player.getConnection().endPacket();
			player.getConnection().send();
			player.getTrade().addItem(player.getPlayerTrade(), slot, amount, Item.getItem(id));
			tradeUnaccept(player.getPlayerTrade());
			player.getTrade().setTradeState(player, false);
			player.getTrade().setTradeState(player.getPlayerTrade(), false);
		}
	}
	
	private static void sendUnknownItem(Player player, int id, int slot, int amount) {
		if(slot >= 0 && slot <= 6) {
			player.getConnection().startPacket();
			player.getConnection().writeShort(PacketID.TRADE);
			player.getConnection().writeShort(PacketID.TRADE_ADD_ITEM);
			player.getConnection().writeShort(PacketID.UNKNOWN_ITEM);
			player.getConnection().writeItem(Item.getItem(id));
			player.getConnection().writeInt(slot);
			player.getConnection().writeInt(amount);
			player.getConnection().writeBoolean(false);
			player.getConnection().endPacket();
			player.getConnection().send();
			player.getTrade().addItem(player.getPlayerTrade(), slot, amount, Item.getItem(id));
			tradeUnaccept(player.getPlayerTrade());
			player.getTrade().setTradeState(player, false);
			player.getTrade().setTradeState(player.getPlayerTrade(), false);
		}
	}
	
	private static void sendKnownItem(Player player, int id, int slot, int amount) {
		if(slot >= 0 && slot <= 6) {
			player.getConnection().startPacket();
			player.getConnection().writeShort(PacketID.TRADE);
			player.getConnection().writeShort(PacketID.TRADE_ADD_ITEM);
			player.getConnection().writeShort(PacketID.KNOWN_ITEM);
			player.getConnection().writeInt(id);
			player.getConnection().writeInt(slot);
			player.getConnection().writeInt(amount);
			player.getConnection().writeBoolean(false);
			player.getConnection().endPacket();
			player.getConnection().send();
			player.getTrade().addItem(player.getPlayerTrade(), slot, amount, Item.getItem(id));
			tradeUnaccept(player.getPlayerTrade());
			player.getTrade().setTradeState(player, false);
			player.getTrade().setTradeState(player.getPlayerTrade(), false);
		}
	}
	
	private static void sendKnownItem(Player player, int id, int slot, int amount, int gem1Id, int gem2Id, int gem3Id) {
		if(slot >= 0 && slot <= 6) {
			player.getConnection().startPacket();
			player.getConnection().writeShort(PacketID.TRADE);
			player.getConnection().writeShort(PacketID.TRADE_ADD_ITEM);
			player.getConnection().writeShort(PacketID.KNOWN_ITEM);
			player.getConnection().writeInt(id);
			player.getConnection().writeInt(slot);
			player.getConnection().writeInt(amount);
			player.getConnection().writeBoolean(true);
			player.getConnection().writeInt(gem1Id);
			player.getConnection().writeInt(gem2Id);
			player.getConnection().writeInt(gem3Id);
			player.getConnection().endPacket();
			player.getConnection().send();
			player.getTrade().addItem(player.getPlayerTrade(), slot, amount, Item.getItem(id));
			tradeUnaccept(player.getPlayerTrade());
			player.getTrade().setTradeState(player, false);
			player.getTrade().setTradeState(player.getPlayerTrade(), false);
		}
	}
	
	private static void tradeCancel(Player player) {
		if(player != null) {
			player.getConnection().startPacket();
			player.getConnection().writeShort(PacketID.TRADE);
			player.getConnection().writeShort(PacketID.TRADE_CLOSE);
			player.getConnection().endPacket();
			player.getConnection().send();
		}
	}
}

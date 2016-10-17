package net.command;

import net.Server;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.Player;
import net.game.item.Item;

public class CommandTrade extends Command {
	
	public CommandTrade(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	@Override
	public void read() {
		byte packetID = this.connection.readByte();
		if(packetID == PacketID.TRADE_NEW) { //declare a new trade
			int traded = this.connection.readInt();
			Player trade = Server.getCharacter(traded);
			//System.out.println(trade.getName()+" "+this.player.getPlayerTrade()+" "+trade.getPlayerTrade());
			if(trade != null) {
				if(this.player.getPlayerTrade() == null && trade.getPlayerTrade() == null) { //players are not trading
				}
				else { //cancel current trade
					tradeCancel(trade.getPlayerTrade());
					trade.getPlayerTrade().setPlayerTrade(null);
				}
				trade.setPlayerTrade(this.player);
				this.player.setPlayerTrade(trade);
				write(PacketID.TRADE_REQUEST, trade, this.player.getName());
			}
			else {
				//send yellow message player not found
				//CommandAddLocalMessage("aucun joueur bla bla");
			}
		}
		else if(packetID == PacketID.TRADE_NEW_CONFIRM) { //confirm the trade
			this.player.getPlayerTrade().getConnection().writeByte(PacketID.TRADE);
			this.player.getPlayerTrade().getConnection().writeByte(PacketID.TRADE_NEW_CONFIRM);
			this.player.getPlayerTrade().getConnection().send();
			this.player.getPlayerTrade().initTrade(this.player.getPlayerTrade(), this.player);
			this.player.setTrade(this.player.getPlayerTrade().getTrade());
		}
		else if(packetID == PacketID.TRADE_ADD_ITEM) { //add an item on trade's frame
			//System.out.println("Raw item received");
			int id = this.connection.readInt();
			int slot = this.connection.readInt();
			int amount = this.connection.readInt();
			if(this.connection.readBoolean()) {
				if(this.player.getPlayerTrade().itemHasBeenSendToClient(id)) {
					int gem1Id = this.connection.readInt();
					int gem2Id = this.connection.readInt();
					int gem3Id = this.connection.readInt();
					if(this.player.getBag().getNumberItemInBags(id) >= amount && Item.exists(id)) {
						sendKnownItem(this.player.getPlayerTrade(), id, slot, amount, gem1Id, gem2Id, gem3Id);
					}
					else {
						writeAddItemError(this.player, slot); //c po b1 2 modifié lé paké
					}
					//System.out.println("Item received: has gem, known");
				}
				else {
					if(this.player.getBag().getNumberItemInBags(id) >= amount && Item.exists(id)) {
						sendUnknownItem(this.player.getPlayerTrade(), id, slot, amount, this.connection.readInt(), this.connection.readInt(), this.connection.readInt());
					}
					else {
						writeAddItemError(this.player, slot); //c po b1 2 modifié lé paké
					}
					//System.out.println("Item received: has gem, unknown");
				}
			}
			else {
				if(this.player.getPlayerTrade().itemHasBeenSendToClient(id)) {
					if(this.player.getBag().getNumberItemInBags(id) >= amount && Item.exists(id)) {
						sendKnownItem(this.player.getPlayerTrade(), id, slot, amount);
					}
					else {
						writeAddItemError(this.player, slot); //c po b1 2 modifié lé paké
					}
					//System.out.println("Item received: no gem, known");
				}
				else {
					if(this.player.getBag().getNumberItemInBags(id) >= amount && Item.exists(id)) {
						sendUnknownItem(this.player.getPlayerTrade(), id, slot, amount);
					}
					else {
						writeAddItemError(this.player, slot); //c po b1 2 modifié lé paké
					}
					//System.out.println("Item received: no gem, unknown");
				}
			}
		}
		else if(packetID == PacketID.TRADE_REMOVE_ITEM) {
			int slot = this.connection.readInt();
			if(slot >= 0 && slot <= 6) {
				this.player.getPlayerTrade().getConnection().writeByte(PacketID.TRADE);
				this.player.getPlayerTrade().getConnection().writeByte(PacketID.TRADE_REMOVE_ITEM);
				this.player.getPlayerTrade().getConnection().writeInt(slot);
				this.player.getPlayerTrade().getConnection().send();
				tradeUnaccept(this.player.getPlayerTrade());
			}
		}
		else if(packetID == PacketID.TRADE_ACCEPT) { //lock the trade
			this.player.getPlayerTrade().getConnection().writeByte(PacketID.TRADE);
			this.player.getPlayerTrade().getConnection().writeByte(PacketID.TRADE_ACCEPT);
			this.player.getPlayerTrade().getConnection().send();
			this.player.getTrade().setTradeState(this.player, true);
			if(this.player.getTrade().getTradeInitState() && this.player.getTrade().getTradeTargetState()) {
				this.player.getTrade().exchangeItem();
				closeTrade(this.player);
			}
		}
		else if(packetID == PacketID.TRADE_CLOSE) { //cancel the trade
			closeTrade(this.player);
		}
	}
	
	private static void closeTrade(Player player) {
		tradeCancel(player);
		tradeCancel(player.getPlayerTrade());
		player.setTrade(null);
		player.getPlayerTrade().setTrade(null);
		player.getPlayerTrade().setPlayerTrade(null);
		player.setPlayerTrade(null);
	}
	
	public static void sendTradeItems(Player tradeInit) {
		tradeItem(tradeInit, tradeInit.getTrade().getTradeTargetTable());
		tradeItem(tradeInit.getPlayerTrade(), tradeInit.getTrade().getTradeInitTable());
	}
	
	private static void tradeItem(Player player, Item[] table) {
		player.getConnection().writeByte(PacketID.TRADE);
		player.getConnection().writeByte(PacketID.TRADE_SEND_ALL_ITEMS);
		int i = 0;
		while(i < 6) {
			if(table[i] != null) {
				if(player.itemHasBeenSendToClient(table[i].getId())) {
					player.getConnection().writeByte(PacketID.KNOWN_ITEM);
					player.getConnection().writeInt(table[i].getId());
					player.getConnection().writeInt(table[i].getAmount());
				}
				else {
					player.getConnection().writeByte(PacketID.UNKNOWN_ITEM);
					player.getConnection().writeItem(table[i]);
					player.getConnection().writeInt(table[i].getAmount());
				}
			}
			else {
				player.getConnection().writeByte((byte)-1);
			}
			i++;
		}
		player.getConnection().send();
	}
	
	private static void tradeUnaccept(Player player) {
		player.getConnection().writeByte(PacketID.TRADE);
		player.getConnection().writeByte(PacketID.TRADE_UNACCEPT);
		player.getConnection().send();
	}
	
	private static void write(byte packetId, Player player, String name) {
		player.getConnection().writeByte(PacketID.TRADE);
		player.getConnection().writeByte(packetId);
		player.getConnection().writeString(name);
		player.getConnection().send();
	}
	
	private static void writeAddItemError(Player player, int slot) {
		player.getConnection().writeByte(PacketID.TRADE);
		player.getConnection().writeByte(PacketID.TRADE_ADD_ITEM_ERROR);
		player.getConnection().writeInt(slot);
		player.getConnection().send();
		System.out.println("trade error: "+slot);
	}
	
	private static void sendUnknownItem(Player player, int id, int slot, int amount, int gem1Id, int gem2Id, int gem3Id) {
		if(slot >= 0 && slot <= 6) {
			player.getConnection().writeByte(PacketID.TRADE);
			player.getConnection().writeByte(PacketID.TRADE_ADD_ITEM);
			player.getConnection().writeByte(PacketID.UNKNOWN_ITEM);
			player.getConnection().writeItem(Item.getItem(id));
			player.getConnection().writeInt(slot);
			player.getConnection().writeInt(amount);
			player.getConnection().writeBoolean(true);
			player.getConnection().writeInt(gem1Id);
			player.getConnection().writeInt(gem2Id);
			player.getConnection().writeInt(gem3Id);
			player.getConnection().send();
			player.getTrade().addItem(player, slot, amount, Item.getItem(id));
			tradeUnaccept(player.getPlayerTrade());
			player.getTrade().setTradeState(player, false);
			player.getTrade().setTradeState(player.getPlayerTrade(), false);
		}
	}
	
	private static void sendUnknownItem(Player player, int id, int slot, int amount) {
		if(slot >= 0 && slot <= 6) {
			player.getConnection().writeByte(PacketID.TRADE);
			player.getConnection().writeByte(PacketID.TRADE_ADD_ITEM);
			player.getConnection().writeByte(PacketID.UNKNOWN_ITEM);
			player.getConnection().writeItem(Item.getItem(id));
			player.getConnection().writeInt(slot);
			player.getConnection().writeInt(amount);
			player.getConnection().writeBoolean(false);
			player.getConnection().send();
			player.getTrade().addItem(player, slot, amount, Item.getItem(id));
			tradeUnaccept(player.getPlayerTrade());
			player.getTrade().setTradeState(player, false);
			player.getTrade().setTradeState(player.getPlayerTrade(), false);
		}
	}
	
	private static void sendKnownItem(Player player, int id, int slot, int amount) {
		if(slot >= 0 && slot <= 6) {
			player.getConnection().writeByte(PacketID.TRADE);
			player.getConnection().writeByte(PacketID.TRADE_ADD_ITEM);
			player.getConnection().writeByte(PacketID.KNOWN_ITEM);
			player.getConnection().writeInt(id);
			player.getConnection().writeInt(slot);
			player.getConnection().writeInt(amount);
			player.getConnection().writeBoolean(false);
			player.getConnection().send();
			player.getTrade().addItem(player, slot, amount, Item.getItem(id));
			tradeUnaccept(player.getPlayerTrade());
			player.getTrade().setTradeState(player, false);
			player.getTrade().setTradeState(player.getPlayerTrade(), false);
		}
	}
	
	private static void sendKnownItem(Player player, int id, int slot, int amount, int gem1Id, int gem2Id, int gem3Id) {
		if(slot >= 0 && slot <= 6) {
			player.getConnection().writeByte(PacketID.TRADE);
			player.getConnection().writeByte(PacketID.TRADE_ADD_ITEM);
			player.getConnection().writeByte(PacketID.KNOWN_ITEM);
			player.getConnection().writeInt(id);
			player.getConnection().writeInt(slot);
			player.getConnection().writeInt(amount);
			player.getConnection().writeBoolean(true);
			player.getConnection().writeInt(gem1Id);
			player.getConnection().writeInt(gem2Id);
			player.getConnection().writeInt(gem3Id);
			player.getConnection().send();
			player.getTrade().addItem(player, slot, amount, Item.getItem(id));
			tradeUnaccept(player.getPlayerTrade());
			player.getTrade().setTradeState(player, false);
			player.getTrade().setTradeState(player.getPlayerTrade(), false);
		}
	}
	
	private static void tradeCancel(Player player) {
		player.getConnection().writeByte(PacketID.TRADE);
		player.getConnection().writeByte(PacketID.TRADE_CLOSE);
		player.getConnection().send();
	}
}

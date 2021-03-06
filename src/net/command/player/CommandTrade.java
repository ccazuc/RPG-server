package net.command.player;

import net.Server;
import net.command.Command;
import net.command.chat.CommandPlayerNotFound;
import net.command.chat.CommandSendMessage;
import net.command.chat.MessageType;
import net.command.item.CommandSetItem;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.item.DragItem;
import net.game.item.Item;
import net.game.item.gem.GemColor;
import net.game.item.stuff.Stuff;
import net.game.log.Log;
import net.game.manager.IgnoreMgr;
import net.game.unit.Player;
import net.utils.StringUtils;

public class CommandTrade extends Command
{

	public CommandTrade(String name, boolean debug)
	{
		super(name, debug);
	}
	
	@Override
	public void read(Player player)
	{
		Connection connection = player.getConnection();
		short packetID = connection.readShort();
		if (packetID == PacketID.TRADE_NEW)
		{ //declare a new trade
			String traded = connection.readString();
			if (!StringUtils.checkPlayerNameLength(traded))
			{
				CommandPlayerNotFound.write(connection, traded);
				return;
			}
			traded = StringUtils.formatPlayerName(traded);
			Player trade = Server.getInGameCharacterByName(traded);
			if (trade == null)
			{
				CommandPlayerNotFound.write(connection, traded);
				return;
			}
			if (trade == player)
			{
				CommandSendMessage.selfWithoutAuthor(connection, "Can't trade with yourself.", MessageType.SELF);
				return;
			}
			if (IgnoreMgr.isIgnored(trade.getUnitID(), player.getUnitID()))
			{
				CommandSendMessage.selfWithoutAuthor(connection, trade.getName()+IgnoreMgr.ignoreMessage, MessageType.SELF);
				return;
			}
			if (player.getPlayerTrade() != null || trade.getPlayerTrade() != null)
			{
				CommandSendMessage.selfWithoutAuthor(connection, traded + " is busy trading with someone.", MessageType.SELF);
				return;
			}
			trade.setPlayerTrade(player);
			player.setPlayerTrade(trade);
			CommandSendMessage.selfWithAuthor(trade.getConnection(), " wants to trade with you.", player.getName(), MessageType.SELF);
			write(PacketID.TRADE_REQUEST, trade, player.getName());
			writeTradeNewConfirmed(player);
		}
		else if (packetID == PacketID.TRADE_NEW_CONFIRM)
		{ //confirm the trade
			confirmTrade(player.getPlayerTrade());
			confirmTrade(player);
			player.getPlayerTrade().initTrade(player.getPlayerTrade(), player);
			player.setTrade(player.getPlayerTrade().getTrade());
		}
		else if (packetID == PacketID.TRADE_ADD_ITEM)
		{ //add an item on trade's frame
			if (player.getTrade() == null)
			{
				Log.writePlayerLog(player, "Tried to add an item to the trade whereas he's not trading.");
				player.close();
				return;
			}
			DragItem slotType = DragItem.values()[connection.readByte()];
			int itemSlot = connection.readInt();
			int tradeSlot = connection.readInt();
			if (player.getTrade().isLocked())
				return;
			if (tradeSlot < 0 || tradeSlot >= player.getTrade().getTradeInitTable().length)
			{
				Log.writePlayerLog(player, "Invalid tradeSlot value in CommandTrade : " + tradeSlot);
				return;
			}
			Item item = null;
			if (slotType == DragItem.BAG)
				item = player.getBag().getBag(itemSlot);
			else if (slotType == DragItem.INVENTORY)
				item = player.getStuff(itemSlot);
			else
				return;
			if (item == null)
				return;
			int slot = 0;
			if (player.getTrade().getTradeTable(player)[tradeSlot] != null)
			{
				slot = player.getItemBagSlot(player.getTrade().getTradeTable(player)[tradeSlot]);
				if (slot == -1)
				{
					slot = player.getItemInventorySlot(player.getTrade().getTradeTable(player)[tradeSlot]);
					if (slot == -1)
						Log.writePlayerLog(player, "Item not found in CommandTrade:TRADE_ADD_ITEM.");
					else
						CommandSetItem.setSelectable(player, DragItem.INVENTORY, slot);
				}
				else
				{
					CommandSetItem.setSelectable(player, DragItem.BAG, slot);
				}
			}
			player.getTrade().getTradeTable(player)[tradeSlot] = item;
			addSelfItem(player, slotType, itemSlot, tradeSlot);
			addOtherItem(player.getPlayerTrade(), tradeSlot+7, item);
			/*int id = connection.readInt();
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
						writeAddItemError(player, slot); //c po b1 2 modifi� l� pak�
					}
				}
				else {
					if(player.getBag().getNumberItemInBags(id) >= amount && Item.exists(id)) {
						sendUnknownItem(player.getPlayerTrade(), id, slot, amount, connection.readInt(), connection.readInt(), connection.readInt());
					}
					else {
						writeAddItemError(player, slot); //c po b1 2 modifi� l� pak�
					}
				}
			}
			else {
				if(player.getPlayerTrade().itemHasBeenSendToClient(id)) {
					if(player.getBag().getNumberItemInBags(id) >= amount && Item.exists(id)) {
						sendKnownItem(player.getPlayerTrade(), id, slot, amount);
					}
					else {
						writeAddItemError(player, slot); //c po b1 2 modifi� l� pak�
					}
				}
				else {
					if(player.getBag().getNumberItemInBags(id) >= amount && Item.exists(id)) {
						sendUnknownItem(player.getPlayerTrade(), id, slot, amount);
					}
					else {
						writeAddItemError(player, slot); //c po b1 2 modifi� l� pak�
					}
				}
			}*/
		}
		else if(packetID == PacketID.TRADE_REMOVE_ITEM)
		{
			int slot = connection.readInt();
			if (player.getTrade() == null)
			{
				Log.writePlayerLog(player, "Tried to remove item from a trade whereas he's not trading.");
				player.close();
				return;
			}
			if (player.getTrade().isLocked())
				return;
			if (slot < 0 || slot > 6)
			{
				Log.writePlayerLog(player, "Tried to remove remove an item from trade slot : "+slot+'.');
				player.close();
				return;
			}
			removeItem(player, slot);
			removeItem(player.getPlayerTrade(), slot+7);
			player.getTrade().setTradeState(player, false);
		}
		else if (packetID == PacketID.TRADE_ACCEPT) { //lock the trade
			if (player.getTrade() == null)
			{
				Log.writePlayerLog(player, "Tried to accept the trade whereas he's not trading.");
				player.close();
				return;
			}
			tradeAccept(player.getPlayerTrade());
			player.getTrade().setTradeState(player, true);
			if (player.getTrade().getTradeInitState() && player.getTrade().getTradeTargetState())
			{
				player.getTrade().lock();
				if (player.getTrade().exchangeItem() == -1)
					return;
				closeTrade(player);
			}
		}
		else if (packetID == PacketID.TRADE_CLOSE)
		{ //cancel the trade
			if (player.getTrade() == null) 
			{
				Log.writePlayerLog(player, "Tried to close the trade whereas he's not trading.");
				player.close();
				return;
			}
			closeTrade(player);
		}
	}
	
	private static void writeTradeNewConfirmed(Player player)
	{
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.TRADE);
		player.getConnection().writeShort(PacketID.TRADE_NEW_CONFIRMED);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	private static void tradeAccept(Player player)
	{
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.TRADE);
		player.getConnection().writeShort(PacketID.TRADE_ACCEPT);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	public static void addSelfItem(Player player, DragItem slotType, int itemSlot, int tradeSlot)
	{
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.TRADE);
		player.getConnection().writeShort(PacketID.TRADE_ADD_ITEM);
		player.getConnection().writeBoolean(true);
		player.getConnection().writeByte(slotType.getValue());
		player.getConnection().writeInt(itemSlot);
		player.getConnection().writeInt(tradeSlot);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	public static void addOtherItem(Player player, int tradeSlot, Item item)
	{
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.TRADE);
		player.getConnection().writeShort(PacketID.TRADE_ADD_ITEM);
		player.getConnection().writeBoolean(false);
		player.getConnection().writeInt(tradeSlot);
		player.getConnection().writeInt(item.getAmount());
		player.getConnection().writeInt(item.getId());
		if (item.isStuff() || item.isWeapon())
		{
			if (((Stuff)item).getGemSlot1() != GemColor.NONE)
			{
				player.getConnection().writeBoolean(true);
				player.getConnection().writeInt(((Stuff)item).getEquippedGemID(0));
				player.getConnection().writeInt(((Stuff)item).getEquippedGemID(1));
				player.getConnection().writeInt(((Stuff)item).getEquippedGemID(2));
				player.getConnection().endPacket();
				player.getConnection().send();
				return;
			}
			
		}
		player.getConnection().writeBoolean(false);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	public static void closeTrade(Player player)
	{
		tradeCancel(player);
		tradeCancel(player.getPlayerTrade());
		if(player.getPlayerTrade() != null)
		{
			player.getPlayerTrade().setTrade(null);
			player.getPlayerTrade().setPlayerTrade(null);
			player.setPlayerTrade(null);
		}
		player.setTrade(null);
	}
	
	public static void removeItem(Player player, int slot)
	{
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.TRADE);
		player.getConnection().writeShort(PacketID.TRADE_REMOVE_ITEM);
		player.getConnection().writeInt(slot);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	public static void sendTradeItems(Player tradeInit)
	{
		tradeItem(tradeInit, tradeInit.getTrade().getTradeTargetTable(), tradeInit.getTrade().getTradeInitTable());
		tradeItem(tradeInit.getPlayerTrade(), tradeInit.getTrade().getTradeInitTable(), tradeInit.getTrade().getTradeTargetTable());
	}
	
	private static void tradeItem(Player player, Item[] addTable, Item[] deleteTable)
	{
		//player.getConnection().startPacket();
		//player.getConnection().writeShort(PacketID.TRADE);
		//player.getConnection().writeShort(PacketID.TRADE_SEND_ALL_ITEMS);
		for (int i = 0; i < 6; ++i)
		{
			if(addTable[i] != null)
				player.addItem(addTable[i], addTable[i].getAmount());
			if(deleteTable[i] != null)
				player.deleteIdenticalItem(deleteTable[i]);
		}
		//player.getConnection().endPacket();
		//player.getConnection().send();
	}
	
	public static void tradeUnaccept(Player player)
	{
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.TRADE);
		player.getConnection().writeShort(PacketID.TRADE_UNACCEPT);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	private static void write(short packetId, Player player, String name)
	{
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.TRADE);
		player.getConnection().writeShort(packetId);
		player.getConnection().writeString(name);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	/*private static void writeAddItemError(Player player, int slot) {
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
	}*/
	
	private static void confirmTrade(Player player)
	{
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.TRADE);
		player.getConnection().writeShort(PacketID.TRADE_NEW_CONFIRM);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	private static void tradeCancel(Player player)
	{
		if(player != null)
		{
			player.getConnection().startPacket();
			player.getConnection().writeShort(PacketID.TRADE);
			player.getConnection().writeShort(PacketID.TRADE_CLOSE);
			player.getConnection().endPacket();
			player.getConnection().send();
		}
	}
}

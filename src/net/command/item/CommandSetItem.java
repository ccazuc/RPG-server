package net.command.item;

import net.command.Command;
import net.connection.PacketID;
import net.game.Player;
import net.game.item.DragItem;

public class CommandSetItem extends Command {

	@Override
	public void read(Player player) {
		
	}
	
	public static void setAmount(Player player, DragItem type, int slot, int amount) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.SET_ITEM);
		player.getConnection().writeShort(PacketID.SET_ITEM_AMOUNT);
		player.getConnection().writeByte(type.getValue());
		player.getConnection().writeInt(slot);
		player.getConnection().writeInt(amount);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	public static void setNull(Player player, DragItem type, int slot) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.SET_ITEM);
		player.getConnection().writeShort(PacketID.SET_ITEM_NULL);
		player.getConnection().writeByte(type.getValue());
		player.getConnection().writeInt(slot);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	public static void swapItems(Player player, DragItem sourceType, int source, DragItem destionationType, int destination) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.SET_ITEM);
		player.getConnection().writeShort(PacketID.SET_ITEM_SWAP);
		player.getConnection().writeByte(sourceType.getValue());
		player.getConnection().writeInt(source);
		player.getConnection().writeByte(destionationType.getValue());
		player.getConnection().writeInt(destination);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	public static void setSelectable(Player player, DragItem type, int slot) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.SET_ITEM);
		player.getConnection().writeShort(PacketID.SET_ITEM_SELECTABLE);
		player.getConnection().writeByte(type.getValue());
		player.getConnection().writeInt(slot);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	public static void addItem(Player player, DragItem type, int id, int slot, int amount) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.SET_ITEM);
		player.getConnection().writeShort(PacketID.SET_ITEM_ADD);
		player.getConnection().writeByte(type.getValue());
		player.getConnection().writeInt(id);
		player.getConnection().writeInt(slot);
		player.getConnection().writeInt(amount);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
}

package net.command.item;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.item.DragItem;
import net.game.unit.Player;

public class CommandDeleteItem extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		DragItem type = DragItem.values()[connection.readByte()];
		int slot = connection.readInt();
		if(type == null) {
			player.close();
			return;
		}
		if(type == DragItem.INVENTORY) {
			player.setStuff(slot, null);
			write(connection, type, slot);
		}
		else if(type == DragItem.BAG) {
			player.getBag().setBag(slot, null);
			write(connection, type, slot);
		}
	}
	
	public static void write(Connection connection, DragItem slotType, int slot) {
		connection.startPacket();
		connection.writeShort(PacketID.DELETE_ITEM);
		connection.writeByte(slotType.getValue());
		connection.writeInt(slot);
		connection.endPacket();
		connection.send();
	}
}

package net.command.player;

import net.command.Command;
import net.connection.Connection;
import net.game.Player;
import net.game.item.DragItem;
import net.game.item.Item;

public class CommandDragItems extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		DragItem dragType = DragItem.getValue(connection.readChar());
		int slot1 = connection.readInt();
		int slot2 = connection.readInt();
		if(dragType != null) {
			if(dragType == DragItem.BAG) {
				swapBagItem(player, slot1, slot2);
			}
			else if(dragType == DragItem.BANK) {
				
			}
			else if(dragType == DragItem.GUILDBANK) {
				
			}
			else if(dragType == DragItem.INVENTORY) {
				
			}
		} 
		else {
			player.close();
		}
	}
	
	private static void swapBagItem(Player player, int slot1, int slot2) {
		Item temp = player.getBag().getBag(slot2);
		if(player.getBag().getBag(slot1) != null) {
			//player.getBag().setBag(i, stuff, number);
		}
	}
}

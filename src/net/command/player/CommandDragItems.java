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
		DragItem sourceType = DragItem.getValue(connection.readByte());
		int source = connection.readInt();
		DragItem destinationType = DragItem.getValue(connection.readByte());
		int destination = connection.readInt();
		System.out.println(source+" "+player.getBag().getBag(source)+" "+destination+" "+player.getBag().getBag(destination)+" ");
		if(sourceType == null || destinationType == null) {
			player.close();
			return;
		}
		if(sourceType == DragItem.BAG) {
			swapBagItem(player, source, destination);
		}
		else if(sourceType == DragItem.BANK) {
			
		}
		else if(sourceType == DragItem.GUILDBANK) {
			
		}
		else if(sourceType == DragItem.INVENTORY) {
			
		}
	}
	
	private static void swapBagItem(Player player, int source, int destination) {
		Item tmp = player.getBag().getBag(source);
		if(player.getBag().getBag(destination) != null) {
			if(player.getBag().getBag(destination).isStackable() && tmp.isStackable() && tmp.getId() == player.getBag().getBag(destination).getId()) {
				if(tmp.getAmount()+player.getBag().getBag(destination).getAmount() > tmp.getMaxStack()) {
					int amount = tmp.getAmount()+player.getBag().getBag(destination).getAmount()-tmp.getMaxStack();
					tmp.setAmount(amount);
					player.getBag().getBag(destination).setAmount(tmp.getMaxStack());
					CommandSetItem.setAmount(player, DragItem.BAG, source, tmp.getAmount());
					CommandSetItem.setAmount(player, DragItem.BAG, destination, player.getBag().getBag(destination).getAmount());
				}
				else {
					player.getBag().getBag(destination).setAmount(tmp.getAmount()+player.getBag().getBag(destination).getAmount());
					player.getBag().setBag(source, null);
					CommandSetItem.setAmount(player, DragItem.BAG, destination, player.getBag().getBag(destination).getAmount());
					CommandSetItem.setNull(player, DragItem.BAG, source);
				}
				return;
			}
		}
		player.getBag().setBag(source, player.getBag().getBag(destination));
		player.getBag().setBag(destination, tmp);
		CommandSetItem.swapItems(player, DragItem.BAG, source, DragItem.BAG, destination);
	}
}

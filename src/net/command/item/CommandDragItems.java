package net.command.item;

import net.command.Command;
import net.command.player.CommandSendRedAlert;
import net.connection.Connection;
import net.game.DefaultRedAlert;
import net.game.item.DragItem;
import net.game.item.Item;
import net.game.item.stuff.Stuff;
import net.game.log.Log;
import net.game.unit.Player;

public class CommandDragItems extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		DragItem sourceType = DragItem.getValue(connection.readByte());
		int source = connection.readInt();
		DragItem destinationType = DragItem.getValue(connection.readByte());
		int destination = connection.readInt();
		int amount = connection.readInt();
		if(sourceType == null || destinationType == null) {
			player.close();
			return;
		}
		if(sourceType == DragItem.BAG) {
			if(destinationType == DragItem.BAG) {
				swapBagToBagItem(player, source, destination, amount);
			}
			else if(destinationType == DragItem.INVENTORY) {
				swapBagToInventoryItem(player, source, destination);
			}
		}
		else if(sourceType == DragItem.BANK) {
			
		}
		else if(sourceType == DragItem.GUILDBANK) {
			
		}
		else if(sourceType == DragItem.INVENTORY) {
			if(destinationType == DragItem.BAG) {
				swapInventoryToBagItem(player, source, destination);
			}
			else if(destinationType == DragItem.INVENTORY) {
				
			}
		}
	}
	
	private static void swapInventoryToBagItem(Player player, int source, int destination) {
		Stuff sourceItem = player.getStuff(source);
		if(sourceItem == null) {
			return;
		}
		Item destinationItem = player.getBag().getBag(destination);
		if(destinationItem == null) {
			player.getBag().setBag(destination, sourceItem);
			CommandSetItem.swapItems(player, DragItem.INVENTORY, source, DragItem.BAG, destination);
			return;
		}
		if(!destinationItem.isStuff() && !destinationItem.isWeapon()) {
			CommandSendRedAlert.write(player, DefaultRedAlert.CANNOT_EQUIP_ITEM);
			CommandSetItem.setSelectable(player, DragItem.BAG, destination);
			CommandSetItem.setSelectable(player, DragItem.INVENTORY, source);
			return;
		}
		if(((Stuff)destinationItem).getType().getSlot() != source && ((Stuff)destinationItem).getType().getSlot2() != source) {
			CommandSendRedAlert.write(player, DefaultRedAlert.CANNOT_EQUIP_ITEM);
			CommandSetItem.setSelectable(player, DragItem.BAG, destination);
			CommandSetItem.setSelectable(player, DragItem.INVENTORY, source);
			return;
		}
		if(destinationItem.isStuff() && !player.canEquipStuff((Stuff)destinationItem)) {
			CommandSendRedAlert.write(player, DefaultRedAlert.CANNOT_EQUIP_ITEM);
			CommandSetItem.setSelectable(player, DragItem.BAG, destination);
			CommandSetItem.setSelectable(player, DragItem.INVENTORY, source);
			return;
		}
		if(destinationItem.isWeapon() && !player.canEquipWeapon((Stuff)destinationItem)) {
			CommandSendRedAlert.write(player, DefaultRedAlert.CANNOT_EQUIP_ITEM);
			CommandSetItem.setSelectable(player, DragItem.BAG, destination);
			CommandSetItem.setSelectable(player, DragItem.INVENTORY, source);
			return;
		}
		player.setStuff(source, destinationItem);
		player.getBag().setBag(destination, sourceItem);
		CommandSetItem.swapItems(player, DragItem.INVENTORY, source, DragItem.BAG, destination);
	}
	
	private static void swapBagToBagItem(Player player, int source, int destination, int amount) {
		Item tmp = player.getBag().getBag(source);
		if(player.getBag().getBag(source) == null) {
			return;
		}
		if(amount == -1) {
			if(player.getBag().getBag(destination) != null) {
				if(player.getBag().getBag(destination).isStackable() && tmp.isStackable() && tmp.getId() == player.getBag().getBag(destination).getId()) {
					if(tmp.getAmount()+player.getBag().getBag(destination).getAmount() > tmp.getMaxStack()) {
						int tmpAmount = tmp.getAmount()+player.getBag().getBag(destination).getAmount()-tmp.getMaxStack();
						tmp.setAmount(tmpAmount);
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
			return;
		}
		if(amount <= 0) {
			Log.writePlayerLog(player, "Modified default value of draggedAmount for draggedItem to "+amount);
			return;
		}
		if(player.getBag().getBag(source).getAmount() < amount) {
			Log.writePlayerLog(player, "Server's amount < client's amount in swapBagToBagItem in CommandDragItems");
			return;
		}
		if(player.getBag().getBag(destination) == null) {
			Item item = Item.getItem(player.getBag().getBag(source).getId());
			if(item == null) {
				return;
			}
			player.getBag().setBag(destination, item, amount);
			player.getBag().getBag(source).setAmount(player.getBag().getBag(source).getAmount()-amount);
			CommandSetItem.addItem(player, DragItem.BAG, player.getBag().getBag(destination).getId(), destination, player.getBag().getBag(destination).getAmount());
			CommandSetItem.setAmount(player, DragItem.BAG, source, player.getBag().getBag(source).getAmount());
			return;
		}
		if(player.getBag().getBag(source).getId() != player.getBag().getBag(destination).getId()) {
			CommandSetItem.setSelectable(player, DragItem.BAG, source);
			CommandSetItem.setSelectable(player, DragItem.BAG, destination);
			CommandSendRedAlert.write(player, DefaultRedAlert.CANNOT_STACK_ITEM);
			return;
		}
		player.getBag().getBag(destination).setAmount(player.getBag().getBag(destination).getAmount()+amount);
		player.getBag().getBag(source).setAmount(player.getBag().getBag(source).getAmount()-amount);
		CommandSetItem.setAmount(player, DragItem.BAG, destination, player.getBag().getBag(destination).getAmount());
		CommandSetItem.setAmount(player, DragItem.BAG, source, player.getBag().getBag(source).getAmount());
 			
	}
	
	private static void swapBagToInventoryItem(Player player, int source, int destination) {
		Item tmp = player.getBag().getBag(source);
		if(tmp == null) {
			return;
		}
		if(!tmp.isStuff() && !tmp.isWeapon()) {
			System.out.println('a');
			CommandSendRedAlert.write(player, DefaultRedAlert.CANNOT_EQUIP_ITEM);
			CommandSetItem.setSelectable(player, DragItem.BAG, source);
			CommandSetItem.setSelectable(player, DragItem.INVENTORY, destination);
			return;
		}
		if(((Stuff)tmp).getType().getSlot() != destination && ((Stuff)tmp).getType().getSlot2() != destination) {
			System.out.println('b');
			CommandSendRedAlert.write(player, DefaultRedAlert.CANNOT_EQUIP_ITEM);
			CommandSetItem.setSelectable(player, DragItem.BAG, source);
			CommandSetItem.setSelectable(player, DragItem.INVENTORY, destination);
			return;
		}
		if(tmp.isStuff() && !player.canEquipStuff((Stuff)tmp)) {
			System.out.println('c');
			CommandSendRedAlert.write(player, DefaultRedAlert.CANNOT_EQUIP_ITEM);
			CommandSetItem.setSelectable(player, DragItem.BAG, source);
			CommandSetItem.setSelectable(player, DragItem.INVENTORY, destination);
			return;
		}
		if(tmp.isWeapon() && !player.canEquipWeapon((Stuff)tmp)) {
			System.out.println('d');
			CommandSendRedAlert.write(player, DefaultRedAlert.CANNOT_EQUIP_ITEM);
			CommandSetItem.setSelectable(player, DragItem.BAG, source);
			CommandSetItem.setSelectable(player, DragItem.INVENTORY, destination);
			return;
		}
		System.out.println("SWAPPED");
		player.getBag().setBag(source, player.getStuff(destination));
		player.setStuff(destination, tmp);
		CommandSetItem.swapItems(player, DragItem.BAG, source, DragItem.INVENTORY, destination);
	}
}

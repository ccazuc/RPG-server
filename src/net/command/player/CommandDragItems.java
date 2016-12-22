package net.command.player;

import net.command.Command;
import net.connection.Connection;
import net.game.DefaultRedAlert;
import net.game.Player;
import net.game.item.DragItem;
import net.game.item.Item;
import net.game.item.stuff.Stuff;

public class CommandDragItems extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		DragItem sourceType = DragItem.getValue(connection.readByte());
		int source = connection.readInt();
		DragItem destinationType = DragItem.getValue(connection.readByte());
		int destination = connection.readInt();
		int amount = connection.readInt();
		System.out.println(source+" "+player.getBag().getBag(source)+" "+destination+" "+player.getBag().getBag(destination)+" "+amount);
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
			
		}
	}
	
	private static void swapBagToBagItem(Player player, int source, int destination, int amount) {
		Item tmp = player.getBag().getBag(source);
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
			System.out.println("ERROR player modified default value of draggedAmount on swapBagToBagItam in CommandDragItems for player "+player.getCharacterId()+" amount value : "+amount);
			return;
		}
		if(player.getBag().getBag(source) == null) {
			System.out.println("ERROR on swapBagToBagItam in CommandDragItems for player "+player.getCharacterId());
			return;
		}
		if(player.getBag().getBag(source).getAmount() < amount) {
			System.out.println("ERROR amount < client amount on swapBagToBagItam in CommandDragItems for player "+player.getCharacterId());
		}
		if(player.getBag().getBag(destination) == null) {
			Item item = Item.getItem(player.getBag().getBag(source).getId());
			if(item == null) {
				return;
			}
			player.getBag().setBag(destination, item, amount);
			player.getBag().getBag(source).setAmount(player.getBag().getBag(source).getAmount()-amount);
			CommandSetItem.addItem(player, DragItem.BAG, player.getBag().getBag(destination).getItemType(), player.getBag().getBag(destination).getId(), destination, player.getBag().getBag(destination).getAmount());
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
		if(!tmp.isStuff() || !tmp.isWeapon()) {
			CommandSendRedAlert.write(player, DefaultRedAlert.CANNOT_EQUIP_ITEM);
			CommandSetItem.setSelectable(player, DragItem.BAG, source);
			CommandSetItem.setSelectable(player, DragItem.INVENTORY, destination);
			return;
		}
		if(((Stuff)tmp).getType().getSlot() != destination && ((Stuff)tmp).getType().getSlot2() != destination) {
			CommandSendRedAlert.write(player, DefaultRedAlert.CANNOT_EQUIP_ITEM);
			CommandSetItem.setSelectable(player, DragItem.BAG, source);
			CommandSetItem.setSelectable(player, DragItem.INVENTORY, destination);
			return;
		}
		if(player.canEquipStuff((Stuff)tmp)) {
			player.getBag().setBag(source, player.getStuff(destination));
			player.setStuff(destination, tmp);
			CommandSetItem.swapItems(player, DragItem.BAG, source, DragItem.INVENTORY, destination);
		}
	}
}

package net.command.player;

import net.command.Command;
import net.connection.Connection;
import net.game.item.Item;
import net.game.item.gem.GemManager;
import net.game.item.stuff.Stuff;
import net.game.unit.Player;

public class CommandSendSingleBagItem extends Command { //UNUSED
	
	@Override
	public void read(Player player) {
		/*Connection connection = player.getConnection();
		int i = connection.readInt();
		int id = connection.readInt();
		Item item = Item.getItemClone(id);
		if(item.isContainer() || item.isGem()) {
			player.getBag().setBag(i, item);
		}
		else if(item.isStuff() || item.isWeapon()) {
			player.getBag().setBag(i, item);
			int gem1Id = connection.readInt();
			int gem2Id = connection.readInt();
			int gem3Id = connection.readInt();
			if(player.getBag().getBag(i) != null) {
				((Stuff)player.getBag().getBag(i)).setEquippedGem(0, GemManager.getClone(gem1Id));
				((Stuff)player.getBag().getBag(i)).setEquippedGem(1, GemManager.getClone(gem2Id));
				((Stuff)player.getBag().getBag(i)).setEquippedGem(2, GemManager.getClone(gem3Id));
			}
		}
		else if(item.isItem() || item.isPotion()) {
			int amount = connection.readInt();
			player.getBag().setBag(i, item, amount);
		}*/
	}
	
	@Override
	public void write() {}
}

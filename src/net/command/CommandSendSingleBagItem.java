package net.command;

import net.connection.ConnectionManager;
import net.game.item.Item;
import net.game.item.gem.GemManager;
import net.game.item.stuff.Stuff;

public class CommandSendSingleBagItem extends Command {

	public CommandSendSingleBagItem(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	@Override
	public void read() {
		int i = this.connection.readInt();
		int id = this.connection.readInt();
		Item item = Item.getItem(id);
		if(item.isContainer() || item.isGem()) {
			this.player.getBag().setBag(i, item);
		}
		else if(item.isStuff() || item.isWeapon()) {
			this.player.getBag().setBag(i, item);
			int gem1Id = this.connection.readInt();
			int gem2Id = this.connection.readInt();
			int gem3Id = this.connection.readInt();
			if(this.player.getBag().getBag(i) != null) {
				((Stuff)this.player.getBag().getBag(i)).setEquippedGem1(GemManager.getClone(gem1Id));
				((Stuff)this.player.getBag().getBag(i)).setEquippedGem1(GemManager.getClone(gem2Id));
				((Stuff)this.player.getBag().getBag(i)).setEquippedGem1(GemManager.getClone(gem3Id));
			}
		}
		else if(item.isItem() || item.isPotion()) {
			int amount = this.connection.readInt();
			this.player.getBag().setBag(i, item, amount);
		}
	}
	
	@Override
	public void write() {
	}
}

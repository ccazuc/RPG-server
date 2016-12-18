package net.command.item;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.Player;
import net.game.item.Item;
import net.game.item.bag.ContainerManager;
import net.game.item.gem.GemManager;
import net.game.item.potion.PotionManager;
import net.game.item.stuff.StuffManager;
import net.game.item.weapon.WeaponManager;

public class CommandRequestItem extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		int id = connection.readInt();
		byte slotType = connection.readByte();
		int slot = connection.readInt();
		boolean isGem = connection.readBoolean();
		int gemSlot = 0;
		if(isGem) {
			gemSlot = connection.readInt();
		}
		Item item = null;
		boolean knownItem = player.itemHasBeenSendToClient(id);
		if(!knownItem) {
			if(StuffManager.exists(id)) {
				item = StuffManager.getStuff(id);
			}
			else if(WeaponManager.exists(id)) {
				item = WeaponManager.getWeapon(id);
			}
			else if(GemManager.exists(id)) {
				item = GemManager.getGem(id);
			}
			else if(PotionManager.exists(id)) {
				item = PotionManager.getPotion(id);
			}
			else if(ContainerManager.exists(id)) {
				item = ContainerManager.getContainer(id);
				player.addItemSentToClient(id);
			}
			if(item == null) {
				return;
			}
		}
		connection.writeShort(PacketID.REQUEST_ITEM);
		if(knownItem) {
			connection.writeBoolean(true);
			connection.writeInt(id);
		}
		else {
			connection.writeBoolean(false);
			connection.writeItem(item);
		}
		connection.writeByte(slotType);
		connection.writeInt(slot);
		connection.writeBoolean(isGem);
		if(isGem) {
			connection.writeInt(gemSlot);
		}
		connection.send();
		if(item == null) {
			player.addItemSentToClient(id);
		}
		else {
			player.addItemSentToClient(item.getId());
		}
	}
}

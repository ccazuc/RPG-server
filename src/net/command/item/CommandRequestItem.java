package net.command.item;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.Player;
import net.game.item.gem.GemManager;
import net.game.item.potion.PotionManager;
import net.game.item.stuff.StuffManager;
import net.game.item.weapon.WeaponManager;

public class CommandRequestItem extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		int id = connection.readInt();
		if(StuffManager.exists(id)) {
			connection.writeShort(PacketID.STUFF);
			connection.writeStuff(StuffManager.getStuff(id));
			connection.send();
			player.addItemSentToClient(id);
		}
		else if(WeaponManager.exists(id)) {
			connection.writeShort(PacketID.WEAPON);
			connection.writeWeapon(WeaponManager.getWeapon(id));
			connection.send();
			player.addItemSentToClient(id);
		}
		else if(GemManager.exists(id)) {
			connection.writeShort(PacketID.GEM);
			connection.writeGem(GemManager.getGem(id));
			connection.send();
			player.addItemSentToClient(id);
		}
		else if(PotionManager.exists(id)) {
			connection.writeShort(PacketID.POTION);
			connection.writePotion(PotionManager.getPotion(id));
			connection.send();
			player.addItemSentToClient(id);
		}
	}
}

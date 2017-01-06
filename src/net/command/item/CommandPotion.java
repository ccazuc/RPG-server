package net.command.item;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.item.potion.PotionManager;
import net.game.unit.Player;

public class CommandPotion extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		int id = connection.readInt();
		if(PotionManager.exists(id)) {
			connection.startPacket();
			connection.writeShort(PacketID.POTION);
			connection.writePotion(PotionManager.getPotion(id));
			connection.endPacket();
			connection.send();
		}
	}
}

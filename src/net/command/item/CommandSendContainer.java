package net.command.item;

import net.connection.PacketID;
import net.game.unit.Player;

public class CommandSendContainer {

	public static void sendContainer(Player player) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.SEND_EQUIPPED_CONTAINER);
		player.getConnection().writeByte((byte)player.getBag().getEquippedBag().length);
		int i = 0;
		while(i < player.getBag().getEquippedBag().length) {
			if(player.getBag().getEquippedBag(i) == null) {
				player.getConnection().writeByte((byte)0);
				player.getConnection().writeInt(0);
			}
			else {
				player.getConnection().writeByte(player.getBag().getEquippedBag(i).getSize());
				player.getConnection().writeInt(player.getBag().getEquippedBag(i).getId());
			}
			i++;
		}
		player.getConnection().endPacket();
		player.getConnection().send();
	}
}

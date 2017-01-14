package net.command.player.spell;

import net.connection.PacketID;
import net.game.unit.Player;

public class CommandSendGCD {

	public static void sendGCD(Player player, long startTimer, long endTimer) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.SEND_GCD);
		player.getConnection().writeLong(startTimer);
		player.getConnection().writeLong(endTimer);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
}

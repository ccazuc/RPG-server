package net.command.player;

import net.game.Player;
import net.connection.PacketID;
import net.game.DefaultRedAlert;

public class CommandSendRedAlert {

	public static void write(Player player, DefaultRedAlert alert) {
		player.getConnection().writeShort(PacketID.SEND_RED_ALERT);
		player.getConnection().writeBoolean(true);
		player.getConnection().writeByte(alert.getValue());
		player.getConnection().send();
	}
	
	public static void write(Player player, String alert) {
		player.getConnection().writeShort(PacketID.SEND_RED_ALERT);
		player.getConnection().writeBoolean(false);
		player.getConnection().writeString(alert);
		player.getConnection().send();
	}
}

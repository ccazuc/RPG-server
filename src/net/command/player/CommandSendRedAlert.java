package net.command.player;

import net.command.Command;
import net.connection.PacketID;
import net.game.DefaultRedAlert;
import net.game.unit.Player;

public class CommandSendRedAlert extends Command {

	public CommandSendRedAlert(String name, boolean debug)
	{
		super(name, debug);
	}	

	public static void write(Player player, DefaultRedAlert alert) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.SEND_RED_ALERT);
		player.getConnection().writeBoolean(true);
		player.getConnection().writeByte(alert.getValue());
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	public static void write(Player player, String alert) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.SEND_RED_ALERT);
		player.getConnection().writeBoolean(false);
		player.getConnection().writeString(alert);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
}

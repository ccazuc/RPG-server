package net.command.chat;

import net.command.Command;
import net.connection.PacketID;
import net.game.unit.Player;
import net.utils.Color;

public class CommandDefaultMessage extends Command {
	
	public static void write(Player player, DefaultMessage message) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.CHAT_DEFAULT_MESSAGE);
		player.getConnection().writeChar(message.getValue());
		player.getConnection().writeBoolean(true);
		player.getConnection().writeByte(MessageColor.YELLOW.getValue());
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	public static void write(Player player, DefaultMessage message, MessageColor color) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.CHAT_DEFAULT_MESSAGE);
		player.getConnection().writeChar(message.getValue());
		player.getConnection().writeBoolean(true);
		player.getConnection().writeByte(color.getValue());
		player.getConnection().endPacket();
		player.getConnection().send();
	}

	public static void write(Player player, DefaultMessage message, Color color) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.CHAT_DEFAULT_MESSAGE);
		player.getConnection().writeChar(message.getValue());
		player.getConnection().writeBoolean(false);
		player.getConnection().writeColor(color);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
}

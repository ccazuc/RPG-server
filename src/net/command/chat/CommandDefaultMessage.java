package net.command.chat;

import net.command.Command;
import net.connection.PacketID;
import net.game.Player;
import net.utils.Color;

public class CommandDefaultMessage extends Command {
	
	public static void write(Player player, DefaultMessage message) {
		player.getConnection().writeShort(PacketID.CHAT_DEFAULT_MESSAGE);
		player.getConnection().writeChar(message.getValue());
		player.getConnection().writeBoolean(true);
		player.getConnection().writeChar(MessageColor.YELLOW.getValue());
		player.getConnection().send();
	}
	
	public static void write(Player player, DefaultMessage message, MessageColor color) {
		player.getConnection().writeShort(PacketID.CHAT_DEFAULT_MESSAGE);
		player.getConnection().writeChar(message.getValue());
		player.getConnection().writeBoolean(true);
		player.getConnection().writeChar(color.getValue());
		player.getConnection().send();
	}

	public static void write(Player player, DefaultMessage message, Color color) {
		player.getConnection().writeShort(PacketID.CHAT_DEFAULT_MESSAGE);
		player.getConnection().writeChar(message.getValue());
		player.getConnection().writeBoolean(false);
		player.getConnection().writeColor(color);
		player.getConnection().send();
	}
}

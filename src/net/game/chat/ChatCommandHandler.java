package net.game.chat;

import net.Server;
import net.game.unit.Player;
import net.thread.chatcommand.ChatCommandRequest;

public class ChatCommandHandler {

	public static void parse(String message, Player player) {
		//check if mute etc
		Server.addNewChatCommandRequest(new ChatCommandRequest(message, player));
	}
}

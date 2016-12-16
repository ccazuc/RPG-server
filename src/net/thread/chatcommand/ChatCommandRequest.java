package net.thread.chatcommand;

import net.command.chat.CommandSendMessage;
import net.command.chat.MessageType;
import net.game.Player;
import net.game.chat.StoreChatCommand;

public class ChatCommandRequest {

	private String command;
	private Player player;
	
	public ChatCommandRequest(String command, Player player) {
		this.command = command;
		this.player = player;
	}
	
	public String getCommand() {
		return this.command;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public void execute() {
		if(this.player == null) {
			return;
		}
		String command = checkCommand(this.command);
		if(StoreChatCommand.contains(command)) {
			StoreChatCommand.get(command).handle(this.command, this.player);
		}
		else {
			CommandSendMessage.selfWithoutAuthor(this.player.getConnection(), "That command doesn't exist, type .help for help.", MessageType.SELF);
		}
	}
	
	private static String checkCommand(String message) {
		int i = 1;
		while(i < message.length() && message.charAt(i) != ' ') {
			i++;
		}
		return message.substring(1, i);
	}
}

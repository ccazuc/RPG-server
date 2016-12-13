package net.game.chat;

import net.game.Player;

public class ChatSubCommand {

	private String name;
	
	public ChatSubCommand(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void handle(Player player) {}
}

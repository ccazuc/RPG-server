package net.thread.chatcommand;

import net.game.unit.Player;

public class Who {

	private String word;
	private Player player;
	
	public Who(String word, Player player) {
		this.word = word;
		this.player = player;
	}
	
	public String getWord() {
		return this.word;
	}
	
	public Player getPlayer() {
		return this.player;
	}
}

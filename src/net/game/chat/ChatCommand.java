package net.game.chat;

import java.util.ArrayList;

import net.game.AccountRank;
import net.game.Player;

public class ChatCommand {

	protected AccountRank accountRank;
	protected String name;
	protected ArrayList<ChatSubCommand> subCommandList;
	
	public ChatCommand(String name, AccountRank accountLevel) {
		this.accountRank = accountLevel;
		this.name = name;
	}
	
	public void addSubCommand(ChatSubCommand command) {
		if(this.subCommandList == null) {
			this.subCommandList = new ArrayList<ChatSubCommand>();
		}
		this.subCommandList.add(command);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void handle(String command, Player player) {}
	
	public String printSubCommandError() {
		StringBuilder result = new StringBuilder();
		result.append("Available command for "+this.name+" : \n");
		int i = 0;
		while(i < this.subCommandList.size()) {
			result.append("- "+this.subCommandList.get(i).getName());
			i++;
			if(i < this.subCommandList.size()) {
				result.append('\n');
			}
		}
		return result.toString();
	}
}

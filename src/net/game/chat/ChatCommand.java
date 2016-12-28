package net.game.chat;

import java.util.ArrayList;

import net.game.AccountRank;
import net.game.Player;

public class ChatCommand {

	protected String helpMessage = "";
	protected AccountRank rank;
	protected String name;
	protected ArrayList<ChatSubCommand> subCommandList;
	
	public ChatCommand(String name, String helpMessage, AccountRank accountLevel) {
		this.rank = accountLevel;
		this.helpMessage = helpMessage;
		this.name = name;
	}
	
	public ChatCommand(String name, AccountRank accountLevel) {
		this.rank = accountLevel;
		this.name = name;
	}
	
	public void addSubCommand(ChatSubCommand command) {
		if(this.subCommandList == null) {
			this.subCommandList = new ArrayList<ChatSubCommand>();
		}
		this.subCommandList.add(command);
	}
	
	public String printHelpMessage() {
		return this.helpMessage;
	}
	
	@SuppressWarnings("unused")
	public void handle(String command, Player player) {}
	
	public String printSubCommandError(Player player) {
		StringBuilder result = new StringBuilder();
		int i = 0;
		boolean initHeader = false;
		while(i < this.subCommandList.size()) {
			if(player.getAccountRank().superiorOrEqualsTo(this.subCommandList.get(i).getRank())) {
				if(!initHeader) {
					result.append("Command "+this.name+" have subcommands:");
					initHeader = true;
				}
				result.append("\n"+this.subCommandList.get(i).getName());
			}
			i++;
		}
		if(!initHeader) {
			result.append("No command available for "+this.name);
		}
		return result.toString();
	}
	
	public String getName() {
		return this.name;
	}
	
	public AccountRank getRank() {
		return this.rank;
	}
}

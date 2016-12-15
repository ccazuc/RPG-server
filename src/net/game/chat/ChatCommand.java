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
	
	@SuppressWarnings("unused")
	public void handle(String command, Player player) {}
	
	public String printSubCommandError(Player player) {
		StringBuilder result = new StringBuilder();
		int i = 0;
		boolean initHeader = false;
		while(i < this.subCommandList.size()) {
			if(player.getAccountRank().superiorOrEqualsTo(this.subCommandList.get(i).getRank())) {
				if(!initHeader) {
					result.append("Available command for "+this.name+" : \n");
					initHeader = true;
				}
				result.append("- "+this.subCommandList.get(i).getName());
			}
			i++;
			if(i < this.subCommandList.size() && initHeader) {
				result.append('\n');
			}
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
		return this.accountRank;
	}
}

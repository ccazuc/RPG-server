package net.game.chat;

import java.util.ArrayList;

import net.game.AccountRank;
import net.game.Player;

public class ChatSubCommand {

	protected String parentName;
	protected String name;
	protected AccountRank rank;
	protected ArrayList<ChatSubCommand> commandList;
	
	public ChatSubCommand(String name, String parentName, AccountRank rank) {
		this.name = name;
		this.rank = rank;
		this.parentName = parentName;
	}
	
	public void addSubCommand(ChatSubCommand command) {
		if(this.commandList == null) {
			this.commandList = new ArrayList<ChatSubCommand>();
		}
		this.commandList.add(command);
	}
	
	public String printSubCommandError(Player player) {
		StringBuilder result = new StringBuilder();
		int i = 0;
		boolean initHeader = false;
		while(i < this.commandList.size()) {
			if(player.getAccountRank().superiorOrEqualsTo(this.commandList.get(i).getRank())) {
				if(!initHeader) {
					result.append("Available command for "+this.parentName+' '+this.name+" : \n");
					initHeader = true;
				}
				result.append("- "+this.commandList.get(i).getName());
			}
			i++;
			if(i < this.commandList.size() && initHeader) {
				result.append('\n');
			}
		}
		if(!initHeader) {
			result.append("No command available for "+this.parentName+' '+this.name);
		}
		return result.toString();
	}
	
	public String getName() {
		return this.name;
	}
	
	public AccountRank getRank() {
		return this.rank;
	}
	
	@SuppressWarnings("unused")
	public void handle(String[] command, Player player) {}
}

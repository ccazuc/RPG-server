package net.game.chat;

import java.util.ArrayList;

import net.game.AccountRank;
import net.game.unit.Player;

public class ChatSubCommand {

	protected String helpMessage;
	protected String parentName;
	protected String name;
	protected AccountRank rank;
	protected ArrayList<ChatSubCommand> commandList;
	
	public ChatSubCommand(String name, String parentName, AccountRank rank) {
		this.name = name;
		this.rank = rank;
		this.parentName = parentName;
	}
	
	public ChatSubCommand(String name, String parentName, String helpMessage, AccountRank rank) {
		this.name = name;
		this.rank = rank;
		this.parentName = parentName;
		this.helpMessage = helpMessage;
	}
	
	public void addSubCommand(ChatSubCommand command) {
		if(this.commandList == null) {
			this.commandList = new ArrayList<ChatSubCommand>();
		}
		this.commandList.add(command);
	}
	
	public String printHelpMessage() {
		return this.helpMessage;
	}
	
	public String printSubCommandError(Player player) {
		StringBuilder result = new StringBuilder();
		int i = 0;
		boolean initHeader = false;
		while(i < this.commandList.size()) {
			if(player.getAccountRank().superiorOrEqualsTo(this.commandList.get(i).getRank())) {
				if(!initHeader) {
					result.append("Command "+this.parentName+' '+this.name+" have subcommands:");
					initHeader = true;
				}
				result.append("\n"+this.commandList.get(i).getName());
			}
			i++;
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

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
	
	public String printSubCommandError() {
		StringBuilder result = new StringBuilder();
		result.append("Available command for "+this.parentName+' '+this.name+" : \n");
		int i = 0;
		while(i < this.commandList.size()) {
			result.append("- "+this.commandList.get(i).getName());
			i++;
			if(i < this.commandList.size()) {
				result.append('\n');
			}
		}
		return result.toString();
	}
	
	public String getName() {
		return this.name;
	}
	
	@SuppressWarnings("unused")
	public void handle(String[] command, Player player) {}
}

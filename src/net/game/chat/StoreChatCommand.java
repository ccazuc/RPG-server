package net.game.chat;

import java.util.HashMap;

import net.Server;
import net.command.chat.CommandDefaultMessage;
import net.command.chat.CommandSendMessage;
import net.command.chat.DefaultMessage;
import net.command.chat.MessageType;
import net.game.AccountRank;
import net.game.Player;
import net.utils.Color;

public class StoreChatCommand {

	private final static HashMap<String, ChatCommand> commandMap = new HashMap<String, ChatCommand>();
	
	private final static ChatCommand account = new ChatCommand("account", AccountRank.PLAYER) {
		
		@Override
		public void handle(String command, Player player) {
			command = command.trim().toLowerCase();
			if(command.equals('.'+this.name)) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Account level : "+(player.getAccountRank().getValue()+1), MessageType.SELF);
			}
			else {
				String[] value = command.split(" ");
				int i = 0;
				while(i < this.subCommandList.size()) {
					if(this.subCommandList.get(i).getName().equals(value[1])) {
						this.subCommandList.get(i).handle(value, player);
						return;
					}
					i++;
				}
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(), MessageType.SELF);
			}
		}
	};
	private final static ChatSubCommand account_onlinelist = new ChatSubCommand("onlinelist", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(player.getAccountRank().getValue() >= this.rank.getValue()) {
				CommandDefaultMessage.write(player, DefaultMessage.NOT_ENOUGH_RIGHT);
				return;
			}
			StringBuilder builder = new StringBuilder();
			for(Player players : Server.getInGamePlayerList().values()) {
				builder.append(players.getName()+" : "+players.getAccountId());
			}
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), builder.toString(), MessageType.SELF);
		}
	};
	private final static ChatSubCommand account_set = new ChatSubCommand("set", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!(player.getAccountRank().getValue() >= this.rank.getValue())) {
				CommandDefaultMessage.write(player, DefaultMessage.NOT_ENOUGH_RIGHT);
				return;
			}
			if(!(value.length >= 3)) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(), MessageType.SELF);
				return;
			}
			int i = 0;
			while(i < this.commandList.size()) {
				if(this.commandList.get(i).getName().equals(value[2])) {
					this.commandList.get(i).handle(value, player);
					return;
				}
				i++;
			}
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(), MessageType.SELF);
		}
	};
	private final static ChatSubCommand account_set_gmlevel = new ChatSubCommand("gmlevel", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!(player.getAccountRank().getValue() >= this.rank.getValue())) {
				CommandDefaultMessage.write(player, DefaultMessage.NOT_ENOUGH_RIGHT);
				return;
			}
			if(!(value.length >= 5)) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Invalid synthax : .account set gmlevel [account_name] [account_level]", MessageType.SELF);
				return;
			}
			if(Server.isInteger(value[4])) {
				int level = Integer.parseInt(value[4]);
			}
			else {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Invalid synthax : .account set gmlevel [account_name] [account_level]", MessageType.SELF);
			}
		}
	};
	private final static ChatCommand announce = new ChatCommand("announce", AccountRank.PLAYER) {
		
		@Override
		public void handle(String command, Player player) {
			command = command.trim().toLowerCase();
			if(command.startsWith('.'+this.name)) {
				for(Player players : Server.getInGamePlayerList().values()) {
					CommandSendMessage.selfWithoutAuthor(players.getConnection(), "[GM ANNOUNCE] "+command.substring(this.name.length()+1), MessageType.SELF, new Color(51/255f, 204/255f, 221/255f));
				}
			}
			else {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Invalid synthax : .announce [message]", MessageType.SELF);
			}
		}
	};
	
	public static void initChatCommandMap() {
		account.addSubCommand(account_onlinelist);
		account_set.addSubCommand(account_set_gmlevel);
		account.addSubCommand(account_set);
		commandMap.put(account.getName(), account);
		commandMap.put(announce.getName(), announce);
	}
	
	public static boolean contains(String command) {
		return commandMap.containsKey(command);
	}
	
	public static ChatCommand get(String command) {
		return commandMap.get(command);
	}
}

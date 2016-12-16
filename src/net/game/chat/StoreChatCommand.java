package net.game.chat;

import java.util.HashMap;

import net.Server;
import net.command.chat.CommandDefaultMessage;
import net.command.chat.CommandSendMessage;
import net.command.chat.DefaultMessage;
import net.command.chat.MessageType;
import net.game.AccountRank;
import net.game.Player;
import net.game.manager.AccountManager;
import net.game.manager.CharacterManager;
import net.utils.Color;

public class StoreChatCommand {
	
	private final static long MS_IN_A_YEAR = 31536000000l;
	private final static long MS_IN_A_MONTH = 1036800000l;
	private final static long MS_IN_A_WEEK = 604800000l;
	private final static long MS_IN_A_DAY = 86400000l;
	private final static long MS_IN_AN_HOUR = 3600000l;
	private final static long MS_IN_A_MINUTE = 60000l;

	final static HashMap<String, ChatCommand> commandMap = new HashMap<String, ChatCommand>();
	
	private final static ChatCommand account = new ChatCommand("account", AccountRank.PLAYER) {
		
		@Override
		public void handle(String command, Player player) {
			command = command.trim().toLowerCase();
			if(command.equals('.'+this.name)) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Account level : "+(player.getAccountRank().getValue()), MessageType.SELF);
			}
			else {
				String[] value = command.split(" ");
				if(value.length < 2) {
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
					return;
				}
				int i = 0;
				while(i < this.subCommandList.size()) {
					if(this.subCommandList.get(i).getName().equals(value[1])) {
						this.subCommandList.get(i).handle(value, player);
						return;
					}
					i++;
				}
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
			}
		}
	};
	private final static ChatSubCommand account_onlinelist = new ChatSubCommand("onlinelist", "account", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(player.getAccountRank().getValue() >= this.rank.getValue()) {
				CommandDefaultMessage.write(player, DefaultMessage.NOT_ENOUGH_RIGHT);
				return;
			}
			StringBuilder builder = new StringBuilder();
			for(Player players : Server.getInGamePlayerList().values()) {
				builder.append(players.getAccountName()+" : "+players.getAccountId());
			}
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), builder.toString(), MessageType.SELF);
		}
	};
	private final static ChatSubCommand account_set = new ChatSubCommand("set", "account", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(player.getAccountRank().getValue() < this.rank.getValue()) {
				CommandDefaultMessage.write(player, DefaultMessage.NOT_ENOUGH_RIGHT);
				return;
			}
			if(value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
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
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
		}
	};
	private final static ChatSubCommand account_set_gmlevel = new ChatSubCommand("gmlevel", "account_set", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(player.getAccountRank().getValue() < this.rank.getValue()) {
				CommandDefaultMessage.write(player, DefaultMessage.NOT_ENOUGH_RIGHT);
				return;
			}
			if(value.length < 5) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Invalid synthax : .account set gmlevel [account_name] [account_level]", MessageType.SELF);
				return;
			}
			if(Server.isInteger(value[4])) {
				String accountName = value[3];
				int level = Integer.parseInt(value[4]);
				if(!(level >= 1 && level <= AccountRank.values().length)) {
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Invalid value for [account_level] on .account set gmlevel [account_name] [account_level]", MessageType.SELF);
					return;
				}
				int accountId = AccountManager.loadAccountIDFromName(accountName);
				if(accountId == -1) {
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Account "+accountName+" not found.", MessageType.SELF);
					return;
				}
				AccountManager.updateAccountRank(accountId, level);
				Player tmp = Server.getInGameCharacterByAccount(accountId);
				if(tmp == null) {
					return;
				}
				tmp.setAccountRank(AccountRank.get(level));
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
			if(command.equals('.'+this.name)) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Invalid value for [message] in .announce [message]", MessageType.SELF);
				return;
			}
			for(Player players : Server.getInGamePlayerList().values()) {
				CommandSendMessage.selfWithoutAuthor(players.getConnection(), "[GM ANNOUNCE] "+command.substring(this.name.length()+1), MessageType.SELF, new Color(51/255f, 204/255f, 221/255f));
			}
		}
	};
	private final static ChatCommand ban = new ChatCommand("ban", AccountRank.PLAYER) {
		
		@Override
		public void handle(String command, Player player) {
			command = command.trim().toLowerCase();
			if(command.length() < 7) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
				return;
			}
			String[] value = command.split(" ");
			if(value.length < 2) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
				return;
			}
			int i = 0;
			while(i < this.subCommandList.size()) {
				if(this.subCommandList.get(i).getName().equals(value[1])) {
					this.subCommandList.get(i).handle(value, player);
					return;
				}
				i++;
			}
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
		}
	};
	private final static ChatSubCommand ban_account = new ChatSubCommand("account", "ban", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(player.getAccountRank().getValue() < this.rank.getValue()) {
				CommandDefaultMessage.write(player, DefaultMessage.NOT_ENOUGH_RIGHT);
				return;
			}
			if(value.length < 5) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Invalid synthax : .ban account [account_name] [duration] [reason]", MessageType.SELF);
				return;
			}
			String accountName = value[2];
			String banTime = value[3];
			String reason = value[4];
			long banTimer = 0;
			if(Server.isInteger(banTime)) {
				banTimer = Integer.parseInt(banTime);
			}
			else {
				banTimer = convStringTimerToMS(banTime);
				if(banTimer == -666) {
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Invalid synthax : .ban account [account_name] [duration] [reason]", MessageType.SELF);
					return;
				}
			}
			int accountId = AccountManager.loadAccountIDFromName(accountName);
			if(accountId == -1) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Account "+accountName+" not found.", MessageType.SELF);
				return;
			}
			long timer = System.currentTimeMillis();
			if(banTimer < 0) {
				banTimer = -1-System.currentTimeMillis();
			}
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Account "+accountId+" banned "+banTime+" for : "+reason, MessageType.SELF);
			AccountManager.banAccount(accountId, timer, banTimer+timer, player.getName(), reason);
			Player banned = Server.getInGameCharacterByAccount(accountId);
			if(banned == null) {
				return;
			}
			banned.close();
		}
	};
	private final static ChatSubCommand ban_character = new ChatSubCommand("character", "ban", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(player.getAccountRank().getValue() < this.rank.getValue()) {
				CommandDefaultMessage.write(player, DefaultMessage.NOT_ENOUGH_RIGHT);
				return;
			}
			if(value.length < 5) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Invalid synthax : .ban character [character_name] [duration] [reason]", MessageType.SELF);
				return;
			}
			String characterName = value[2];
			String banTime = value[3];
			String reason = value[4];
			long banTimer = 0;
			if(Server.isInteger(banTime)) {
				banTimer = Integer.parseInt(banTime);
			}
			else {
				banTimer = convStringTimerToMS(banTime);
				if(banTimer == -666) {
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Invalid synthax : .ban character [character_name] [duration] [reason]", MessageType.SELF);
					return;
				}
			}
			int characterId = CharacterManager.loadCharacterIDFromName(characterName);
			if(characterId == -1) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Character "+characterName+" not found.", MessageType.SELF);
				return;
			}
			long timer = System.currentTimeMillis();
			if(banTimer < 0) {
				banTimer = -1-System.currentTimeMillis();
			}
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Character "+characterName+" banned "+banTime+" for : "+reason, MessageType.SELF);
			CharacterManager.banCharacter(characterId, timer, banTimer+timer, player.getName(), reason);
			Player banned = Server.getInGameCharacter(characterId);
			if(banned == null) {
				return;
			}
			banned.close();
		}
	};
	private final static ChatSubCommand ban_ip = new ChatSubCommand("ip", "ban", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(player.getAccountRank().getValue() < this.rank.getValue()) {
				CommandDefaultMessage.write(player, DefaultMessage.NOT_ENOUGH_RIGHT);
				return;
			}
			if(value.length < 5) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Invalid synthax : .ban ip [ip_adress] [duration] [reason]", MessageType.SELF);
				return;
			}
			String ipAdress = value[2];
			String banTime = value[3];
			String reason = value[4];
			long banTimer = 0;
			if(Server.isInteger(banTime)) {
				banTimer = Integer.parseInt(banTime);
			}
			else {
				banTimer = convStringTimerToMS(banTime);
				if(banTimer == -666) {
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Invalid synthax : .ban ip [ip_adress] [duration] [reason]", MessageType.SELF);
					return;
				}
			}
			int characterId = CharacterManager.loadCharacterIDFromName(ipAdress);
			if(characterId == -1) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Character "+ipAdress+" not found.", MessageType.SELF);
				return;
			}
			long timer = System.currentTimeMillis();
			if(banTimer < 0) {
				banTimer = -1-System.currentTimeMillis();
			}
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Character "+ipAdress+" banned "+banTime+" for : "+reason, MessageType.SELF);
			CharacterManager.banCharacter(characterId, timer, banTimer+timer, player.getName(), reason);
			Player banned = Server.getInGameCharacter(characterId);
			if(banned == null) {
				return;
			}
			banned.close();
		}
	};
	private final static ChatCommand help = new ChatCommand("help", AccountRank.PLAYER) {
		
		@Override
		public void handle(String command, Player player) {
			command = command.trim().toLowerCase();
			if(command.equals('.'+this.name)) {
				StringBuilder builder = new StringBuilder();
				builder.append("Available commands:");
				for(ChatCommand chatCommand : commandMap.values()) {
					if(player.getAccountRank().superiorOrEqualsTo(chatCommand.getRank())) {
						builder.append("\n    "+chatCommand.getName());
					}
				}
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), builder.toString(), MessageType.SELF);
			}
			else {
				String[] value = command.split(" ");
				if(value.length < 2) {
					return;
				}
				if(commandMap.containsKey(value[1])) {
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), commandMap.get(value[1]).printSubCommandError(player), MessageType.SELF);
				}
			}
		}
	};
	private final static ChatCommand server = new ChatCommand("server", AccountRank.PLAYER) {
		
		@Override
		public void handle(String command, Player player) {
			command = command.trim().toLowerCase();
			if(command.equals('.'+this.name)) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
			}
			else {
				String[] value = command.split(" ");
				if(value.length < 2) {
					return;
				}
				int i = 0;
				while(i < this.subCommandList.size()) {
					if(this.subCommandList.get(i).getName().equals(value[1])) {
						this.subCommandList.get(i).handle(value, player);
						return;
					}
					i++;
				}
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
			}
		}
	};
	private final static ChatSubCommand server_exit = new ChatSubCommand("exit", "server", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(player.getAccountRank().getValue() < this.rank.getValue()) {
				CommandDefaultMessage.write(player, DefaultMessage.NOT_ENOUGH_RIGHT);
				return;
			}
			System.out.println("[SERVER EXIT] requested by "+player.getName());
			Server.close();
		}
	};
	private final static ChatSubCommand server_info = new ChatSubCommand("info", "server", AccountRank.PLAYER) {
		
		@Override
		public void handle(String[] value, Player player) {
			StringBuilder builder = new StringBuilder();
			builder.append("Server informations :\n");
			builder.append("Server message of the day :\n"+Server.getServerMessageOfTheDay()+'\n');
			builder.append("Online since "+convMillisToDate(System.currentTimeMillis()-Server.getServerStartTimer())+'\n');
			builder.append("Online player(s) : "+Server.getInGamePlayerList().size());
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), builder.toString(), MessageType.SELF);
		}
	};
	private final static ChatSubCommand server_motd = new ChatSubCommand("motd", "server", AccountRank.PLAYER) {
		
		@Override
		public void handle(String[] value, Player player) {
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), Server.getServerMessageOfTheDay(), MessageType.SELF);
		}
	};
	private final static ChatSubCommand server_set = new ChatSubCommand("set", "server", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(player.getAccountRank().getValue() < this.rank.getValue()) {
				CommandDefaultMessage.write(player, DefaultMessage.NOT_ENOUGH_RIGHT);
				return;
			}
			if(value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
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
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
		}
	};
	private final static ChatSubCommand server_set_motd = new ChatSubCommand("motd", "server_set", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(player.getAccountRank().getValue() < this.rank.getValue()) {
				CommandDefaultMessage.write(player, DefaultMessage.NOT_ENOUGH_RIGHT);
				return;
			}
			if(value.length < 4) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [motd] in .server set motd [motd]", MessageType.SELF);
				return;
			}
			Server.setServerMessageOfTheDay(value[3]);
		}
	};
	private final static ChatSubCommand server_set_closed = new ChatSubCommand("closed", "server_set", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(player.getAccountRank().getValue() < this.rank.getValue()) {
				CommandDefaultMessage.write(player, DefaultMessage.NOT_ENOUGH_RIGHT);
				return;
			}
			if(value.length < 4) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [closed] in .server set closed [on/off]", MessageType.SELF);
				return;
			}
			if(value[3].equals("on")) {
				Server.setIsAcceptingConnection(true);
			}
			else if(value[3].equals("off")) {
				Server.setIsAcceptingConnection(false);
			}
			else {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [closed] in .server set closed [on/off]", MessageType.SELF);
			}
		}
	};
	
	public static void initChatCommandMap() {
		account.addSubCommand(account_onlinelist);
		account_set.addSubCommand(account_set_gmlevel);
		account.addSubCommand(account_set);
		commandMap.put(account.getName(), account);
		commandMap.put(announce.getName(), announce);
		ban.addSubCommand(ban_account);
		ban.addSubCommand(ban_character);
		ban.addSubCommand(ban_ip);
		commandMap.put(ban.getName(), ban);
		commandMap.put(help.getName(), help);
		server.addSubCommand(server_exit);
		server.addSubCommand(server_info);
		server.addSubCommand(server_motd);
		server.addSubCommand(server_set);
		server_set.addSubCommand(server_set_motd);
		server_set.addSubCommand(server_set_closed);
		commandMap.put(server.getName(), server);
	}
	
	static long convStringTimerToMS(String timer) {
		int valueStart = 0;
		long value = 0;
		int i = 0;
		while(i < timer.length()) {
			if(timer.charAt(i) == 'y') {
				String tmp = timer.substring(valueStart, i);
				if(Server.isInteger(tmp)) {
					value+= Integer.parseInt(tmp)*31536000000l;
					valueStart = i+1;
				}
				else {
					return -666;
				}
			}
			else if(timer.charAt(i) == 'm') {
				String tmp = timer.substring(valueStart, i);
				if(Server.isInteger(tmp)) {
					value+= Integer.parseInt(tmp)*2592000000l;
					valueStart = i+1;
				}
				else {
					return -666;
				}
			}
			else if(timer.charAt(i) == 'w') {
				String tmp = timer.substring(valueStart, i);
				if(Server.isInteger(tmp)) {
					value+= Integer.parseInt(tmp)*604800000;
					valueStart = i+1;
				}
				else {
					return -666;
				}
			}
			else if(timer.charAt(i) == 'd') {
				String tmp = timer.substring(valueStart, i);
				if(Server.isInteger(tmp)) {
					value+= Integer.parseInt(tmp)*86400000;
					valueStart = i+1;
				}
				else {
					return -666;
				}
				
			}
			else if(timer.charAt(i) == 'h') {
				String tmp = timer.substring(valueStart, i);
				if(Server.isInteger(tmp)) {
					value+= Integer.parseInt(tmp)*3600000;
					valueStart = i+1;
				}
				else {
					return -666;
				}
			}
			else if(timer.charAt(i) == 's') {
				String tmp = timer.substring(valueStart, i);
				if(Server.isInteger(tmp)) {
					value+= Integer.parseInt(tmp)*1000;
					valueStart = i+1;
				}
				else {
					return -666;
				}
			}
			i++;
		}
		return value;
	}
	
	public static String convMillisToDate(long millis) {
		StringBuilder builder = new StringBuilder();
		float value = millis/MS_IN_A_YEAR;
		if(value >= 2) {
			builder.append((int)value+" years ");
		}
		else if(value >= 1) {
			builder.append((int)value+" year ");
		}
		value = (millis%MS_IN_A_YEAR)/MS_IN_A_MONTH;
		if(value >= 2) {
			builder.append((int)value+" months ");
		}
		else if(value >= 1) {
			builder.append((int)value+" month ");
		}
		value = (millis%MS_IN_A_MONTH)/MS_IN_A_WEEK;
		if(value >= 2) {
			builder.append((int)value+" weeks ");
		}
		else if(value >= 1) {
			builder.append((int)value+" week ");
		}
		value = (millis%MS_IN_A_WEEK)/MS_IN_A_DAY;
		if(value >= 2) {
			builder.append((int)value+" days ");
		}
		else if(value >= 1) {
			builder.append((int)value+" day ");
		}
		value = (millis%MS_IN_A_DAY)/MS_IN_AN_HOUR;
		if(value >= 2) {
			builder.append((int)value+" hours ");
		}
		else if(value >= 1) {
			builder.append((int)value+" hour ");
		}
		value = (millis%MS_IN_AN_HOUR)/MS_IN_A_MINUTE;
		if(value >= 2) {
			builder.append((int)value+" minutes ");
		}
		else if(value >= 1) {
			builder.append((int)value+" minute ");
		}
		value = (millis%MS_IN_A_MINUTE)/1000;
		if(value >= 2) {
			builder.append((int)value+" seconds ");
		}
		else if(value >= 1) {
			builder.append((int)value+" second ");
		}
		return builder.toString();
	}
	
	public static boolean contains(String command) {
		return commandMap.containsKey(command);
	}
	
	public static ChatCommand get(String command) {
		return commandMap.get(command);
	}
}

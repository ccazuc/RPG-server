package net.game.chat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import jdo.JDOStatement;
import net.Server;
import net.command.chat.CommandDefaultMessage;
import net.command.chat.CommandPlayerNotFound;
import net.command.chat.CommandSendMessage;
import net.command.chat.DefaultMessage;
import net.command.chat.MessageType;
import net.game.AccountRank;
import net.game.Player;
import net.game.item.Item;
import net.game.manager.AccountMgr;
import net.game.manager.BanMgr;
import net.game.manager.CharacterMgr;
import net.game.manager.DebugMgr;
import net.thread.sql.SQLDatas;
import net.utils.Color;

public class StoreChatCommand {
	
	private final static long MS_IN_A_YEAR = 31536000000l;
	private final static long MS_IN_A_MONTH = 1036800000l;
	private final static long MS_IN_A_WEEK = 604800000l;
	private final static long MS_IN_A_DAY = 86400000l;
	private final static long MS_IN_AN_HOUR = 3600000l;
	private final static long MS_IN_A_MINUTE = 60000l;
	static JDOStatement getBanInfoAccountName;
	static JDOStatement getBanInfoCharacterName;
	static JDOStatement getBanInfoIPAdress;
	static JDOStatement getBanListAccount;
	static JDOStatement getBanListCharacter;
	static JDOStatement getBanListAccountPattern;
	static JDOStatement getBanListCharacterPattern;

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
	private final static ChatSubCommand account_onlinelist = new ChatSubCommand("onlinelist", "account", "Syntax: .account onlinelist \n\n List all online accounts.", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
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
			if(!checkRank(player, this.rank)) {
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
	private final static ChatSubCommand account_set_gmlevel = new ChatSubCommand("gmlevel", "account_set", "Invalid synthax : .account set gmlevel [account_name] [account_level]", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 5) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
				return;
			}
			if(Server.isInteger(value[4])) {
				String accountName = value[3];
				int level = Integer.parseInt(value[4]);
				if(!(level >= 1 && level <= AccountRank.values().length)) {
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
					return;
				}
				int accountId = AccountMgr.loadAccountIDFromName(accountName);
				if(accountId == -1) {
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Account "+accountName+" not found.", MessageType.SELF);
					return;
				}
				AccountMgr.updateAccountRank(accountId, level);
				Player tmp = Server.getInGameCharacterByAccount(accountId);
				if(tmp == null) {
					return;
				}
				tmp.setAccountRank(AccountRank.get(level));
			}
			else {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
			}
		}
	};
	private final static ChatCommand announce = new ChatCommand("announce", "Synthax : .announce [message] \n\n Send an announce to all players", AccountRank.PLAYER) {
		
		@Override
		public void handle(String command, Player player) {
			command = command.trim().toLowerCase();
			if(command.equals('.'+this.name)) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
				return;
			}
			for(Player players : Server.getInGamePlayerList().values()) {
				CommandSendMessage.selfWithoutAuthor(players.getConnection(), "[GM ANNOUNCE] "+command.substring(this.name.length()+1), MessageType.SELF, new Color(0/255f, 208/255f, 225/255f));
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
	private final static ChatSubCommand ban_account = new ChatSubCommand("account", "ban", "Synthax : .ban account [account_name] [duration] [reason]", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 5) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
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
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
					return;
				}
			}
			int accountId = AccountMgr.loadAccountIDFromName(accountName);
			if(accountId == -1) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Account "+accountName+" not found.", MessageType.SELF);
				return;
			}
			long timer = System.currentTimeMillis();
			if(banTimer < 0) {
				banTimer = -1-System.currentTimeMillis();
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Account "+accountId+" banned permanently for : "+reason, MessageType.SELF);
			}
			else {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Account "+accountId+" banned "+banTime+" for : "+reason, MessageType.SELF);
			}
			BanMgr.banAccount(accountId, timer, banTimer+timer, player.getName(), reason);
			Player banned = Server.getInGameCharacterByAccount(accountId);
			if(banned == null) {
				return;
			}
			banned.close();
		}
	};
	private final static ChatSubCommand ban_character = new ChatSubCommand("character", "ban", "Synthax : .ban character [character_name] [duration] [reason]", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 5) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
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
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
					return;
				}
			}
			int characterId = CharacterMgr.loadCharacterIDFromName(characterName);
			if(characterId == -1) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Character "+characterName+" not found.", MessageType.SELF);
				return;
			}
			long timer = System.currentTimeMillis();
			if(banTimer < 0) {
				banTimer = -1-System.currentTimeMillis();
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Character "+characterName+" banned permanently for : "+reason, MessageType.SELF);
			}
			else {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Character "+characterName+" banned "+banTime+" for : "+reason, MessageType.SELF);
			}
			BanMgr.banCharacter(characterId, timer, banTimer+timer, player.getName(), reason);
			Player banned = Server.getInGameCharacter(characterId);
			if(banned == null) {
				return;
			}
			banned.close();
		}
	};
	private final static ChatSubCommand ban_ip = new ChatSubCommand("ip", "ban", "Synthax : .ban ip [ip_adress] [duration] [reason]",  AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 5) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
				return;
			}
			String ipAdress = value[2];
			String banTime = value[3];
			String reason = value[4];
			long banTimer = 0;
			if(!isValidIpAdresse(ipAdress)) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
				return;
			}
			if(Server.isInteger(banTime)) {
				banTimer = Integer.parseInt(banTime);
			}
			else {
				banTimer = convStringTimerToMS(banTime);
				if(banTimer == -666) {
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
					return;
				}
			}
			long timer = System.currentTimeMillis();
			if(banTimer < 0) {
				banTimer = -1-System.currentTimeMillis();
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "IPAdress "+ipAdress+" banned permanently for : "+reason, MessageType.SELF);
			}
			else {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "IPAdress "+ipAdress+" banned "+banTime+" for : "+reason, MessageType.SELF);
			}
			BanMgr.banIPAdress(ipAdress, timer, banTimer+timer, player.getName(), reason);
			ArrayList<Player> bannedList = Server.getAllInGameCharacterByIP('/'+ipAdress);
			if(bannedList == null) {
				return;
			}
			int i = 0;
			while(i < bannedList.size()) {
				bannedList.get(i).close();
				i++;
			}
		}
	};
	private final static ChatCommand help = new ChatCommand("help", "Syntax: .help [command] \n\nDisplay usage instructions for the given command. If no command provided show list of available commands.", AccountRank.PLAYER) {
		
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
					if(value.length > 2 && commandMap.get(value[1]).subCommandList != null) {
						int i = 0;
						while(i < commandMap.get(value[1]).subCommandList.size()) {
							if(commandMap.get(value[1]).subCommandList.get(i).getName().equals(value[2])) {
								if(value.length > 3 && commandMap.get(value[1]).subCommandList.get(i).commandList != null) {
									int j = 0;
									while(j < commandMap.get(value[1]).subCommandList.get(i).commandList.size()) {
										if(commandMap.get(value[1]).subCommandList.get(i).commandList.get(j).getName().equals(value[3])) {
											CommandSendMessage.selfWithoutAuthor(player.getConnection(), commandMap.get(value[1]).subCommandList.get(i).commandList.get(j).helpMessage, MessageType.SELF);
											return;
										}
										j++;
									}
									CommandSendMessage.selfWithoutAuthor(player.getConnection(), "This command doesn't exist, type .help for help.", MessageType.SELF);
									return;
								}
								CommandSendMessage.selfWithoutAuthor(player.getConnection(), commandMap.get(value[1]).subCommandList.get(i).printHelpMessage(), MessageType.SELF);
								return;
							}
							i++;
						}
						CommandSendMessage.selfWithoutAuthor(player.getConnection(), "This command doesn't exist, type .help for help.", MessageType.SELF);
						return;
					}
					if(commandMap.get(value[1]).subCommandList != null) {
						CommandSendMessage.selfWithoutAuthor(player.getConnection(), commandMap.get(value[1]).printSubCommandError(player), MessageType.SELF);
					}
					else {
						CommandSendMessage.selfWithoutAuthor(player.getConnection(), commandMap.get(value[1]).printHelpMessage(), MessageType.SELF);
					}
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
	private final static ChatSubCommand server_exit = new ChatSubCommand("exit", "server", "Syntax: .server exit\n\n  Close the server", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			System.out.println("[SERVER EXIT] requested by "+player.getName());
			Server.close();
		}
	};
	private final static ChatSubCommand server_info = new ChatSubCommand("info", "server", "Syntax: .server info\n\nDisplay multiple server informations.", AccountRank.PLAYER) {
		
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
	private final static ChatSubCommand server_motd = new ChatSubCommand("motd", "server", "Syntax: .server motd\n\n Display the server message of the day.", AccountRank.PLAYER) {
		
		@Override
		public void handle(String[] value, Player player) {
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), Server.getServerMessageOfTheDay(), MessageType.SELF);
		}
	};
	private final static ChatSubCommand server_ram = new ChatSubCommand("ram", "server", "Syntax: .server ram\n\n Dispay the ram used by the server.", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Server is using "+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/(1024f*1024f)+" Mb of ram.", MessageType.SELF);
		}
	};
	private final static ChatSubCommand server_gc = new ChatSubCommand("gc", "server", "Syntax: .server gc\n\n Perform a gc and display the ram used before and after the gc.", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Used ram before gc: "+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/(1024f*1024f)+" Mb", MessageType.SELF);
			System.gc();
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Used ram after gc: "+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/(1024f*1024f)+" Mb", MessageType.SELF);
		}
	};
	private final static ChatSubCommand server_set = new ChatSubCommand("set", "server", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
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
	private final static ChatSubCommand server_set_motd = new ChatSubCommand("motd", "server_set", "Syntax: .server set motd [message]\n\n Set the server message of the day.", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 4) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [motd] in .server set motd [motd]", MessageType.SELF);
				return;
			}
			Server.setServerMessageOfTheDay(value[3]);
		}
	};
	private final static ChatSubCommand server_set_closed = new ChatSubCommand("closed", "server_set", "Syntax: .server set closed [on/off]\n\n Set wether the server should accept connection or not.", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
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
	private final static ChatCommand baninfo = new ChatCommand("baninfo", AccountRank.GAMEMASTER) {
	
		@Override
		public void handle(String command, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
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
	private final static ChatSubCommand baninfo_account = new ChatSubCommand("account", "baninfo", "Syntax : .baninfo account [account_name || account_id]\n\n Display ban informations of the account.", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [account_name || account_id] in .baninfo account [account_name || account_id]", MessageType.SELF);
				return;
			}
			int accountId = 0;
			if(Server.isInteger(value[2])) {
				accountId = Integer.parseInt(value[2]);
			}
			else {
				accountId = AccountMgr.loadAccountIDFromName(value[2]);
			}
			if(accountId == -1) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Account "+value[2].substring(0, 1).toUpperCase()+value[2].substring(1).toLowerCase()+" not found.", MessageType.SELF);
				return;
			}
			try {
				if(getBanInfoAccountName == null) {
					getBanInfoAccountName = Server.getAsyncJDO().prepare("SELECT ban_date, unban_date, banned_by, ban_reason FROM account_banned WHERE account_id = ?");
				}
				getBanInfoAccountName.clear();
				getBanInfoAccountName.putInt(accountId);
				getBanInfoAccountName.execute();
				if(getBanInfoAccountName.fetch()) {
					long ban_date = getBanInfoAccountName.getLong();
					long unban_date = getBanInfoAccountName.getLong();
					String banned_by = getBanInfoAccountName.getString();
					String ban_reason = getBanInfoAccountName.getString();
					StringBuilder builder = new StringBuilder();
					Date banDate = new Date(ban_date);
					if(unban_date == -1) {
						builder.append("Account "+value[2]+" has been permanently banned the "+banDate.toString()+" by "+banned_by+" for :\n"+ban_reason);
					}
					else {
						Date unbanDate = new Date(unban_date);
						builder.append("Account "+value[2]+" has been banned the "+banDate.toString()+" by "+banned_by+" for :\n"+ban_reason+"\nBan will expire the "+unbanDate.toString());
					}
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), builder.toString(), MessageType.SELF);
				}
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
	private final static ChatSubCommand baninfo_character = new ChatSubCommand("character", "baninfo", "Syntax: .baninfo character [character_name || character_id]\n\n Display ban informations of the character.", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [character_name || character_id] in .baninfo character [character_name || character_id]", MessageType.SELF);
				return;
			}
			int characterId = 0;
			if(Server.isInteger(value[2])) {
				characterId = Integer.parseInt(value[2]);
			}
			else {
				characterId = CharacterMgr.loadCharacterIDFromName(value[2]);
			}
			if(characterId == -1) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Character "+value[2]+" not found.", MessageType.SELF);
				return;
			}
			try {
				if(getBanInfoCharacterName == null) {
					getBanInfoCharacterName = Server.getAsyncJDO().prepare("SELECT ban_date, unban_date, banned_by, ban_reason FROM character_banned WHERE character_id = ?");
				}
				getBanInfoCharacterName.clear();
				getBanInfoCharacterName.putInt(characterId);
				getBanInfoCharacterName.execute();
				if(getBanInfoCharacterName.fetch()) {
					long ban_date = getBanInfoCharacterName.getLong();
					long unban_date = getBanInfoCharacterName.getLong();
					String banned_by = getBanInfoCharacterName.getString();
					String ban_reason = getBanInfoCharacterName.getString();
					StringBuilder builder = new StringBuilder();
					Date banDate = new Date(ban_date);
					if(unban_date == -1) {
						builder.append("Character "+value[2]+" has been permanently banned the "+banDate.toString()+" by "+banned_by+" for :\n"+ban_reason);
					}
					else {
						Date unbanDate = new Date(unban_date);
						builder.append("Character "+value[2]+" has been banned the "+banDate.toString()+" by "+banned_by+" for :\n"+ban_reason+"\nBan will expire the "+unbanDate.toString());
					}
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), builder.toString(), MessageType.SELF);
				}
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
	private final static ChatSubCommand baninfo_ip = new ChatSubCommand("ip", "baninfo", "Syntax: .baninfo ip [ip_adress]\n\n Display informations of the ip", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [ip_adress] in .baninfo ip [ip_adress]", MessageType.SELF);
				return;
			}
			if(!isValidIpAdresse(value[2])) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [ip_adress] in .baninfo ip [ip_adress]", MessageType.SELF);
				return;
			}
			try {
				if(getBanInfoCharacterName == null) {
					getBanInfoCharacterName = Server.getAsyncJDO().prepare("SELECT ban_date, unban_date, banned_by, ban_reason FROM ip_banned WHERE ip_adress = ?");
				}
				getBanInfoIPAdress.clear();
				getBanInfoIPAdress.putString(value[2]);
				getBanInfoIPAdress.execute();
				if(getBanInfoIPAdress.fetch()) {
					long ban_date = getBanInfoIPAdress.getLong();
					long unban_date = getBanInfoIPAdress.getLong();
					String banned_by = getBanInfoIPAdress.getString();
					String ban_reason = getBanInfoIPAdress.getString();
					StringBuilder builder = new StringBuilder();
					Date banDate = new Date(ban_date);
					if(unban_date == -1) {
						builder.append("Ip adress "+value[2]+" has been permanently banned the "+banDate.toString()+" by "+banned_by+" for :\n"+ban_reason);
					}
					else {
						Date unbanDate = new Date(unban_date);
						builder.append("Ip adress "+value[2]+" has been banned the "+banDate.toString()+" by "+banned_by+" for :\n"+ban_reason+"\nBan will expire the "+unbanDate.toString());
					}
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), builder.toString(), MessageType.SELF);
				}
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
	private final static ChatCommand banlist = new ChatCommand("banlist", AccountRank.GAMEMASTER) {
	
		@Override
		public void handle(String command, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
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
	private final static ChatSubCommand banlist_account = new ChatSubCommand("account", "banlist", "Syntax: .banlist account || .banlist account [pattern]\n\n Display the account banlist for the given pattern.", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			try {
				if(value.length < 3) {
					if(getBanListAccount == null) {
						getBanListAccount = Server.getAsyncJDO().prepare("SELECT account_id FROM account_banned");
					}
					StringBuilder builder = new StringBuilder();
					builder.append("List of banned accounts:");
					getBanListAccount.clear();
					getBanListAccount.execute();
					while(getBanListAccount.fetch()) {
						String name = AccountMgr.loadAccountNameFromID(getBanListAccount.getInt());
						if(name != null) {
							builder.append("\n    "+name);
						}
					}
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), builder.toString(), MessageType.SELF);
				}
				else {
					if(getBanListAccountPattern == null) {
						getBanListAccountPattern = Server.getAsyncJDO().prepare("SELECT COUNT(account_id) FROM account_banned WHERE account_id = ?");
					}
					ArrayList<SQLDatas> accountIDList = AccountMgr.loadAccountIDAndNameFromNamePattern(value[2]);
					if(accountIDList == null || accountIDList.size() == 0) {
						CommandSendMessage.selfWithoutAuthor(player.getConnection(), "No account match "+value[2], MessageType.SELF);
						return;
					}
					StringBuilder builder = new StringBuilder();
					builder.append("List of banned account matching "+value[2]+':');
					int i = 0;
					while(i < accountIDList.size()) {
						getBanListAccountPattern.clear();
						getBanListAccountPattern.putInt(accountIDList.get(i).getIValue1());
						getBanListAccountPattern.execute();
						if(getBanListAccountPattern.fetch()) {
							int number = getBanListAccountPattern.getInt();
							if(number > 0) {
								builder.append("\n    "+accountIDList.get(i).getStringValue1());
							}
						}
						i++;
					}
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), builder.toString(), MessageType.SELF);
				}
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
	private final static ChatSubCommand banlist_character = new ChatSubCommand("character", "banlist", "Syntax: .banlist character [pattern]\n\n Display the character banlist for the given pattern.", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [pattern] in .banlist character [pattern]", MessageType.SELF);
				return;
			}
			try {
				if(getBanListCharacterPattern == null) {
					getBanListCharacterPattern = Server.getAsyncJDO().prepare("SELECT COUNT(character_id) FROM character_banned WHERE character_id = ?");
				}
				ArrayList<SQLDatas> characterIDList = CharacterMgr.loadCharacterIDAndNameFromNamePattern(value[2]);
				if(characterIDList == null || characterIDList.size() == 0) {
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), "No character match "+value[2], MessageType.SELF);
					return;
				}
				StringBuilder builder = new StringBuilder();
				builder.append("List of banned character matching "+value[2]+':');
				int i = 0;
				while(i < characterIDList.size()) {
					getBanListCharacterPattern.clear();
					getBanListCharacterPattern.putInt(characterIDList.get(i).getIValue1());
					getBanListCharacterPattern.execute();
					if(getBanListCharacterPattern.fetch()) {
						if(getBanListCharacterPattern.getInt() > 0) {
							builder.append("\n    "+characterIDList.get(i).getStringValue1());
						}
					}
					i++;
				}
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), builder.toString(), MessageType.SELF);
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
	private final static ChatCommand character = new ChatCommand("character", AccountRank.GAMEMASTER) {
	
		@Override
		public void handle(String command, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
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
	private final static ChatSubCommand character_level = new ChatSubCommand("level", "character", "Syntax: .character level", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect synthax for .character level [character_name] [level]", MessageType.SELF);
				return;
			}
			Player target = null;
			if(value.length < 4) {
				if(Server.isInteger(value[2])) {
					player.setLevel(Integer.parseInt(value[2]));
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), "You are now level "+player.getLevel(), MessageType.SELF);
				}
				else {
					target = Server.getInGameCharacterByName(value[2]);
					if(target != null) {
						target.setLevel(target.getLevel()+1);
						CharacterMgr.setExperience(target.getCharacterId(), Player.getExpNeeded(target.getLevel()));
						CommandSendMessage.selfWithoutAuthor(player.getConnection(), target.getName()+" is now level "+target.getLevel(), MessageType.SELF);
					}
					else {
						int id = CharacterMgr.loadCharacterIDFromName(value[2]);
						if(id == -1) {
							return;
						}
						int level = Player.getLevel(CharacterMgr.getExperience(id));
						CharacterMgr.setExperience(id, Player.getExpNeeded(level+1));
						CommandSendMessage.selfWithoutAuthor(player.getConnection(), value[2]+" is now level "+level+1, MessageType.SELF);
					}
				}
			}
			else if(value.length == 4) {
				if(!Server.isInteger(value[3])) {
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [level] in .character level [character_name] [level]", MessageType.SELF);
					return;
				}
				target = Server.getInGameCharacterByName(value[2]);
				int level = Integer.parseInt(value[3]);
				if(target != null) {
					target.setLevel(level);
					CharacterMgr.setExperience(target.getCharacterId(), Player.getExpNeeded(target.getLevel()));
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), target.getName()+" is now level "+level, MessageType.SELF);
				}
				else {
					int id = CharacterMgr.loadCharacterIDFromName(value[2]);
					if(id == -1) {
						return;
					}
					CharacterMgr.setExperience(id, Player.getExpNeeded(level));
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), value[2]+" is now level "+level, MessageType.SELF);
				}
			}
		}
	};
	private final static ChatSubCommand character_erase = new ChatSubCommand("erase", "character", ".character erase [name]\n\n Erase the character.", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect synthax for .character erase [character_name]", MessageType.SELF);
				return;
			}
			Player target = Server.getInGameCharacterByName(value[2]);
			if(target != null) {
				target.close();
			}
			CharacterMgr.deleteCharacterByName(value[2]);
		}
	};
	private final static ChatCommand debug = new ChatCommand("debug", AccountRank.ADMINISTRATOR) {
	
		@Override
		public void handle(String command, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
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
	private final static ChatSubCommand debug_looptoolongtimer = new ChatSubCommand("looptoolongtimer", "debug", ".debug looptoolongtimer [timer]\n\n Set the value of looptoolong print.", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect synthax for .debug looptoolongtimer [value]", MessageType.SELF);
				return;
			}
			if(!Server.isInteger(value[2])) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for .debug looptoolongtimer [value]", MessageType.SELF);
				return;
			}
			DebugMgr.setLoopTooLongValue(Integer.parseInt(value[2]));
		}
	};
	private final static ChatSubCommand debug_printsqltimer = new ChatSubCommand("printsqltimer", "debug", ".debug printsqltimer [true || false]\n\n Set wether the time to execute the request should be printed.", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect synthax for .debug printsqltimer [true || false]", MessageType.SELF);
				return;
			}
			if(!value[2].equals("true") && !value[2].equals("false")) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for .debug printsqltimer [true || false]", MessageType.SELF);
				return;
			}
			boolean b = value[2].equals("true") ? true : false;
			DebugMgr.setSQLRequestTimer(b);
		}
	};
	private final static ChatSubCommand debug_printlogfiletimer = new ChatSubCommand("printlogfiletimer", "debug", ".debug printlogfiletimer [true || false]\n\n Set wether the time to write in the log file should be printed.", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect synthax for .debug printlogfiletimer [true || false]", MessageType.SELF);
				return;
			}
			if(!value[2].equals("true") && !value[2].equals("false")) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for .debug printlogfiletimer [true || false]", MessageType.SELF);
				return;
			}
			boolean b = value[2].equals("true") ? true : false;
			DebugMgr.setWriteLogFileTimer(b);
		}
	};
	private final static ChatSubCommand debug_chatcommandtimer = new ChatSubCommand("chatcommandtimer", "debug", ".debug chatcommandtimer [true || false]\n\n Set wether the time to execute the chat command should be printed.", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect synthax for .debug chatcommandtimer [true || false]", MessageType.SELF);
				return;
			}
			if(!value[2].equals("true") && !value[2].equals("false")) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for .debug chatcommandtimer [true || false]", MessageType.SELF);
				return;
			}
			boolean b = value[2].equals("true") ? true : false;
			DebugMgr.setChatCommandTimer(b);
		}
	};
	private final static ChatCommand additem = new ChatCommand("additem", "List of possible syntax: \n  .additem [item_id || item_name] to the item to yourself.\n  .additem [item_id || item_name] [character_name]\n  .additem [item_id || item_name] [amount] [character_id || character_name]", AccountRank.GAMEMASTER) {
	
		@Override
		public void handle(String command, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			command = command.trim().toLowerCase();
			if(command.equals('.'+this.name)) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect syntax, list of possible syntax:"
								+ "\n  .additem [item_id || item_name] to the item to yourself. "
								+ "\n  .additem [item_id || item_name] [character_name]"
								+ "\n  .additem [item_id || item_name] [amount]"
								+ "\n  .additem [item_id || item_name] [amount] [character_id || character_name]", MessageType.SELF);
			}
			else {
				String[] value = command.split(" ");
				if(value.length < 2) {
					return;
				}
				if(value.length == 2) {
					if(Server.isInteger(value[1])) {
						Item item = Item.getItem(Integer.parseInt(value[1]));
						if(item == null) {
							CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Item not found.", MessageType.SELF);
							return;
						}
						player.addItem(item, 1);
					}
					else {
						//TODO: find item by name efficency
					}
				}
				else if(value.length == 3) {
					Player playerToAdd = player;
					int amount = 1;
					if(!Server.isInteger(value[2])) {
						playerToAdd = Server.getCharacter(value[2]);
						if(playerToAdd == null) {
							CommandPlayerNotFound.write(player.getConnection(), value[2].substring(0, 1).toUpperCase()+value[2].substring(1));
							return;
						}
					}
					else {
						amount = Integer.parseInt(value[2]);
					}
					if(Server.isInteger(value[1])) {
						Item item = Item.getItem(Integer.parseInt(value[1]));
						if(item == null) {
							CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Item not found.", MessageType.SELF);
							return;
						}
						player.addItem(item, amount);
					}
					else {
						//TODO: find item by name efficency
					}
				}
				else if(value.length == 4) {
					if(!Server.isInteger(value[2])) {
						CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [amount] in .additem [item_id || item_name] [amount] [character_id || character_name]", MessageType.SELF);
						return;
					}
					Item item = null;
					if(Server.isInteger(value[1])) {
						item = Item.getItem(Integer.parseInt(value[1]));
					}
					else {
						//TODO: find item by name efficency
					}
					if(item == null) {
						CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Item not found.", MessageType.SELF);
						return;
					}
					Player playerToAdd = null;
					if(Server.isInteger(value[3])) {
						playerToAdd = Server.getCharacter(Integer.parseInt(value[3]));
					}
					else {
						playerToAdd = Server.getCharacter(value[3]);
					}
					if(playerToAdd == null) {
						CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Player not found.", MessageType.SELF);
						return;
					}
					playerToAdd.addItem(item, Integer.parseInt(value[2]));
				}
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
		server.addSubCommand(server_ram);
		server.addSubCommand(server_gc);
		server_set.addSubCommand(server_set_motd);
		server_set.addSubCommand(server_set_closed);
		commandMap.put(server.getName(), server);
		baninfo.addSubCommand(baninfo_account);
		baninfo.addSubCommand(baninfo_character);
		baninfo.addSubCommand(baninfo_ip);
		commandMap.put(baninfo.getName(), baninfo);
		banlist.addSubCommand(banlist_account);
		banlist.addSubCommand(banlist_character);
		commandMap.put(banlist.getName(), banlist);
		character.addSubCommand(character_level);
		character.addSubCommand(character_erase);
		commandMap.put(character.getName(), character);
		debug.addSubCommand(debug_looptoolongtimer);
		debug.addSubCommand(debug_printsqltimer);
		debug.addSubCommand(debug_printlogfiletimer);
		debug.addSubCommand(debug_chatcommandtimer);
		commandMap.put(debug.getName(), debug);
		commandMap.put(additem.getName(), additem);
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
	
	static boolean checkRank(Player player, AccountRank rank) {
		if(player.getAccountRank().getValue() < rank.getValue()) {
			CommandDefaultMessage.write(player, DefaultMessage.NOT_ENOUGH_RIGHT);
			return false;
		}
		return true;
	}
	
	static boolean isValidIpAdresse(String ip) {
		String[] value = ip.split(".");
		if(value.length != 4) {
			return false;
		}
		int i = 0;
		while(i < value.length) {
			if(!Server.isInteger(value[i])) {
				return false;
			}
			int number = Integer.parseInt(value[i]);
			if(number < 0 && number > 255) {
				return false;
			}
			i++;
		}
		return true;
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

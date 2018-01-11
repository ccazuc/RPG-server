package net.game.chat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

import jdo.JDOStatement;
import net.Server;
import net.command.chat.CommandDefaultMessage;
import net.command.chat.CommandPlayerNotFound;
import net.command.chat.CommandSendMessage;
import net.command.chat.DefaultMessage;
import net.command.chat.MessageColor;
import net.command.chat.MessageType;
import net.command.player.CommandSendRedAlert;
import net.config.ConfigMgr;
import net.game.AccountRank;
import net.game.item.Item;
import net.game.manager.AccountMgr;
import net.game.manager.BanMgr;
import net.game.manager.CharacterMgr;
import net.game.manager.DebugMgr;
import net.game.spell.SpellMgr;
import net.game.unit.Player;
import net.thread.sql.SQLDatas;
import net.utils.Color;
import net.utils.StringUtils;

public class StoreChatCommand {
	
	private final static long MS_IN_A_YEAR = 31536000000l;
	private final static long MS_IN_A_MONTH = 1036800000l;
	private final static long MS_IN_A_WEEK = 604800000l;
	private final static long MS_IN_A_DAY = 86400000l;
	private final static long MS_IN_AN_HOUR = 3600000l;
	private final static long MS_IN_A_MINUTE = 60000l;
	static JDOStatement getBanListAccountPattern;
	static JDOStatement getBanListCharacterPattern;
	final static StringBuilder builder = new StringBuilder();
	private final static Pattern isValidIP = Pattern.compile("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

	final static HashMap<String, ChatCommand> commandMap = new HashMap<String, ChatCommand>();
	
	private final static ChatCommand account = new ChatCommand("account", AccountRank.PLAYER) {
		
		@Override
		public void handle(String command, Player player) {
			command = command.trim();
			if(command.equalsIgnoreCase('.'+this.name)) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Account level : ".concat(String.valueOf(player.getAccountRank().getValue())), MessageType.SELF);
			}
			else {
				String[] value = command.split(" ");
				if(value.length < 2) {
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
					return;
				}
				int i = 0;
				while(i < this.subCommandList.size()) {
					if(this.subCommandList.get(i).getName().equalsIgnoreCase(value[1])) {
						this.subCommandList.get(i).handle(value, player);
						return;
					}
					i++;
				}
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
			}
		}
	};
	private final static ChatSubCommand account_onlinelist = new ChatSubCommand("onlinelist", "account", "Syntax: .account onlinelist \n\nList all online accounts.", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			builder.setLength(0);
			for(Player players : Server.getInGamePlayerList().values())
				builder.append("Name: ").append(players.getAccountName()).append(", ID: ").append(player.getAccountId()).append(", Rank: ").append(players.getAccountRank().getValue());
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
				if(this.commandList.get(i).getName().equalsIgnoreCase(value[2])) {
					this.commandList.get(i).handle(value, player);
					return;
				}
				i++;
			}
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
		}
	};
	private final static ChatSubCommand account_set_gmlevel = new ChatSubCommand("gmlevel", "account_set", "Syntax : .account set gmlevel [account_name] [account_level]", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 5) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
				return;
			}
			if(StringUtils.isInteger(value[4])) {
				String accountName = value[3];
				int level = Integer.parseInt(value[4]);
				if(level < 1 || level > AccountRank.values().length) {
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
					return;
				}
				int accountId = AccountMgr.loadAccountIDFromName(accountName);
				if(accountId == -1) {
					builder.setLength(0);
					builder.append("Account ").append(accountName).append(" not found.");
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), builder.toString(), MessageType.SELF);
					return;
				}
				AccountMgr.updateAccountRank(accountId, level);
				Player tmp = Server.getInGameCharacterByAccount(accountId);
				if(tmp == null) {
					return;
				}
				tmp.setAccountRank(AccountRank.getRank(level));
			}
			else {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
			}
		}
	};
	private final static ChatCommand announce = new ChatCommand("announce", "Synthax : .announce [message]\n\nSend an announce to all players", AccountRank.PLAYER) {
		
		@Override
		public void handle(String command, Player player) {
			command = command.trim();
			if(command.equalsIgnoreCase('.'+this.name)) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
				return;
			}
			String display = command.substring(this.name.length()+1);
			for(Player players : Server.getInGamePlayerList().values()) {
				CommandSendMessage.selfWithoutAuthor(players.getConnection(), display, MessageType.ANNOUNCE, new Color(0/255f, 208/255f, 225/255f));
			}
		}
	};
	private final static ChatCommand unban = new ChatCommand("unban", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String command, Player player) {
			command = command.trim();
			String[] value = command.split(" ");
			if (value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
				return;
			}
			int i = -1;
			while (++i < this.subCommandList.size()) {
				if (this.subCommandList.get(i).getName().equalsIgnoreCase(value[1])) {
					this.subCommandList.get(i).handle(value, player);
					return;
				}
			}
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
		}
	};
	private final static ChatSubCommand unban_account = new ChatSubCommand("account", "unban", "Syntax : .unban account [account_name || account_id]", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if (!checkRank(player, this.rank))
				return;
			if (value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
				return;
			}
			String accountInfo = value[2];
			int accountId;
			if (StringUtils.isInteger(accountInfo))
				accountId = Integer.parseInt(accountInfo);
			else
				accountId = AccountMgr.loadAccountIDFromName(accountInfo);
			if (accountId == -1 && !BanMgr.isAccountBanned(accountId)) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(),"Account " + accountInfo + " not found or not banned.", MessageType.SELF);
				return;
			}
			BanMgr.unbanAccount(accountId);
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Account " + accountId + " unbanned.", MessageType.SELF);
		}
	};
	private final static ChatSubCommand unban_character = new ChatSubCommand("character", "unban", "Syntax : .unban character [character_name || character_id]", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if (!checkRank(player, this.rank))
				return;
			if (value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
				return;
			}
			String characterInfo = value[2];
			int characterId;
			if (StringUtils.isInteger(characterInfo))
				characterId = Integer.parseInt(characterInfo);
			else
				characterId = CharacterMgr.loadCharacterIDFromName(characterInfo);
			if (characterId == -1 && !BanMgr.isCharacterBanned(characterId)) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(),"Character " + characterInfo + " not found or not banned.", MessageType.SELF);
				return;
			}
			BanMgr.unbanCharacter(characterId);
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Character " + characterId + " unbanned.", MessageType.SELF);
		}
	};
	private final static ChatCommand ban = new ChatCommand("ban", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String command, Player player) {
			command = command.trim();
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
				if(this.subCommandList.get(i).getName().equalsIgnoreCase(value[1])) {
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
			if(!checkRank(player, this.rank))
				return;
			if(value.length < 5) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
				return;
			}
			String accountName = value[2];
			String banTime = value[3];
			String reason = value[4];
			long banTimer = 0;
			if(StringUtils.isInteger(banTime))
				banTimer = Integer.parseInt(banTime);
			else {
				banTimer = convStringTimerToMS(banTime);
				if(banTimer == -666) {
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
					return;
				}
			}
			int accountId = AccountMgr.loadAccountIDFromName(accountName);
			if(accountId == -1) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), new StringBuilder().append("Account ").append(accountName).append(" not found.").toString(), MessageType.SELF);
				return;
			}
			long timer = System.currentTimeMillis();
			builder.setLength(0);
			int i = 4;
			while(i < value.length) {
				builder.append(value[i]).append(' ');
				i++;
			}
			reason = builder.toString();
			if(banTimer <= 0) {
				banTimer = -timer;
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), new StringBuilder().append("Account ").append(accountId).append(" banned permanently for: ").append(reason).toString(), MessageType.SELF);
			}
			else
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), new StringBuilder().append("Account ").append(accountId).append(" banned ").append(banTime).append(" for: ").append(reason).toString(), MessageType.SELF);
			BanMgr.banAccount(accountId, timer, banTimer+timer, player.getName(), reason);
			Player banned = Server.getInGameCharacterByAccount(accountId);
			if(banned != null)
				banned.close();
		}
	};
	private final static ChatSubCommand ban_character = new ChatSubCommand("character", "ban", "Synthax : .ban character [character_name] [duration] [reason] (duration <= 0 = permanent)", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank))
				return;
			if(value.length < 5) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
				return;
			}
			String characterName = value[2];
			String banTime = value[3];
			String reason = value[4];
			long banTimer = 0;
			if(StringUtils.isInteger(banTime))
				banTimer = Integer.parseInt(banTime);
			else {
				banTimer = convStringTimerToMS(banTime);
				if(banTimer == -666) {
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
					return;
				}
			}
			int characterId = CharacterMgr.loadCharacterIDFromName(characterName);
			if(characterId == -1) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), new StringBuilder().append("Character ").append(characterName).append(" not found.").toString(), MessageType.SELF);
				return;
			}
			builder.setLength(0);
			int i = 4;
			while(i < value.length) {
				builder.append(value[i]).append(' ');
				i++;
			}
			reason = builder.toString();
			long timer = System.currentTimeMillis();
			if(banTimer <= 0) {
				banTimer = -timer; //that way banTimer + timer == 0
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), new StringBuilder().append("Character ").append(characterName).append(" banned permanently for: ").append(reason).toString(), MessageType.SELF);
			}
			else
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), new StringBuilder().append("Character ").append(characterName).append(" banned ").append(banTime).append(" for: ").append(reason).toString(), MessageType.SELF);
			BanMgr.banCharacter(characterId, timer, banTimer+timer, player.getName(), reason);
			Player banned = Server.getInGameCharacter(characterId);
			if(banned != null)
				banned.close();
		}
	};
	private final static ChatSubCommand ban_ip = new ChatSubCommand("ip", "ban", "Synthax : .ban ip [ip_adress] [duration] [reason]",  AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank))
				return;
			if(value.length < 5) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
				return;
			}
			String ipAdress = value[2];
			String banTime = value[3];
			String reason = value[4];
			long banTimer = 0;
			if(!isValidIpAdress(ipAdress)) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
				return;
			}
			if(StringUtils.isInteger(banTime))
				banTimer = Integer.parseInt(banTime);
			else {
				banTimer = convStringTimerToMS(banTime);
				if(banTimer == -666) {
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
					return;
				}
			}
			long timer = System.currentTimeMillis();
			builder.setLength(0);
			int i = 4;
			while(i < value.length) {
				builder.append(value[i]).append(' ');
				i++;
			}
			reason = builder.toString();
			if(banTimer <= 0) {
				banTimer = -timer;
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), new StringBuilder().append("IPAdress ").append(ipAdress).append(" banned permanently for: ").append(reason).toString(), MessageType.SELF);
			}
			else {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), new StringBuilder().append("IPAdress ").append(ipAdress).append(" banned ").append(banTime).append(" for: ").append(reason).toString(), MessageType.SELF);
			}
			BanMgr.banIPAdress(ipAdress, timer, banTimer+timer, player.getName(), reason);
			ArrayList<Player> bannedList = Server.getAllInGameCharacterByIP('/'+ipAdress);
			if(bannedList == null)
				return;
			i = -1;
			while(++i < bannedList.size())
				bannedList.get(i).close();
		}
	};
	private final static ChatCommand help = new ChatCommand("help", "Syntax: .help [command] \n\nDisplay usage instructions for the given command. If no command provided show list of available commands.", AccountRank.PLAYER) {
		
		@Override
		public void handle(String command, Player player) {
			command = command.trim();
			if(command.equalsIgnoreCase('.'+this.name)) {
				builder.setLength(0);
				builder.append("Available commands:");
				for(ChatCommand chatCommand : commandMap.values()) {
					if(player.getAccountRank().superiorOrEqualsTo(chatCommand.getRank())) {
						builder.append("\n").append(chatCommand.getName());
					}
				}
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), builder.toString(), MessageType.SELF);
			}
			else {
				String[] value = command.split(" ");
				if(value.length < 2) {
					return;
				}
				if (!commandMap.containsKey(value[1]))
				{
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), "This command doesn't exist, type .help for help.", MessageType.SELF);
					return;
				}
				if(value.length > 2 && commandMap.get(value[1]).subCommandList != null) {
					int i = -1;
					while(++i < commandMap.get(value[1]).subCommandList.size())
						if(commandMap.get(value[1]).subCommandList.get(i).getName().equalsIgnoreCase(value[2])) {
							if(value.length > 3 && commandMap.get(value[1]).subCommandList.get(i).commandList != null) {
								int j = 0;
								while(j < commandMap.get(value[1]).subCommandList.get(i).commandList.size()) {
									if(commandMap.get(value[1]).subCommandList.get(i).commandList.get(j).getName().equalsIgnoreCase(value[3])) {
										CommandSendMessage.selfWithoutAuthor(player.getConnection(), commandMap.get(value[1]).subCommandList.get(i).commandList.get(j).helpMessage, MessageType.SELF);
										return;
									}
									j++;
								}
								CommandSendMessage.selfWithoutAuthor(player.getConnection(), "This command doesn't exist, type .help for help.", MessageType.SELF);
								return;
							}
							CommandSendMessage.selfWithoutAuthor(player.getConnection(), commandMap.get(value[1]).subCommandList.get(i).printHelpMessage(player), MessageType.SELF);
							return;
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
	};
	private final static ChatCommand server = new ChatCommand("server", AccountRank.PLAYER) {
		
		@Override
		public void handle(String command, Player player) {
			command = command.trim();
			if(command.equalsIgnoreCase('.'+this.name)) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
			}
			else {
				String[] value = command.split(" ");
				if(value.length < 2) {
					return;
				}
				int i = 0;
				while(i < this.subCommandList.size()) {
					if(this.subCommandList.get(i).getName().equalsIgnoreCase(value[1])) {
						this.subCommandList.get(i).handle(value, player);
						return;
					}
					i++;
				}
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
			}
		}
	};
	private final static ChatSubCommand server_exit = new ChatSubCommand("exit", "server", "Syntax: .server exit\n\nCloses the server.", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			System.out.println("[SERVER EXIT] requested by ".concat(player.getName()));
			Server.close();
		}
	};
	private final static ChatSubCommand server_info = new ChatSubCommand("info", "server", "Syntax: .server info\n\nDisplay multiple server informations.", AccountRank.PLAYER) {
		
		@Override
		public void handle(String[] value, Player player) {
			builder.setLength(0);
			builder.append("Server informations :\n");
			builder.append("Server message of the day :\n").append(ConfigMgr.getServerMessageOfTheDay()).append('\n');
			builder.append("Online since ").append(convMillisToDate(Server.getLoopTickTimer()-Server.getServerStartTimer())).append('\n');
			builder.append("Online player(s) : ").append(Server.getInGamePlayerList().size());
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), builder.toString(), MessageType.SELF);
		}
	};
	private final static ChatSubCommand server_motd = new ChatSubCommand("motd", "server", "Syntax: .server motd\n\nDisplay the server message of the day.", AccountRank.PLAYER) {
		
		@Override
		public void handle(String[] value, Player player) {
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), ConfigMgr.getServerMessageOfTheDay(), MessageType.SELF);
		}
	};
	private final static ChatSubCommand server_ram = new ChatSubCommand("ram", "server", "Syntax: .server ram\n\nDispay the ram used by the server.", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), new StringBuilder().append("Server is using ").append((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/(1024f*1024f)).append(" Mb of ram.").toString(), MessageType.SELF);
		}
	};
	private final static ChatSubCommand server_gc = new ChatSubCommand("gc", "server", "Syntax: .server gc\n\nPerform a gc and display the ram used before and after the gc.", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), new StringBuilder().append("Used ram before gc: ").append((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/(1024f*1024f)).append(" Mb").toString(), MessageType.SELF);
			System.gc();
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), new StringBuilder().append("Used ram after gc: ").append((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/(1024f*1024f)).append(" Mb").toString(), MessageType.SELF);
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
				if(this.commandList.get(i).getName().equalsIgnoreCase(value[2])) {
					this.commandList.get(i).handle(value, player);
					return;
				}
				i++;
			}
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
		}
	};
	private final static ChatSubCommand server_set_motd = new ChatSubCommand("motd", "server_set", "Syntax: .server set motd [message]\n\nSet the server message of the day.", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 4) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [motd] in .server set motd [motd]", MessageType.SELF);
				return;
			}
			ConfigMgr.setServerMessageOfTheDay(value[3]);
		}
	};
	private final static ChatSubCommand server_set_closed = new ChatSubCommand("closed", "server_set", "Syntax: .server set closed [on/off]\n\nSet wether the server should accept connection or not.", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 4) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [closed] in .server set closed [on/off]", MessageType.SELF);
				return;
			}
			if(value[3].equalsIgnoreCase("on")) {
				Server.setIsAcceptingConnection(false);
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "The server is now blocking connection.", MessageType.SELF);
			}
			else if(value[3].equalsIgnoreCase("off")) {
				Server.setIsAcceptingConnection(true);
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "The server is now accepting connection.", MessageType.SELF);
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
			command = command.trim();
			if(command.equalsIgnoreCase('.'+this.name)) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
			}
			else {
				String[] value = command.split(" ");
				if(value.length < 2) {
					return;
				}
				int i = 0;
				while(i < this.subCommandList.size()) {
					if(this.subCommandList.get(i).getName().equalsIgnoreCase(value[1])) {
						this.subCommandList.get(i).handle(value, player);
						return;
					}
					i++;
				}
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
			}
		}
	};
	private final static ChatSubCommand baninfo_account = new ChatSubCommand("account", "baninfo", "Syntax : .baninfo account [account_name || account_id]\n\nDisplay ban informations of the account.", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [account_name || account_id] in .baninfo account [account_name || account_id]", MessageType.SELF);
				return;
			}
			int accountID = 0;
			if(StringUtils.isInteger(value[2])) {
				accountID = Integer.parseInt(value[2]);
			}
			else {
				accountID = AccountMgr.loadAccountIDFromName(value[2]);
			}
			if(accountID == -1) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Account "+value[2].substring(0, 1).toUpperCase()+value[2].substring(1).toLowerCase()+" not found.", MessageType.SELF);
				return;
			}
			SQLDatas datas = BanMgr.getBanInfoAccountIDLowAsync(accountID);
			if(datas == null) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "There is no account banned with this id.", MessageType.SELF);
				return;
			}
			long ban_date = (long)datas.getNextObject();
			long unban_date = (long)datas.getNextObject();
			String banned_by = (String)datas.getNextObject();
			String ban_reason = (String)datas.getNextObject();
			builder.setLength(0);
			Date banDate = new Date(ban_date);
			if(unban_date <= 0) {
				builder.append("Account ").append(value[2]).append(" has been permanently banned the ").append(banDate.toString()).append(" by ").append(banned_by).append(" for :\n").append(ban_reason);
			}
			else {
				Date unbanDate = new Date(unban_date);
				builder.append("Account ").append(value[2]).append(" has been banned the ").append(banDate.toString()).append(" by ").append(banned_by).append(" for :\n").append(ban_reason).append("\nBan will expire the ").append(unbanDate.toString());
			}
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), builder.toString(), MessageType.SELF);
		}
	};
	private final static ChatSubCommand baninfo_character = new ChatSubCommand("character", "baninfo", "Syntax: .baninfo character [character_name || character_id]\n\nDisplay ban informations of the character.", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [character_name || character_id] in .baninfo character [character_name || character_id]", MessageType.SELF);
				return;
			}
			int characterID = 0;
			if(StringUtils.isInteger(value[2])) {
				characterID = Integer.parseInt(value[2]);
			}
			else {
				characterID = CharacterMgr.loadCharacterIDFromName(value[2]);
			}
			if(characterID == -1) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), new StringBuilder().append("Character ").append(value[2]).append(" not found.").toString(), MessageType.SELF);
				return;
			}
			SQLDatas datas = BanMgr.getBanInfoCharacterIDLowAsync(characterID);
			if(datas == null) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "There is no character banned with this id.", MessageType.SELF);
				return;
			}
			long ban_date = (long)datas.getNextObject();
			long unban_date = (long)datas.getNextObject();
			String banned_by = (String)datas.getNextObject();
			String ban_reason = (String)datas.getNextObject();
			builder.setLength(0);
			Date banDate = new Date(ban_date);
			if(unban_date <= 0) {
				builder.append("Character ").append(value[2]).append(" has been permanently banned the ").append(banDate.toString()).append(" by ").append(banned_by).append(" for :\n").append(ban_reason);
			}
			else {
				Date unbanDate = new Date(unban_date);
				builder.append("Character ").append(value[2]).append(" has been banned the ").append(banDate.toString()).append(" by ").append(banned_by).append(" for :\n").append(ban_reason).append("\nBan will expire the ").append(unbanDate.toString());
			}
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), builder.toString(), MessageType.SELF);
		}
	};
	private final static ChatSubCommand baninfo_ip = new ChatSubCommand("ip", "baninfo", "Syntax: .baninfo ip [ip_adress]\n\nDisplay informations of the ip", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [ip_adress] in .baninfo ip [ip_adress]", MessageType.SELF);
				return;
			}
			if(!isValidIpAdress(value[2])) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [ip_adress] in .baninfo ip [ip_adress]", MessageType.SELF);
				return;
			}
			SQLDatas datas = BanMgr.getBanInfoIPAdressLowAsync(value[2]);
			if(datas == null) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "There is no character banned with this id.", MessageType.SELF);
				return;
			}
			long ban_date = (long)datas.getNextObject();
			long unban_date = (long)datas.getNextObject();
			String banned_by = (String)datas.getNextObject();
			String ban_reason = (String)datas.getNextObject();
			builder.setLength(0);
			Date banDate = new Date(ban_date);
			if(unban_date <= 0) {
				builder.append("Ip adress ").append(value[2]).append(" has been permanently banned the ").append(banDate.toString()).append(" by ").append(banned_by).append(" for :\n").append(ban_reason);
			}
			else {
				Date unbanDate = new Date(unban_date);
				builder.append("Ip adress ").append(value[2]).append(" has been banned the ").append(banDate.toString()).append(" by ").append(banned_by).append(" for :\n").append(ban_reason).append("\nBan will expire the ").append(unbanDate.toString());
			}
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), builder.toString(), MessageType.SELF);
		}
	};
	private final static ChatCommand banlist = new ChatCommand("banlist", AccountRank.GAMEMASTER) {
	
		@Override
		public void handle(String command, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			command = command.trim();
			if(command.equalsIgnoreCase('.'+this.name)) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
			}
			else {
				String[] value = command.split(" ");
				if(value.length < 2) {
					return;
				}
				int i = 0;
				while(i < this.subCommandList.size()) {
					if(this.subCommandList.get(i).getName().equalsIgnoreCase(value[1])) {
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
					ArrayList<Integer> list = BanMgr.getBanListAccountIDLowAsync();
					if(list == null) {
						return;
					}
					int i = list.size();
					builder.setLength(0);
					while(--i >= 0) {
						String name = AccountMgr.loadAccountNameFromID(list.get(i));
						if(name != null) {
							builder.append("\n").append(name).append(", id : ").append(list.get(i));
						}
					}
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), builder.toString(), MessageType.SELF);
				}
				else {
					if(StringUtils.containsOnlySpace(value[2])) {
						return;
					}
					if(getBanListAccountPattern == null) {
						getBanListAccountPattern = Server.getAsyncLowPriorityJDO().prepare("SELECT COUNT(account_id) FROM account_banned WHERE account_id = ?");
					}
					ArrayList<SQLDatas> accountIDList = AccountMgr.loadAccountIDAndNameFromNamePattern(value[2]);
					if(accountIDList == null || accountIDList.size() == 0) {
						CommandSendMessage.selfWithoutAuthor(player.getConnection(), "No account match ".concat(value[2]), MessageType.SELF);
						return;
					}
					builder.setLength(0);
					builder.append("List of banned account matching ").append(value[2]).append(':');
					int i = 0;
					while(i < accountIDList.size()) {
						getBanListAccountPattern.clear();
						getBanListAccountPattern.putInt((int)accountIDList.get(i).getNextObject());
						getBanListAccountPattern.execute();
						if(getBanListAccountPattern.fetch()) {
							int number = getBanListAccountPattern.getInt();
							if(number > 0) {
								builder.append("\n").append((String)accountIDList.get(i).getNextObject());
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
	private final static ChatSubCommand banlist_character = new ChatSubCommand("character", "banlist", "Syntax: .banlist character [pattern]\n\nDisplay the character banlist for the given pattern.", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [pattern] in .banlist character [pattern]", MessageType.SELF);
				return;
			}
			/*try {
				if(getBanListCharacterPattern == null) {
					getBanListCharacterPattern = Server.getAsyncLowPriorityJDO().prepare("SELECT COUNT(character_id) FROM character_banned WHERE character_id = ?");
				}
				ArrayList<SQLDatas> characterIDList = CharacterMgr.loadCharacterIDAndNameFromNamePattern(value[2]);
				if(characterIDList == null || characterIDList.size() == 0) {
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), "No character match ".concat(value[2]), MessageType.SELF);
					return;
				}
				builder.setLength(0);
				int i = 0;
				boolean init = false;
				while(i < characterIDList.size()) {
					getBanListCharacterPattern.clear();
					getBanListCharacterPattern.putInt(characterIDList.get(i).getIValue1());
					getBanListCharacterPattern.execute();
					if(getBanListCharacterPattern.fetch()) {
						if(getBanListCharacterPattern.getInt() > 0) {
							if(!init) {
								builder.append("List of banned character matching ").append(value[2]).append(':');
								init = true;
							}
							builder.append("\n").append(characterIDList.get(i).getStringValue1());
						}
					}
					i++;
				}
				if(!init) {
					builder.append("No character match ".concat(value[2]));
				}
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), builder.toString(), MessageType.SELF);
			}
			catch(SQLException e) {
				e.printStackTrace();
			}*/
			ArrayList<SQLDatas> list = CharacterMgr.loadCharacterBannedIDAndNameFromNamePatternHighAsync(value[2]);
			if(list == null || list.size() == 0) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "No character match ".concat(value[2]), MessageType.SELF);
				return;
			}
			int i = -1;
			builder.setLength(0);
			builder.append("List of banned character matching ").append(value[2]).append(':');
			while(++i < list.size()) {
					builder.append("\n").append((String)list.get(i).getNextObject());
			}
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), builder.toString(), MessageType.SELF);
		}
	};
	private final static ChatCommand character = new ChatCommand("character", AccountRank.GAMEMASTER) {
	
		@Override
		public void handle(String command, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			command = command.trim();
			if(command.equalsIgnoreCase('.'+this.name)) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
			}
			else {
				String[] value = command.split(" ");
				if(value.length < 2) {
					return;
				}
				int i = 0;
				while(i < this.subCommandList.size()) {
					if(this.subCommandList.get(i).getName().equalsIgnoreCase(value[1])) {
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
				if(StringUtils.isInteger(value[2])) {
					player.setLevel(Integer.parseInt(value[2]));
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), "You are now level ".concat(String.valueOf(player.getLevel())), MessageType.SELF);
				}
				else {
					target = Server.getInGameCharacterByName(value[2]);
					if(target != null) {
						target.setLevel(target.getLevel()+1);
						CharacterMgr.setExperience(target.getUnitID(), Player.getExpNeeded(target.getLevel()));
						CommandSendMessage.selfWithoutAuthor(player.getConnection(), new StringBuilder().append(target.getName()).append(" is now level ").append(target.getLevel()).toString(), MessageType.SELF);
					}
					else {
						int id = CharacterMgr.loadCharacterIDFromName(value[2]);
						if(id == -1) {
							return;
						}
						int level = Player.getLevel(CharacterMgr.getExperience(id));
						CharacterMgr.setExperience(id, Player.getExpNeeded(level+1));
						CommandSendMessage.selfWithoutAuthor(player.getConnection(), new StringBuilder().append(value[2]).append(" is now level ").append(level+1).toString(), MessageType.SELF);
					}
				}
			}
			else if(value.length == 4) {
				if(!StringUtils.isInteger(value[3])) {
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [level] in .character level [character_name] [level]", MessageType.SELF);
					return;
				}
				target = Server.getInGameCharacterByName(value[2]);
				int level = Integer.parseInt(value[3]);
				if(target != null) {
					target.setLevel(level);
					CharacterMgr.setExperience(target.getUnitID(), Player.getExpNeeded(target.getLevel()));
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), new StringBuilder().append(target.getName()).append(" is now level ").append(level).toString(), MessageType.SELF);
				}
				else {
					int id = CharacterMgr.loadCharacterIDFromName(value[2]);
					if(id == -1) {
						return;
					}
					CharacterMgr.setExperience(id, Player.getExpNeeded(level));
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), new StringBuilder().append(value[2]).append(" is now level ").append(level).toString(), MessageType.SELF);
				}
			}
		}
	};
	private final static ChatSubCommand character_erase = new ChatSubCommand("erase", "character", ".character erase [name]\n\nErase the character.", AccountRank.ADMINISTRATOR) {
		
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
			command = command.trim();
			if(command.equalsIgnoreCase('.'+this.name)) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
				return;
			}
			String[] value = command.split(" ");
			if(value.length < 2) {
				return;
			}
			int i = 0;
			while(i < this.subCommandList.size()) {
				if(this.subCommandList.get(i).getName().equalsIgnoreCase(value[1])) {
					this.subCommandList.get(i).handle(value, player);
					return;
				}
				i++;
			}
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
		}
	};
	private final static ChatSubCommand debug_looptoolongtimer = new ChatSubCommand("looptoolongtimer", "debug", ".debug looptoolongtimer [timer]\n\nSet the value of looptoolong print.", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect synthax for .debug looptoolongtimer [value]", MessageType.SELF);
				return;
			}
			if(!StringUtils.isInteger(value[2])) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for .debug looptoolongtimer [value]", MessageType.SELF);
				return;
			}
			int length = Integer.parseInt(value[2]);
			DebugMgr.setLoopTooLongValue(length);
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Loop too long timer set to "+length+".", MessageType.SELF);
		}
	};
	private final static ChatSubCommand debug_printsqltimer = new ChatSubCommand("printsqltimer", "debug", ".debug printsqltimer [true || false]\n\nSet wether the time to execute the request should be printed.", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect synthax for .debug printsqltimer [true || false]", MessageType.SELF);
				return;
			}
			if(!value[2].equalsIgnoreCase("true") && !value[2].equalsIgnoreCase("false")) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for .debug printsqltimer [true || false]", MessageType.SELF);
				return;
			}
			DebugMgr.setSQLRequestTimer(value[2].equalsIgnoreCase("true") ? true : false);
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Print SQL timer enabled.", MessageType.SELF);
		}
	};
	private final static ChatSubCommand debug_printlogfiletimer = new ChatSubCommand("printlogfiletimer", "debug", ".debug printlogfiletimer [true || false]\n\nSet wether the time to write in the log file should be printed.", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect synthax for .debug printlogfiletimer [true || false]", MessageType.SELF);
				return;
			}
			if(!value[2].equalsIgnoreCase("true") && !value[2].equalsIgnoreCase("false")) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for .debug printlogfiletimer [true || false]", MessageType.SELF);
				return;
			}
			DebugMgr.setWriteLogFileTimer(value[2].equalsIgnoreCase("true") ? true : false);
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Print log file timer enabled.", MessageType.SELF);
		}
	};
	private final static ChatSubCommand debug_chatcommandtimer = new ChatSubCommand("chatcommandtimer", "debug", ".debug chatcommandtimer [true || false]\n\nSet wether the time to execute the chat command should be printed.", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect synthax for .debug chatcommandtimer [true || false]", MessageType.SELF);
				return;
			}
			if(!value[2].equalsIgnoreCase("true") && !value[2].equalsIgnoreCase("false")) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for .debug chatcommandtimer [true || false]", MessageType.SELF);
				return;
			}
			DebugMgr.setChatCommandTimer(value[2].equalsIgnoreCase("true") ? true : false);
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Chatcommand timer enabled.", MessageType.SELF);
		}
	};
	private final static ChatSubCommand debug_whotimer = new ChatSubCommand("whotimer", "debug", ".debug whotimer [true || false]\n\nSet wether the time to execute the who command should be printed.", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect synthax for .debug whotimer [true || false]", MessageType.SELF);
				return;
			}
			if(!value[2].equalsIgnoreCase("true") && !value[2].equalsIgnoreCase("false")) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for .debug whotimer [true || false]", MessageType.SELF);
				return;
			}
			DebugMgr.setExecuteWhoTimer(value[2].equalsIgnoreCase("true") ? true : false);
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Debug timer enabled.", MessageType.SELF);
		}
	};
	private final static ChatSubCommand debug_packetreceived = new ChatSubCommand("packetreceived", "debug", ".debug packetreceived [true || false]\n\nSet wether there should be a print when a packet is received.", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length < 3) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect synthax for .debug packetreceived [true || false]", MessageType.SELF);
				return;
			}
			if(!value[2].equalsIgnoreCase("true") && !value[2].equalsIgnoreCase("false")) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for .debug packetreceived [true || false]", MessageType.SELF);
				return;
			}
			DebugMgr.setPacketReceived(value[2].equalsIgnoreCase("true") ? true : false);
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Packet record enabled.", MessageType.SELF);
		}
	};
	private final static ChatCommand additem = new ChatCommand("additem", "List of possible syntax: \n.additem [item_id || \"item_name\"] to add the item to yourself.\n.additem [item_id || \"item_name\"] [character_name]\n.additem [item_id || \"item_name\"] [amount] [character_id || character_name]\nItem name should not contains space, for example : Potion_of_healing", AccountRank.GAMEMASTER) {
	
		@Override
		public void handle(String command, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			command = command.trim();
			if(command.equalsIgnoreCase('.'+this.name)) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.helpMessage, MessageType.SELF);
			}
			else {
				String[] value = command.split(" ");
				if(value.length < 2) {
					return;
				}
				if(value.length == 2) {
					if(StringUtils.isInteger(value[1])) {
						Item item = Item.getItemClone(Integer.parseInt(value[1]));
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
					if(!StringUtils.isInteger(value[2])) {
						playerToAdd = Server.getCharacter(value[2]);
						if(playerToAdd == null) {
							CommandPlayerNotFound.write(player.getConnection(), value[2]);
							return;
						}
					}
					else {
						amount = Integer.parseInt(value[2]);
					}
					if(StringUtils.isInteger(value[1])) {
						Item item = Item.getItemClone(Integer.parseInt(value[1]));
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
					if(!StringUtils.isInteger(value[2])) {
						CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [amount] in .additem [item_id || item_name] [amount] [character_id || character_name]", MessageType.SELF);
						return;
					}
					Item item = null;
					if(StringUtils.isInteger(value[1])) {
						item = Item.getItemClone(Integer.parseInt(value[1]));
					}
					else {
						//TODO: find item by name efficency
					}
					if(item == null) {
						CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Item not found.", MessageType.SELF);
						return;
					}
					Player playerToAdd = null;
					if(StringUtils.isInteger(value[3])) {
						playerToAdd = Server.getInGameCharacter(Integer.parseInt(value[3]));
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
	private final static ChatCommand gm = new ChatCommand("gm", AccountRank.PLAYER) {
	
		@Override
		public void handle(String command, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			command = command.trim();
			if(command.equalsIgnoreCase('.'+this.name)) {
				if(!checkRank(player, AccountRank.MODERATOR)) {
					return;
				}
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Current GM mode : ".concat(Boolean.toString(player.isGMOn())), MessageType.SELF);
				return;
			}
			String[] value = command.split(" ");
			if(value.length < 2) {
				return;
			}
			if(value[1].equalsIgnoreCase("on")) {
				if(!checkRank(player, AccountRank.MODERATOR)) {
					return;
				}
				player.setGMOn(true);
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "GM mode enabled.", MessageType.SELF);
				return;
			}
			else if(value[1].equalsIgnoreCase("off")) {
				if(!checkRank(player, AccountRank.MODERATOR)) {
					return;
				}
				player.setGMOn(false);
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "GM mode disabled.", MessageType.SELF);
				return;
			}
			int i = 0;
			while(i < this.subCommandList.size()) {
				if(this.subCommandList.get(i).getName().equalsIgnoreCase(value[1])) {
					this.subCommandList.get(i).handle(value, player);
					return;
				}
				i++;
			}
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
		}
	};
	private final static ChatSubCommand gm_list = new ChatSubCommand("list", "gm", ".gm list\n\nDispay all the online gamemaster and their GM status.", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			StringBuilder builder = null;
			boolean init = false;
			for(Player players : Server.getInGamePlayerList().values()) {
				if(players.getAccountRank().getValue() >= AccountRank.GAMEMASTER.getValue()) {
					if(!init) {
						builder = new StringBuilder();
						builder.append("List of available gamemaster : ");
						init = true;
					}
					builder.append("\n- name : ").append(players.getName()).append(" rank : ").append(player.getAccountRank().getName()).append(" GM status enabled : ").append(player.isGMOn());
				}
			}
			if(!init) {
				builder.append("No gamemaster online.");
			}
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), builder.toString(), MessageType.SELF);
		}
	};
	private final static ChatSubCommand gm_announce = new ChatSubCommand("announce", "gm", ".gm announce [message]\n\nSend a message to all online gamemaster without your name.", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length <= 2) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [message] in .gm announce [message]", MessageType.SELF);
				return;
			}
			int i = 2;
			builder.setLength(0);
			while(i < value.length) {
				builder.append(value[i]);
				i++;
			}
			String result = builder.toString();
			for(Player players : Server.getInGamePlayerList().values()) {
				if(players.getAccountRank().getValue() >= AccountRank.GAMEMASTER.getValue()) {
					CommandSendMessage.selfWithoutAuthor(players.getConnection(), result, MessageType.GM_ANNOUNCE, MessageColor.ANNOUNCE);
				}
			}
		}
	};
	private final static ChatSubCommand gm_nameannounce = new ChatSubCommand("nameannounce", "gm", ".gm nameannounce [message]\n\nSend a message to all online gamemaster with your name.", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length <= 2) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [message] in .gm announce [message]", MessageType.SELF);
				return;
			}
			int i = 2;
			builder.setLength(0);
			while(i < value.length) {
				builder.append(value[i]);
				i++;
			}
			String result = builder.toString();
			for(Player players : Server.getInGamePlayerList().values()) {
				if(players.getAccountRank().getValue() >= AccountRank.GAMEMASTER.getValue()) {
					CommandSendMessage.selfWithAuthor(players.getConnection(), result, player.getName(), MessageType.GM_ANNOUNCE, MessageColor.ANNOUNCE);
				}
			}
		}
	};
	private final static ChatSubCommand gm_notify = new ChatSubCommand("notify", "gm", ".gm notify [message]\n\nDisplay a notification on the screen of all online GM.", AccountRank.GAMEMASTER) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			if(value.length <= 2) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [message] in .gm notify [message]", MessageType.SELF);
				return;
			}
			int i = 2;
			builder.setLength(0);
			while(i < value.length) {
				builder.append(value[i]);
				i++;
			}
			String result = builder.toString();
			for(Player players : Server.getInGamePlayerList().values()) {
				if(players.getAccountRank().getValue() >= AccountRank.GAMEMASTER.getValue()) {
					CommandSendRedAlert.write(players, result);
				}
			}
		}
	};
	private final static ChatCommand reload = new ChatCommand("reload", AccountRank.ADMINISTRATOR) {
	
		@Override
		public void handle(String command, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			command = command.trim();
			if(command.equalsIgnoreCase('.'+this.name)) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), printSubCommandError(player), MessageType.SELF);
				return;
			}
			String[] value = command.split(" ");
			if(value.length < 2) {
				return;
			}
			int i = 0;
			while(i < this.subCommandList.size()) {
				if(this.subCommandList.get(i).getName().equalsIgnoreCase(value[1])) {
					this.subCommandList.get(i).handle(value, player);
					return;
				}
				i++;
			}
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printSubCommandError(player), MessageType.SELF);
		}
	};
	private final static ChatSubCommand reload_spell = new ChatSubCommand("spell", "reload", ".reload spell to reload the spells from the DB.", AccountRank.ADMINISTRATOR) {
		
		@Override
		public void handle(String[] value, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			try {
				SpellMgr.loadSpells();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
	private final static ChatCommand cooldown = new ChatCommand("cooldown", ".cooldown [spell_id]\n.cooldown [spell_id] [character_id] reset cooldown of spell [spell_id]", AccountRank.GAMEMASTER) {
	
		@Override
		public void handle(String command, Player player) {
			if(!checkRank(player, this.rank)) {
				return;
			}
			command = command.trim();
			if(command.equalsIgnoreCase('.'+this.name)) {
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printHelpMessage(), MessageType.SELF);
			}
			else {
				String[] value = command.split(" ");
				if(value.length < 2) {
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), this.printHelpMessage(), MessageType.SELF);
					return;
				}
				if(!StringUtils.isInteger(value[1])) {
					CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [spell_id] in .cooldown [spell_id]", MessageType.SELF);
					return;
				}
				if(value.length == 2) {
					player.resetSpellCooldown(Integer.parseInt(value[1]));
				}
				else {
					if(!StringUtils.isInteger(value[2])) {
						CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Incorrect value for [character_id] in .cooldown [spell_id] [character_id]", MessageType.SELF);
						return;
					}
					Player target = Server.getInGameCharacter(Integer.parseInt(value[2]));
					if(target == null) {
						CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Character not found.", MessageType.SELF);
						return;
					}
					target.resetSpellCooldown(Integer.parseInt(value[1]));
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
		debug.addSubCommand(debug_whotimer);
		debug.addSubCommand(debug_packetreceived);
		commandMap.put(debug.getName(), debug);
		commandMap.put(additem.getName(), additem);
		gm.addSubCommand(gm_list);
		gm.addSubCommand(gm_announce);
		gm.addSubCommand(gm_nameannounce);
		gm.addSubCommand(gm_notify);
		commandMap.put(gm.getName(), gm);
		reload.addSubCommand(reload_spell);
		commandMap.put(reload.getName(), reload);
		commandMap.put(cooldown.getName(), cooldown);
		unban.addSubCommand(unban_account);
		unban.addSubCommand(unban_character);
		commandMap.put(unban.getName(), unban);
	}
	
	static long convStringTimerToMS(String timer) {
		int valueStart = 0;
		long value = 0;
		int i = 0;
		while(i < timer.length()) {
			if(timer.charAt(i) == 'y') {
				String tmp = timer.substring(valueStart, i);
				if(StringUtils.isInteger(tmp)) {
					value+= Integer.parseInt(tmp)*31536000000l;
					valueStart = i+1;
				}
				else {
					return -666;
				}
			}
			else if(timer.charAt(i) == 'm') {
				String tmp = timer.substring(valueStart, i);
				if(StringUtils.isInteger(tmp)) {
					value+= Integer.parseInt(tmp)*2592000000l;
					valueStart = i+1;
				}
				else {
					return -666;
				}
			}
			else if(timer.charAt(i) == 'w') {
				String tmp = timer.substring(valueStart, i);
				if(StringUtils.isInteger(tmp)) {
					value+= Integer.parseInt(tmp)*604800000;
					valueStart = i+1;
				}
				else {
					return -666;
				}
			}
			else if(timer.charAt(i) == 'd') {
				String tmp = timer.substring(valueStart, i);
				if(StringUtils.isInteger(tmp)) {
					value+= Integer.parseInt(tmp)*86400000;
					valueStart = i+1;
				}
				else {
					return -666;
				}
				
			}
			else if(timer.charAt(i) == 'h') {
				String tmp = timer.substring(valueStart, i);
				if(StringUtils.isInteger(tmp)) {
					value+= Integer.parseInt(tmp)*3600000;
					valueStart = i+1;
				}
				else {
					return -666;
				}
			}
			else if(timer.charAt(i) == 's') {
				String tmp = timer.substring(valueStart, i);
				if(StringUtils.isInteger(tmp)) {
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
	
	static boolean isValidIpAdress(String ip) {
		return isValidIP.matcher(ip).matches();
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

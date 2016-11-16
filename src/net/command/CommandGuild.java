package net.command;

import net.Server;
import net.command.chat.CommandPlayerNotFound;
import net.command.chat.CommandSendMessage;
import net.command.chat.MessageType;
import net.connection.Connection;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.ClassType;
import net.game.Player;
import net.game.guild.Guild;
import net.game.guild.GuildManager;
import net.game.guild.GuildMember;
import net.game.guild.GuildRank;

public class CommandGuild extends Command {

	public CommandGuild(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	@Override
	public void read() {
		byte packetId = this.connection.readByte();
		if(packetId == PacketID.GUILD_UPDATE_PERMISSION) {
			int rank_order = this.connection.readInt();
			int permission = this.connection.readInt();
			if(rank_order == 1) {
				permission = Guild.GUILD_MASTER_PERMISSION;
			}
			String name = this.connection.readString();
			if(this.player.getGuild() != null) {
				if(this.player.getGuild().isLeader(this.player.getCharacterId())) {
					GuildRank rank = this.player.getGuild().getRank(rank_order);
					if(rank != null) {
						this.player.getGuild().setRankPermission(rank_order, permission, name);
						updatePermission(this.player.getGuild(), rank_order, permission);
					}
					else {
						CommandSendMessage.write(this.connection, "This rank doesn't exist.", MessageType.SELF);
					}
				}
				else {
					CommandSendMessage.write(this.connection, "You don't have the right to do this.", MessageType.SELF);
				}
			}
			else {
				CommandSendMessage.write(this.connection, "You are not in a guild.", MessageType.SELF);
			}
		}
		else if(packetId == PacketID.GUILD_INVITE_PLAYER) {
			String name = this.connection.readString();
			if(name.length() > 2) {
				name = name.substring(0, 1).toUpperCase()+name.substring(1).toLowerCase();
				if(this.player.getGuild() != null) {
					if(this.player.getGuild().getMember(this.player.getCharacterId()).getRank().canInvitePlayer()) {
						Player player = Server.getInGameCharacter(name);
						if(player != null) {
							if(player.getGuild() == null) {
								CommandSendMessage.write(this.connection, "You invited "+name+" to join your guild.", MessageType.SELF);
								joinGuildRequest(player.getConnection(), this.player.getName(), this.player.getGuild().getName());
								player.setGuildRequest(this.player.getGuild().getId());
							}
							else {
								CommandSendMessage.write(this.connection, name+" is already in a guild.", MessageType.SELF);
							}
						}
						else {
							CommandPlayerNotFound.write(this.connection, name);
						}
					}
					else {
						CommandSendMessage.write(this.connection, "You don't have the right to do this.", MessageType.SELF);
					}
				}
				else {
					CommandSendMessage.write(this.connection, "You are not in a guild.", MessageType.SELF);
				}
			}
			else {
				CommandPlayerNotFound.write(this.connection, name);
			}
		}
		else if(packetId == PacketID.GUILD_KICK_MEMBER) {
			int id = this.connection.readInt();
			if(this.player.getGuild() != null) {
				if(this.player.getGuild().getMember(this.player.getCharacterId()).getRank().canKickMember()) {
					GuildMember member = this.player.getGuild().getMember(id);
					if(member != null) {
						if(member.getRank().getOrder() > this.player.getGuild().getMember(this.player.getCharacterId()).getRank().getOrder()) {
							this.player.getGuild().removeMember(id, this.player.getName());
						}
						else {
							
						}
					}
					else {
						CommandSendMessage.write(this.connection, "This player is not in your guild.", MessageType.SELF);
					}
				}
				else {
					CommandSendMessage.write(this.connection, "You don't have the right to do this.", MessageType.SELF);
				}
			}
			else {
				CommandSendMessage.write(this.connection, "You are not in a guild.", MessageType.SELF);
			}
		}
		else if(packetId == PacketID.GUILD_ACCEPT_REQUEST) {
			if(this.player.getGuildRequest() != 0) {
				this.player.setGuild(Server.getGuildList(this.player.getGuildRequest()));
				this.player.getGuild().addMember(new GuildMember(this.player.getCharacterId(), this.player.getName(), this.player.getLevel(), this.player.getGuild().getRankList().get(this.player.getGuild().getRankList().size()-1), true, "", "", this.player.getClasse()));
				initGuildWhenLogin(this.connection, this.player);
			}
			else {
				//u haxxor
			}
		}
		else if(packetId == PacketID.GUILD_DECLINE_REQUEST) {
			System.out.println("REQUEST DECLINED");
			this.player.setGuildRequest(0);
		}
		else if(packetId == PacketID.GUILD_SET_MOTD) {
			String msg = this.connection.readString();
			if(this.player.getGuild() != null) {
				if(this.player.getGuild().getMember(this.player.getCharacterId()).getRank().canModifyMotd()) {
					if(msg.length() > Guild.MOTD_MAX_LENGTH) {
						msg = msg.substring(0, Guild.MOTD_MAX_LENGTH);
					}
					this.player.getGuild().setMotd(msg);
					updateMotd(this.player.getGuild());
					GuildManager.updateMotd(this.player.getGuild());
				}
				else {
					CommandSendMessage.write(this.connection, "You don't have the right to do this.", MessageType.SELF);
				}
			}
			else {
				CommandSendMessage.write(this.connection, "You are not in a guild.", MessageType.SELF);
			}
		}
		else if(packetId == PacketID.GUILD_SET_INFORMATION) {
			String msg = this.connection.readString();
			if(this.player.getGuild() != null) {
				if(this.player.getGuild().getMember(this.player.getCharacterId()).getRank().canModifyGuildInformation()) {
					if(msg.length() > Guild.INFORMATION_MAX_LENGTH) {
						msg = msg.substring(0, Guild.INFORMATION_MAX_LENGTH);
					}
					this.player.getGuild().setInformation(msg);
					updateInformation(this.player.getGuild());
					GuildManager.updateInformation(this.player.getGuild());
				}
				else {
					CommandSendMessage.write(this.connection, "You don't have the right to do this.", MessageType.SELF);
				}
			}
			else {
				CommandSendMessage.write(this.connection, "You are not in a guild.", MessageType.SELF);
			}
		}
		else if(packetId == PacketID.GUILD_PROMOTE_PLAYER) {
			int id = this.connection.readInt();
			if(this.player.getGuild() != null) {
				if(this.player.getGuild().getMember(this.player.getCharacterId()).getRank().canPromote()) {
					GuildMember member = this.player.getGuild().getMember(id);
					if(member != null) {
						if(member.getRank().getOrder() < 2) {
							if(this.player.getGuild().getMember(this.player.getCharacterId()).getRank().getOrder() > member.getRank().getOrder()) {
								member.setRank(this.player.getGuild().getRank(member.getRank().getOrder()-1));
								promotePlayer(this.player.getGuild(), member);
							}
							else {
								CommandSendMessage.write(this.connection, "You don't have the right to do this.", MessageType.SELF);
							}
						}
						else {
							CommandSendMessage.write(this.connection, member.getName()+"'s rank is too high.", MessageType.SELF);
						}
					}
					else {
						CommandSendMessage.write(this.connection, "This player is not in your guild.", MessageType.SELF);
					}
				}
				else {
					CommandSendMessage.write(this.connection, "You don't have the right to do this.", MessageType.SELF);
				}
			}
			else {
				CommandSendMessage.write(this.connection, "You are not in a guild.", MessageType.SELF);
			}
		}
		else if(packetId == PacketID.GUILD_DEMOTE_PLAYER) {
			int id = this.connection.readInt();
			if(this.player.getGuild() != null) {
				if(this.player.getGuild().getMember(this.player.getCharacterId()).getRank().canDemote()) {
					GuildMember member = this.player.getGuild().getMember(id);
					if(member != null) {
						if(member.getRank().getOrder() < this.player.getGuild().getRankList().size()) {
							if(this.player.getGuild().getMember(this.player.getCharacterId()).getRank().getOrder() > member.getRank().getOrder()) {
								member.setRank(this.player.getGuild().getRank(member.getRank().getOrder()+1));
								promotePlayer(this.player.getGuild(), member);
							}
							else {
								CommandSendMessage.write(this.connection, "You don't have the right to do this.", MessageType.SELF);
							}
						}
						else {
							CommandSendMessage.write(this.connection, member.getName()+" has already the lowest rank.", MessageType.SELF);
						}
					}
					else {
						CommandSendMessage.write(this.connection, "This player is not in your guild.", MessageType.SELF);
					}
				}
				else {
					CommandSendMessage.write(this.connection, "You don't have the right to do this.", MessageType.SELF);
				}
			}
			else {
				CommandSendMessage.write(this.connection, "You are not in a guild.", MessageType.SELF);
			}
		}
	}
	
	public static void promotePlayer(Guild guild, GuildMember member) {
		int i = 0;
		GuildMember temp;
		Connection connection;
		while(i < guild.getMemberList().size()) {
			temp = guild.getMemberList().get(i);
			if(Server.getInGameCharacter(temp.getId()) != null) {
				connection = Server.getInGameCharacter(temp.getId()).getConnection();
				connection.writeByte(PacketID.GUILD);
				connection.writeByte(PacketID.GUILD_PROMOTE_PLAYER);
				connection.writeInt(member.getId());
				connection.writeInt(member.getRank().getOrder());
				connection.send();
			}
			i++;
		}
	}
	
	public static void updateMotd(Guild guild) {
		int i = 0;
		GuildMember member;
		Connection connection;
		while(i < guild.getMemberList().size()) {
			member = guild.getMemberList().get(i);
			if(Server.getInGameCharacter(member.getId()) != null) {
				connection = Server.getInGameCharacter(member.getId()).getConnection();
				connection.writeByte(PacketID.GUILD);
				connection.writeByte(PacketID.GUILD_SET_MOTD);
				connection.writeString(guild.getMotd());
				connection.send();
			}
			i++;
		}
	}
	
	public static void updateInformation(Guild guild) {
		int i = 0;
		GuildMember member;
		Connection connection;
		while(i < guild.getMemberList().size()) {
			member = guild.getMemberList().get(i);
			if(Server.getInGameCharacter(member.getId()) != null) {
				connection = Server.getInGameCharacter(member.getId()).getConnection();
				connection.writeByte(PacketID.GUILD);
				connection.writeByte(PacketID.GUILD_SET_INFORMATION);
				connection.writeString(guild.getInformation());
				connection.send();
			}
			i++;
		}
	}
	
	public static void notifyOnlinePlayer(Player player) {
		if(player.getGuild() != null) {
			int i = 0;
			GuildMember member;
			Connection connection;
			while(i < player.getGuild().getMemberList().size()) {
				member = player.getGuild().getMemberList().get(i);
				if(Server.getInGameCharacter(member.getId()) != null) {
					connection = Server.getInGameCharacter(member.getId()).getConnection();
					connection.writeByte(PacketID.GUILD);
					connection.writeByte(PacketID.GUILD_ONLINE_PLAYER);
					connection.writeInt(player.getCharacterId());
					connection.send();
				}
				i++;
			}
		}
	}
	
	public static void notifyOfflinePlayer(Player player) {
		if(player.getGuild() != null) {
			int i = 0;
			GuildMember member;
			Connection connection;
			while(i < player.getGuild().getMemberList().size()) {
				member = player.getGuild().getMemberList().get(i);
				if(Server.getInGameCharacter(member.getId()) != null && member.getId() != player.getCharacterId()) {
					connection = Server.getInGameCharacter(member.getId()).getConnection();
					connection.writeByte(PacketID.GUILD);
					connection.writeByte(PacketID.GUILD_OFFLINE_PLAYER);
					connection.writeInt(player.getCharacterId());
					connection.send();
				}
				i++;
			}
		}
	}
	
	private static void joinGuildRequest(Connection connection, String player_name, String guild_name) {
		connection.writeByte(PacketID.GUILD);
		connection.writeByte(PacketID.GUILD_INVITE_PLAYER);
		connection.writeString(player_name);
		connection.writeString(guild_name);
		connection.send();
	}
	
	private static void updatePermission(Guild guild, int rank_order, int permission) {
		int i = 0;
		Connection connection;
		while(i < guild.getMemberList().size()) {
			if(Server.getInGameCharacter(guild.getMemberList().get(i).getId()) != null) {
				connection = Server.getInGameCharacter(guild.getMemberList().get(i).getId()).getConnection();
				connection.writeByte(PacketID.GUILD);
				connection.writeByte(PacketID.GUILD_UPDATE_PERMISSION);
				connection.writeInt(rank_order);
				connection.writeInt(permission);
				connection.send();
			}
			i++;
		}
	}
	
	public static void notifyNewMember(Guild guild, GuildMember member) {
		int i = 0;
		Connection connection;
		while(i < guild.getMemberList().size()) {
			if(Server.getInGameCharacter(guild.getMemberList().get(i).getId()) != null && guild.getMemberList().get(i) != member) {
				connection = Server.getInGameCharacter(guild.getMemberList().get(i).getId()).getConnection();
				connection.writeByte(PacketID.GUILD);
				connection.writeByte(PacketID.GUILD_NEW_MEMBER);
				connection.writeInt(member.getId());
				connection.writeString(member.getName());
				connection.writeString(member.getNote());
				connection.writeString(member.getOfficerNote());
				connection.writeInt(member.getRank().getOrder());
				connection.writeInt(member.getLevel());
				connection.writeBoolean(member.isOnline());
				connection.writeChar(member.getClassType().getValue());
				connection.send();
			}
			i++;
		}
	}
	
	public static void notifyKickedMember(Guild guild, GuildMember member, String name) {
		int i = 0;
		Connection connection;
		while(i < guild.getMemberList().size()) {
			if(Server.getInGameCharacter(guild.getMemberList().get(i).getId()) != null) {
				connection = Server.getInGameCharacter(guild.getMemberList().get(i).getId()).getConnection();
				connection.writeByte(PacketID.GUILD);
				connection.writeByte(PacketID.GUILD_KICK_MEMBER);
				connection.writeInt(member.getId());
				connection.writeString(member.getName());
				connection.writeString(name);
				connection.send();
			}
			i++;
		}
	}
	
	public static void initGuildWhenLogin(Connection connection, Player player) {
		if(player.getGuild() != null) {
			int i = 0;
			connection.writeByte(PacketID.GUILD);
			connection.writeByte(PacketID.GUILD_INIT);
			connection.writeInt(player.getGuild().getId());
			connection.writeInt(player.getGuild().getLeaderId());
			connection.writeString(player.getGuild().getName());
			connection.writeString(player.getGuild().getInformation());
			connection.writeString(player.getGuild().getMotd());
			connection.writeInt(player.getGuild().getRankList().size());
			while(i < player.getGuild().getRankList().size()) {
				connection.writeInt(player.getGuild().getRankList().get(i).getOrder());
				connection.writeString(player.getGuild().getRankList().get(i).getName());
				connection.writeInt(player.getGuild().getRankList().get(i).getPermission());
				i++;
			}
			i = 0;
			connection.writeInt(player.getGuild().getMemberList().size());
			connection.writeBoolean(player.getGuild().getMember(player.getCharacterId()).getRank().canSeeOfficerNote());
			if(player.getGuild().getMember(player.getCharacterId()).getRank().canSeeOfficerNote()) {
				while(i < player.getGuild().getMemberList().size()) {
					connection.writeInt(player.getGuild().getMemberList().get(i).getId());
					connection.writeInt(player.getGuild().getMemberList().get(i).getLevel());
					connection.writeString(player.getGuild().getMemberList().get(i).getName());
					//connection.writeChar(player.getGuild().getMemberList().get(i).getClassType().getValue());
					connection.writeChar(ClassType.GUERRIER.getValue());
					connection.writeString(player.getGuild().getMemberList().get(i).getNote());
					connection.writeString(player.getGuild().getMemberList().get(i).getOfficerNote());
					connection.writeInt(player.getGuild().getMemberList().get(i).getRank().getOrder());
					connection.writeBoolean(Server.getInGamePlayerList().containsKey(player.getGuild().getMemberList().get(i).getId()));
					i++;
				}
			}
			else {
				while(i < player.getGuild().getMemberList().size()) {
					connection.writeInt(player.getGuild().getMemberList().get(i).getId());
					connection.writeInt(player.getGuild().getMemberList().get(i).getLevel());
					connection.writeString(player.getGuild().getMemberList().get(i).getName());
					//connection.writeChar(player.getGuild().getMemberList().get(i).getClassType().getValue());
					connection.writeChar(ClassType.GUERRIER.getValue());
					connection.writeString(player.getGuild().getMemberList().get(i).getNote());
					connection.writeInt(player.getGuild().getMemberList().get(i).getRank().getOrder());
					connection.writeBoolean(Server.getInGamePlayerList().containsKey(player.getGuild().getMemberList().get(i).getId()));
					i++;
				}
			}
			connection.send();
		}
	}
}

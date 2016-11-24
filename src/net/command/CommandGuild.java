package net.command;

import java.sql.SQLException;

import net.Server;
import net.command.chat.CommandPlayerNotFound;
import net.command.chat.CommandSendMessage;
import net.command.chat.MessageType;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.ClassType;
import net.game.Player;
import net.game.guild.Guild;
import net.game.guild.GuildManager;
import net.game.guild.GuildMember;
import net.game.guild.GuildRank;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;

public class CommandGuild extends Command {
	
	private final static SQLRequest setLeaderInDB = new SQLRequest("UPDATE guild SET leader_id = ? WHERE id = ?") {
		
		@Override
		public void gatherData() {
			try {
				SQLDatas datas = this.datasList.get(0);
				this.statement.clear();
				this.statement.putInt(datas.getIValue1());
				this.statement.putInt(datas.getIValue2());
				this.statement.execute();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
	
	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		byte packetId = connection.readByte();
		if(packetId == PacketID.GUILD_UPDATE_PERMISSION) {
			int rank_order = connection.readInt();
			int permission = connection.readInt();
			if(rank_order == 1) {
				permission = Guild.GUILD_MASTER_PERMISSION;
			}
			String name = connection.readString();
			if(name.length() > 0) {
				if(isInAGuild(player)) {
					if(hasEnoughRight(player, player.getGuild().isLeader(player.getCharacterId()))) {
						GuildRank rank = player.getGuild().getRank(rank_order);
						if(rank != null) {
							player.getGuild().setRankPermission(rank_order, permission, name);
							updatePermission(player.getGuild(), rank_order, permission, name);
						}
						else {
							CommandSendMessage.write(connection, "This rank doesn't exist.", MessageType.SELF);
						}
					}
				}
			}
		}
		else if(packetId == PacketID.GUILD_INVITE_PLAYER) {
			String name = connection.readString();
			if(name.length() > 2) {
				name = name.substring(0, 1).toUpperCase()+name.substring(1).toLowerCase();
				if(isInAGuild(player)) {
					if(hasEnoughRight(player, player.getGuild().getMember(player.getCharacterId()).getRank().canInvitePlayer())) {
						Player member = Server.getInGameCharacter(name);
						if(member != null) {
							if(member.getGuild() == null) {
								CommandSendMessage.write(connection, "You invited "+name+" to join your guild.", MessageType.SELF);
								joinGuildRequest(member.getConnection(), player.getName(), player.getGuild().getName());
								member.setGuildRequest(player.getGuild().getId());
							}
							else {
								CommandSendMessage.write(connection, name+" is already in a guild.", MessageType.SELF);
							}
						}
						else {
							CommandPlayerNotFound.write(connection, name);
						}
					}
				}
			}
			else {
				CommandPlayerNotFound.write(connection, name);
			}
		}
		else if(packetId == PacketID.GUILD_KICK_MEMBER) {
			int id = connection.readInt();
			if(isInAGuild(player)) {
				if(hasEnoughRight(player, player.getGuild().getMember(player.getCharacterId()).getRank().canKickMember())) {
					GuildMember member = player.getGuild().getMember(id);
					if(playerIsInTheSameGuild(player, member)) {
						if(hasEnoughRight(player, member.getRank().getOrder() > player.getGuild().getMember(player.getCharacterId()).getRank().getOrder())) {
							player.getGuild().removeMember(id, player.getName());
							removeMember(player.getGuild(), player.getName(), id);
						}
					}
				}
			}
		}
		else if(packetId == PacketID.GUILD_ACCEPT_REQUEST) {
			if(player.getGuildRequest() != 0) {
				player.setGuild(Server.getGuildList(player.getGuildRequest()));
				player.getGuild().addMember(new GuildMember(player.getCharacterId(), player.getName(), player.getLevel(), player.getGuild().getRankList().get(player.getGuild().getRankList().size()-1), true, "", "", player.getClasse()));
				initGuildWhenLogin(connection, player);
			}
			else {
				CommandSendMessage.write(connection, "Nobody invited you to join their guild.", MessageType.SELF);
			}
		}
		else if(packetId == PacketID.GUILD_DECLINE_REQUEST) {
			player.setGuildRequest(0);
		}
		else if(packetId == PacketID.GUILD_SET_MOTD) {
			String msg = connection.readString();
			if(isInAGuild(player)) {
				if(hasEnoughRight(player, player.getGuild().getMember(player.getCharacterId()).getRank().canModifyMotd())) {
					if(msg.length() > Guild.MOTD_MAX_LENGTH) {
						msg = msg.substring(0, Guild.MOTD_MAX_LENGTH);
					}
					player.getGuild().setMotd(msg);
					updateMotd(player.getGuild());
					GuildManager.updateMotd(player.getGuild());
				}
			}
		}
		else if(packetId == PacketID.GUILD_SET_INFORMATION) {
			String msg = connection.readString();
			if(isInAGuild(player)) {
				if(hasEnoughRight(player, player.getGuild().getMember(player.getCharacterId()).getRank().canModifyGuildInformation())) {
					if(msg.length() > Guild.INFORMATION_MAX_LENGTH) {
						msg = msg.substring(0, Guild.INFORMATION_MAX_LENGTH);
					}
					player.getGuild().setInformation(msg);
					updateInformation(player.getGuild());
					GuildManager.updateInformation(player.getGuild());
				}
			}
		}
		else if(packetId == PacketID.GUILD_PROMOTE_PLAYER) {
			int id = connection.readInt();
			if(isInAGuild(player)) {
				if(hasEnoughRight(player, player.getGuild().getMember(player.getCharacterId()).getRank().canPromote())) {
					GuildMember member = player.getGuild().getMember(id);
					if(playerIsInTheSameGuild(player, member)) {
						if(member.getRank().getOrder() < 2) {
							if(player.getGuild().getMember(player.getCharacterId()).getRank().getOrder() > member.getRank().getOrder()) {
								member.setRank(player.getGuild().getRank(member.getRank().getOrder()-1));
								promotePlayer(player.getGuild(), member);
							}
							else {
								CommandSendMessage.write(connection, "You don't have the right to do this.", MessageType.SELF);
							}
						}
						else {
							CommandSendMessage.write(connection, member.getName()+"'s rank is too high.", MessageType.SELF);
						}
					}
				}
			}
		}
		else if(packetId == PacketID.GUILD_DEMOTE_PLAYER) {
			int id = connection.readInt();
			if(isInAGuild(player)) {
				if(hasEnoughRight(player, player.getGuild().getMember(player.getCharacterId()).getRank().canDemote())) {
					GuildMember member = player.getGuild().getMember(id);
					if(playerIsInTheSameGuild(player, member)) {
						if(member.getRank().getOrder() < player.getGuild().getRankList().size()) {
							if(hasEnoughRight(player, player.getGuild().getMember(player.getCharacterId()).getRank().getOrder() > member.getRank().getOrder())) {
								member.setRank(player.getGuild().getRank(member.getRank().getOrder()+1));
								promotePlayer(player.getGuild(), member);
							}
						}
						else {
							CommandSendMessage.write(connection, member.getName()+" has already the lowest rank.", MessageType.SELF);
						}
					}
				}
			}
		}
		else if(packetId == PacketID.GUILD_LEAVE) {
			if(isInAGuild(player)) {
				leaveGuild(player.getGuild(), player.getCharacterId());
				GuildManager.removeMemberFromDB(player.getGuild(), player.getCharacterId());
				player.setGuild(null);
			}
		}
		else if(packetId == PacketID.GUILD_SET_LEADER) {
			int id = connection.readInt();
			if(isInAGuild(player)) {
				if(player.getGuild().isLeader(player.getCharacterId())) {
					GuildMember member = player.getGuild().getMember(id);
					if(playerIsInTheSameGuild(player, member)) {
						member.setRank(player.getGuild().getRankList().get(0));
						player.getGuild().getMember(player.getCharacterId()).setRank(player.getGuild().getRankList().get(1));
						player.getGuild().setLeaderId(id);
						GuildManager.updateMemberRank(player.getCharacterId(), player.getGuild().getId(), player.getGuild().getMember(player.getCharacterId()).getRank().getOrder());
						setLeader(player.getGuild(), member.getId());
						//setLeaderInDB.setId(id);
						//setLeaderInDB.setId2(player.getGuild().getId());
						//Server.addNewRequest(setLeaderInDB);
						setLeaderInDB(id, player.getGuild().getId());
						GuildManager.updateMemberRank(member.getId(), player.getGuild().getId(), member.getRank().getOrder());
					}
				}
				else {
					CommandSendMessage.write(connection, "You don't have the right to do this.", MessageType.SELF);
				}
			}
		}
	}
	
	public static void removeMember(Guild guild, String name, int id) {
		int i = 0;
		GuildMember temp;
		Connection connection;
		while(i < guild.getMemberList().size()) {
			temp = guild.getMemberList().get(i);
			if(Server.getInGameCharacter(temp.getId()) != null) {
				connection = Server.getInGameCharacter(temp.getId()).getConnection();
				connection.writeByte(PacketID.GUILD);
				connection.writeByte(PacketID.GUILD_KICK_MEMBER);
				connection.writeString(name);
				connection.writeInt(id);
				connection.send();
			}
			i++;
		}
	}
	
	public static void setLeader(Guild guild, int id) {
		int i = 0;
		GuildMember temp;
		Connection connection;
		while(i < guild.getMemberList().size()) {
			temp = guild.getMemberList().get(i);
			if(Server.getInGameCharacter(temp.getId()) != null) {
				connection = Server.getInGameCharacter(temp.getId()).getConnection();
				connection.writeByte(PacketID.GUILD);
				connection.writeByte(PacketID.GUILD_SET_LEADER);
				connection.writeInt(id);
				connection.send();
			}
			i++;
		}
	}
	
	public static void leaveGuild(Guild guild, int id) {
		int i = 0;
		GuildMember temp;
		Connection connection;
		while(i < guild.getMemberList().size()) {
			temp = guild.getMemberList().get(i);
			if(Server.getInGameCharacter(temp.getId()) != null) {
				connection = Server.getInGameCharacter(temp.getId()).getConnection();
				connection.writeByte(PacketID.GUILD);
				connection.writeByte(PacketID.GUILD_MEMBER_LEFT);
				connection.writeInt(id);
				connection.send();
			}
			i++;
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
	
	private static void updatePermission(Guild guild, int rank_order, int permission, String name) {
		int i = 0;
		Connection connection;
		while(i < guild.getMemberList().size()) {
			if(Server.getInGameCharacter(guild.getMemberList().get(i).getId()) != null) {
				connection = Server.getInGameCharacter(guild.getMemberList().get(i).getId()).getConnection();
				connection.writeByte(PacketID.GUILD);
				connection.writeByte(PacketID.GUILD_UPDATE_PERMISSION);
				connection.writeInt(rank_order);
				connection.writeInt(permission);
				connection.writeString(name);
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
	
	private static void setLeaderInDB(int player_id, int guild_id) {
		setLeaderInDB.addDatas(new SQLDatas(player_id, guild_id));
		Server.addNewRequest(setLeaderInDB);
	}
	
	private static boolean isInAGuild(Player player) {
		if(player.getGuild() != null) {
			return true;
		}
		else {
			CommandSendMessage.write(player.getConnection(), "You are not in a guild.", MessageType.SELF);
			return false;
		}
	}
	
	private static boolean hasEnoughRight(Player player, boolean right) {
		if(right) {
			return true;
		}
		else {
			CommandSendMessage.write(player.getConnection(), "You don't have the right to do this.", MessageType.SELF);
			return false;
		}
	}
	
	private static boolean playerIsInTheSameGuild(Player player, GuildMember member) {
		if(member != null) {
			return true;
		}
		else {
			CommandSendMessage.write(player.getConnection(), "This player is not in your guild.", MessageType.SELF);
			return false;
		}
	}
}

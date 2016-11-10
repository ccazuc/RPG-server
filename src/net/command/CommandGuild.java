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
			if(this.player.getGuild() != null) {
				if(this.player.getGuild().isLeader(this.player.getCharacterId())) {
					GuildRank rank = this.player.getGuild().getRank(rank_order);
					if(rank != null) {
						rank.setPermission(permission);
						updatePermission(this.connection, permission, rank_order);
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
			name = name.substring(0, 1).toUpperCase()+name.substring(1).toLowerCase();
			if(this.player.getGuild() != null) {
				if(this.player.getGuild().getMember(this.player.getCharacterId()).getRank().canInvitePlayer() || true) {
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
			this.player.setGuildRequest(0);
		}
	}
	
	private static void joinGuildRequest(Connection connection, String player_name, String guild_name) {
		connection.writeByte(PacketID.GUILD);
		connection.writeByte(PacketID.GUILD_INVITE_PLAYER);
		connection.writeString(player_name);
		connection.writeString(guild_name);
		connection.send();
	}
	
	private static void updatePermission(Connection connection, int permission, int rank_order) {
		connection.writeByte(PacketID.GUILD);
		connection.writeByte(PacketID.GUILD_UPDATE_PERMISSION);
		connection.writeInt(rank_order);
		connection.writeInt(permission);
		connection.send();
	}
	
	public static void notifyNewMember(Guild guild, GuildMember member) {
		int i = 0;
		Connection connection = null;
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
					connection.writeChar(player.getGuild().getMemberList().get(i).getClassType().getValue());
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

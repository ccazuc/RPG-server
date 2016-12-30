package net.command.player;

import net.Server;
import net.command.Command;
import net.command.chat.CommandDefaultMessage;
import net.command.chat.CommandPlayerNotFound;
import net.command.chat.CommandSendMessage;
import net.command.chat.DefaultMessage;
import net.command.chat.MessageType;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.ClassType;
import net.game.Player;
import net.game.guild.Guild;
import net.game.guild.GuildMgr;
import net.game.guild.GuildMember;
import net.game.guild.GuildRank;
import net.game.manager.IgnoreMgr;

public class CommandGuild extends Command {
	
	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		short packetId = connection.readShort();
		if(packetId == PacketID.GUILD_UPDATE_PERMISSION) {
			int rank_order = connection.readInt();
			int permission = connection.readInt();
			String name = connection.readString();
			if(name.length() == 0) {
				return;
			}
			if(!isInAGuild(player)) {
				return;
			}
			if(!hasEnoughRight(player, player.getGuild().isLeader(player.getCharacterId()))) {
				return;
			}
			GuildRank rank = player.getGuild().getRank(rank_order);
			if(rank == null) {
				CommandSendMessage.selfWithoutAuthor(connection, "This rank doesn't exist.", MessageType.SELF);
				return;
			}
			if(rank_order == 1) {
				permission = Guild.GUILD_MASTER_PERMISSION;
			}
			player.getGuild().setRankPermission(rank_order, permission, name);
			updatePermission(player.getGuild(), rank_order, permission, name);
		}
		else if(packetId == PacketID.GUILD_INVITE_PLAYER) {
			String name = connection.readString();
			if(!(name.length() > 2)) {
				CommandPlayerNotFound.write(connection, name);
				return;
			}
			name = name.substring(0, 1).toUpperCase()+name.substring(1).toLowerCase();
			if(!isInAGuild(player)) {
				return;
			}
			if(!hasEnoughRight(player, player.getGuild().getMember(player.getCharacterId()).getRank().canInvitePlayer())) {
				return;
			}
			Player member = Server.getInGameCharacterByName(name);
			if(member == null) {
				CommandPlayerNotFound.write(connection, name);
				return;
			}
			if(member.getGuildRequest() != 0) {
				return;
			}
			if(IgnoreMgr.isIgnored(member.getid(), player.getCharacterId())) {
				CommandSendMessage.selfWithoutAuthor(connection, member.getName()+IgnoreMgr.ignoreMessage, MessageType.SELF);
				return;
			}
			if(member.getGuild() != null) {
				CommandSendMessage.selfWithoutAuthor(connection, name+" is already in a guild.", MessageType.SELF);
				return;
			}
			CommandSendMessage.selfWithoutAuthor(connection, "You invited "+name+" to join your guild.", MessageType.SELF);
			joinGuildRequest(member.getConnection(), player.getName(), player.getGuild().getName());
			member.setGuildRequest(player.getGuild().getId());
		}
		else if(packetId == PacketID.GUILD_KICK_MEMBER) {
			int id = connection.readInt();
			if(!isInAGuild(player)) {
				return;
			}
			if(!hasEnoughRight(player, player.getGuild().getMember(player.getCharacterId()).getRank().canKickMember())) {
				return;
			}
			GuildMember member = player.getGuild().getMember(id);
			if(!playerIsInTheSameGuild(player, member)) {
				return;
			}
			if(!hasEnoughRight(player, member.getRank().getOrder() > player.getGuild().getMember(player.getCharacterId()).getRank().getOrder())) {
				return;
			}
			player.getGuild().removeMember(id, player.getName());
			removeMember(player.getGuild(), player.getName(), id);
		}
		else if(packetId == PacketID.GUILD_ACCEPT_REQUEST) {
			if(player.getGuildRequest() == 0) {
				CommandSendMessage.selfWithoutAuthor(connection, "Nobody invited you to join their guild.", MessageType.SELF);
				return;
			}
			player.setGuild(GuildMgr.getGuild(player.getGuildRequest()));
			player.getGuild().addMember(new GuildMember(player.getCharacterId(), player.getName(), player.getLevel(), player.getGuild().getRankList().get(player.getGuild().getRankList().size()-1), true, "", "", player.getClasse(), System.currentTimeMillis()));
			initGuildWhenLogin(player);
			player.setGuildRequest(0);
		}
		else if(packetId == PacketID.GUILD_DECLINE_REQUEST) {
			player.setGuildRequest(0);
		}
		else if(packetId == PacketID.GUILD_SET_MOTD) {
			String msg = connection.readString();
			if(!isInAGuild(player)) {
				return;
			}
			if(!hasEnoughRight(player, player.getGuild().getMember(player.getCharacterId()).getRank().canModifyMotd())) {
				return;
			}
			if(msg.length() > Guild.MOTD_MAX_LENGTH) {
				msg = msg.substring(0, Guild.MOTD_MAX_LENGTH);
			}
			player.getGuild().setMotd(msg);
			updateMotd(player.getGuild());
			GuildMgr.updateMotd(player.getGuild());
		}
		else if(packetId == PacketID.GUILD_SET_INFORMATION) {
			String msg = connection.readString();
			if(!isInAGuild(player)) {
				return;
			}
			if(!hasEnoughRight(player, player.getGuild().getMember(player.getCharacterId()).getRank().canModifyGuildInformation())) {
				return;
			}
			if(msg.length() > Guild.INFORMATION_MAX_LENGTH) {
				msg = msg.substring(0, Guild.INFORMATION_MAX_LENGTH);
			}
			player.getGuild().setInformation(msg);
			updateInformation(player.getGuild());
			GuildMgr.updateInformation(player.getGuild());
		}
		else if(packetId == PacketID.GUILD_PROMOTE_PLAYER) {
			int id = connection.readInt();
			if(!isInAGuild(player)) {
				return;
			}
			if(!hasEnoughRight(player, player.getGuild().getMember(player.getCharacterId()).getRank().canPromote())) {
				return;
			}
			GuildMember member = player.getGuild().getMember(id);
			if(!playerIsInTheSameGuild(player, member)) {
				return;
			}
			if(!(member.getRank().getOrder() < 2)) {
				CommandSendMessage.selfWithoutAuthor(connection, member.getName()+"'s rank is too high.", MessageType.SELF);
				return;
			}
			if(!hasEnoughRight(player, player.getGuild().getMember(player.getCharacterId()).getRank().getOrder() > member.getRank().getOrder())) {
				return;
			}
			member.setRank(player.getGuild().getRank(member.getRank().getOrder()-1));
			promotePlayer(player.getGuild(), member);
		}
		else if(packetId == PacketID.GUILD_DEMOTE_PLAYER) {
			int id = connection.readInt();
			if(!isInAGuild(player)) {
				return;
			}
			if(!hasEnoughRight(player, player.getGuild().getMember(player.getCharacterId()).getRank().canDemote())) {
				return;
			}
			GuildMember member = player.getGuild().getMember(id);
			if(!playerIsInTheSameGuild(player, member)) {
				return;
			}
			if(!(member.getRank().getOrder() < player.getGuild().getRankList().size())) {
				CommandSendMessage.selfWithoutAuthor(connection, member.getName()+" has already the lowest rank.", MessageType.SELF);
				return;
			}
			if(!hasEnoughRight(player, player.getGuild().getMember(player.getCharacterId()).getRank().getOrder() > member.getRank().getOrder())) {
				return;
			}
			member.setRank(player.getGuild().getRank(member.getRank().getOrder()+1));
			promotePlayer(player.getGuild(), member);
		}
		else if(packetId == PacketID.GUILD_LEAVE) {
			if(!isInAGuild(player)) {
				return;
			}
			leaveGuild(player.getGuild(), player.getCharacterId());
			GuildMgr.removeMemberFromDB(player.getGuild(), player.getCharacterId());
			player.setGuild(null);
		}
		else if(packetId == PacketID.GUILD_SET_LEADER) {
			int id = connection.readInt();
			if(!isInAGuild(player)) {
				return;
			}
			if(!hasEnoughRight(player, player.getGuild().isLeader(player.getCharacterId()))) {
				return;
			}
			GuildMember member = player.getGuild().getMember(id);
			if(!playerIsInTheSameGuild(player, member)) {
				return;
			}
			member.setRank(player.getGuild().getRankList().get(0));
			player.getGuild().getMember(player.getCharacterId()).setRank(player.getGuild().getRankList().get(1));
			player.getGuild().setLeaderId(id);
			GuildMgr.updateMemberRank(player.getCharacterId(), player.getGuild().getId(), player.getGuild().getMember(player.getCharacterId()).getRank().getOrder());
			sendLeaderToClient(player.getGuild(), member.getId());
			GuildMgr.setLeaderInDB(id, player.getGuild().getId());
			GuildMgr.updateMemberRank(member.getId(), player.getGuild().getId(), member.getRank().getOrder());
		}
		else if(packetId == PacketID.GUILD_SET_MEMBER_NOTE) {
			int id = connection.readInt();
			String note = connection.readString();
			if(!isInAGuild(player)) {
				return;
			}
			if(!hasEnoughRight(player, player.getGuild().getMember(player.getCharacterId()).getRank().canEditPublicNote())) {
				return;
			}
			GuildMember member = player.getGuild().getMember(id);
			if(!playerIsInTheSameGuild(player, member)) {
				return;
			}
			if(note.length() > Guild.MEMBER_NOTE_MAX_LENGTH) {
				note = note.substring(0, Guild.MEMBER_NOTE_MAX_LENGTH);
			}
			member.setNote(note);
			sendMemberNoteToClient(player.getGuild(), member);
			GuildMgr.updateMemberNote(member.getId(), member.getNote());
		}
		else if(packetId == PacketID.GUILD_SET_MEMBER_OFFICER_NOTE) {
			int id = connection.readInt();
			String note = connection.readString();
			if(!isInAGuild(player)) {
				return;
			}
			if(!hasEnoughRight(player, player.getGuild().getMember(player.getCharacterId()).getRank().canEditOfficerNote())) {
				return;
			}
			GuildMember member = player.getGuild().getMember(id);
			if(!playerIsInTheSameGuild(player, member)) {
				return;
			}
			if(note.length() > Guild.MEMBER_OFFICER_NOTE_MAX_LENGTH) {
				note = note.substring(0, Guild.MEMBER_OFFICER_NOTE_MAX_LENGTH);
			}
			member.setOfficerNote(note);
			updateMemberOfficerNote(player.getGuild(), member);
			GuildMgr.updateMemberOfficerNote(member.getId(), member.getOfficerNote());
		}
	}
	
	public static void sendMemberNoteToClient(Guild guild, GuildMember member) {
		int i = 0;
		while(i < guild.getMemberList().size()) {
			GuildMember temp = guild.getMemberList().get(i);
			if(Server.getInGameCharacter(temp.getId()) != null) {
				Connection connection = Server.getInGameCharacter(temp.getId()).getConnection();
				connection.startPacket();
				connection.writeShort(PacketID.GUILD);
				connection.writeShort(PacketID.GUILD_SET_MEMBER_NOTE);
				connection.writeInt(member.getId());
				connection.writeString(member.getNote());
				connection.endPacket();
				connection.send();
			}
			i++;
		}
	}
	
	public static void updateMemberOfficerNote(Guild guild, GuildMember member) {
		int i = 0;
		while(i < guild.getMemberList().size()) {
			GuildMember temp = guild.getMemberList().get(i);
			if(Server.getInGameCharacter(temp.getId()) != null) {
				Connection connection = Server.getInGameCharacter(temp.getId()).getConnection();
				connection.startPacket();
				connection.writeShort(PacketID.GUILD);
				connection.writeShort(PacketID.GUILD_SET_MEMBER_OFFICER_NOTE);
				connection.writeInt(member.getId());
				connection.writeString(member.getOfficerNote());
				connection.endPacket();
				connection.send();
			}
			i++;
		}
	}
	
	public static void removeMember(Guild guild, String name, int id) {
		int i = 0;
		while(i < guild.getMemberList().size()) {
			GuildMember temp = guild.getMemberList().get(i);
			if(Server.getInGameCharacter(temp.getId()) != null) {
				Connection connection = Server.getInGameCharacter(temp.getId()).getConnection();
				connection.startPacket();
				connection.writeShort(PacketID.GUILD);
				connection.writeShort(PacketID.GUILD_KICK_MEMBER);
				connection.writeString(name);
				connection.writeInt(id);
				connection.endPacket();
				connection.send();
			}
			i++;
		}
	}
	
	public static void sendLeaderToClient(Guild guild, int id) {
		int i = 0;
		while(i < guild.getMemberList().size()) {
			GuildMember temp = guild.getMemberList().get(i);
			if(Server.getInGameCharacter(temp.getId()) != null) {
				Connection connection = Server.getInGameCharacter(temp.getId()).getConnection();
				connection.startPacket();
				connection.writeShort(PacketID.GUILD);
				connection.writeShort(PacketID.GUILD_SET_LEADER);
				connection.writeInt(id);
				connection.endPacket();
				connection.send();
			}
			i++;
		}
	}
	
	public static void leaveGuild(Guild guild, int id) {
		int i = 0;
		while(i < guild.getMemberList().size()) {
			GuildMember temp = guild.getMemberList().get(i);
			if(Server.getInGameCharacter(temp.getId()) != null) {
				Connection connection = Server.getInGameCharacter(temp.getId()).getConnection();
				connection.startPacket();
				connection.writeShort(PacketID.GUILD);
				connection.writeShort(PacketID.GUILD_MEMBER_LEFT);
				connection.writeInt(id);
				connection.endPacket();
				connection.send();
			}
			i++;
		}
	}
	
	public static void promotePlayer(Guild guild, GuildMember member) {
		int i = 0;
		while(i < guild.getMemberList().size()) {
			GuildMember temp = guild.getMemberList().get(i);
			if(Server.getInGameCharacter(temp.getId()) != null) {
				Connection connection = Server.getInGameCharacter(temp.getId()).getConnection();
				connection.startPacket();
				connection.writeShort(PacketID.GUILD);
				connection.writeShort(PacketID.GUILD_PROMOTE_PLAYER);
				connection.writeInt(member.getId());
				connection.writeInt(member.getRank().getOrder());
				connection.endPacket();
				connection.send();
			}
			i++;
		}
	}
	
	public static void updateMotd(Guild guild) {
		int i = 0;
		while(i < guild.getMemberList().size()) {
			GuildMember member = guild.getMemberList().get(i);
			if(Server.getInGameCharacter(member.getId()) != null) {
				Connection connection = Server.getInGameCharacter(member.getId()).getConnection();
				connection.startPacket();
				connection.writeShort(PacketID.GUILD);
				connection.writeShort(PacketID.GUILD_SET_MOTD);
				connection.writeString(guild.getMotd());
				connection.endPacket();
				connection.send();
			}
			i++;
		}
	}
	
	public static void updateInformation(Guild guild) {
		int i = 0;
		while(i < guild.getMemberList().size()) {
			GuildMember member = guild.getMemberList().get(i);
			if(Server.getInGameCharacter(member.getId()) != null) {
				Connection connection = Server.getInGameCharacter(member.getId()).getConnection();
				connection.startPacket();
				connection.writeShort(PacketID.GUILD);
				connection.writeShort(PacketID.GUILD_SET_INFORMATION);
				connection.writeString(guild.getInformation());
				connection.endPacket();
				connection.send();
			}
			i++;
		}
	}
	
	public static void notifyOnlinePlayer(Player player) {
		if(player.getGuild() != null) {
			int i = 0;
			while(i < player.getGuild().getMemberList().size()) {
				GuildMember member = player.getGuild().getMemberList().get(i);
				if(Server.getInGameCharacter(member.getId()) != null) {
					Connection connection = Server.getInGameCharacter(member.getId()).getConnection();
					connection.startPacket();
					connection.writeShort(PacketID.GUILD);
					connection.writeShort(PacketID.GUILD_ONLINE_PLAYER);
					connection.writeInt(player.getCharacterId());
					connection.endPacket();
					connection.send();
				}
				i++;
			}
		}
	}
	
	public static void notifyOfflinePlayer(Player player) {
		if(player.getGuild() != null) {
			int i = 0;
			while(i < player.getGuild().getMemberList().size()) {
				GuildMember member = player.getGuild().getMemberList().get(i);
				if(Server.getInGameCharacter(member.getId()) != null && member.getId() != player.getCharacterId()) {
					Connection connection = Server.getInGameCharacter(member.getId()).getConnection();
					connection.startPacket();
					connection.writeShort(PacketID.GUILD);
					connection.writeShort(PacketID.GUILD_OFFLINE_PLAYER);
					connection.writeInt(player.getCharacterId());
					connection.endPacket();
					connection.send();
				}
				i++;
			}
		}
	}
	
	private static void joinGuildRequest(Connection connection, String player_name, String guild_name) {
		connection.startPacket();
		connection.writeShort(PacketID.GUILD);
		connection.writeShort(PacketID.GUILD_INVITE_PLAYER);
		connection.writeString(player_name);
		connection.writeString(guild_name);
		connection.endPacket();
		connection.send();
	}
	
	private static void updatePermission(Guild guild, int rank_order, int permission, String name) {
		int i = 0;
		while(i < guild.getMemberList().size()) {
			if(Server.getInGameCharacter(guild.getMemberList().get(i).getId()) != null) {
				Connection connection = Server.getInGameCharacter(guild.getMemberList().get(i).getId()).getConnection();
				connection.startPacket();
				connection.writeShort(PacketID.GUILD);
				connection.writeShort(PacketID.GUILD_UPDATE_PERMISSION);
				connection.writeInt(rank_order);
				connection.writeInt(permission);
				connection.writeString(name);
				connection.endPacket();
				connection.send();
			}
			i++;
		}
	}
	
	public static void notifyNewMember(Guild guild, GuildMember member) {
		int i = 0;
		while(i < guild.getMemberList().size()) {
			if(Server.getInGameCharacter(guild.getMemberList().get(i).getId()) != null && guild.getMemberList().get(i) != member) {
				Connection connection = Server.getInGameCharacter(guild.getMemberList().get(i).getId()).getConnection();
				connection.startPacket();
				connection.writeShort(PacketID.GUILD);
				connection.writeShort(PacketID.GUILD_NEW_MEMBER);
				connection.writeInt(member.getId());
				connection.writeString(member.getName());
				connection.writeString(member.getNote());
				connection.writeString(member.getOfficerNote());
				connection.writeInt(member.getRank().getOrder());
				connection.writeInt(member.getLevel());
				connection.writeBoolean(member.isOnline());
				connection.writeByte(member.getClassType().getValue());
				connection.writeLong(member.getLastLoginTimer());
				connection.endPacket();
				connection.send();
			}
			i++;
		}
	}
	
	public static void notifyKickedMember(Guild guild, GuildMember member, String name) {
		int i = 0;
		while(i < guild.getMemberList().size()) {
			if(Server.getInGameCharacter(guild.getMemberList().get(i).getId()) != null) {
				Connection connection = Server.getInGameCharacter(guild.getMemberList().get(i).getId()).getConnection();
				connection.startPacket();
				connection.writeShort(PacketID.GUILD);
				connection.writeShort(PacketID.GUILD_KICK_MEMBER);
				connection.writeInt(member.getId());
				connection.writeString(member.getName());
				connection.writeString(name);
				connection.endPacket();
				connection.send();
			}
			i++;
		}
	}
	
	public static void initGuildWhenLogin(Player player) {
		if(player.getGuild() == null) {
			return;
		}
		Connection connection = player.getConnection();
		int i = 0;
		connection.startPacket();
		connection.writeShort(PacketID.GUILD);
		connection.writeShort(PacketID.GUILD_INIT);
		connection.writeInt(player.getGuild().getId());
		connection.writeInt(player.getGuild().getLeaderId());
		connection.writeString(player.getGuild().getName());
		connection.writeString(player.getGuild().getInformation());
		connection.writeString(player.getGuild().getMotd());
		connection.writeInt(player.getGuild().getRankList().size());
		while(i < player.getGuild().getRankList().size()) {
			GuildRank rank = player.getGuild().getRankList().get(i);
			connection.writeInt(rank.getOrder());
			connection.writeString(rank.getName());
			connection.writeInt(rank.getPermission());
			i++;
		}
		i = 0;
		connection.writeInt(player.getGuild().getMemberList().size());
		connection.writeBoolean(player.getGuild().getMember(player.getCharacterId()).getRank().canSeeOfficerNote());
		if(player.getGuild().getMember(player.getCharacterId()).getRank().canSeeOfficerNote()) {
			while(i < player.getGuild().getMemberList().size()) {
				GuildMember member = player.getGuild().getMemberList().get(i);
				connection.writeInt(member.getId());
				connection.writeInt(member.getLevel());
				connection.writeString(player.getGuild().getMemberList().get(i).getName());
				//connection.writeChar(member.getClassType().getValue());
				connection.writeByte(ClassType.GUERRIER.getValue());
				connection.writeString(member.getNote());
				connection.writeString(member.getOfficerNote());
				connection.writeInt(member.getRank().getOrder());
				connection.writeBoolean(Server.getInGamePlayerList().containsKey(member.getId()));
				connection.writeLong(member.getLastLoginTimer());
				i++;
			}
		}
		else {
			while(i < player.getGuild().getMemberList().size()) {
				GuildMember member = player.getGuild().getMemberList().get(i);
				connection.writeInt(member.getId());
				connection.writeInt(member.getLevel());
				connection.writeString(member.getName());
				//connection.writeChar(member.getClassType().getValue());
				connection.writeByte(ClassType.GUERRIER.getValue());
				connection.writeString(member.getNote());
				connection.writeInt(member.getRank().getOrder());
				connection.writeBoolean(Server.getInGamePlayerList().containsKey(member.getId()));
				connection.writeLong(member.getLastLoginTimer());
				i++;
			}
		}
		System.out.println("Guild send length "+connection.rBufferPosition());
		connection.endPacket();
		connection.send();
	}
	
	private static boolean isInAGuild(Player player) {
		if(player.getGuild() != null) {
			return true;
		}
		CommandDefaultMessage.write(player, DefaultMessage.NOT_IN_GUILD);
		return false;
	}
	
	private static boolean hasEnoughRight(Player player, boolean right) {
		if(right) {
			return true;
		}
		CommandDefaultMessage.write(player, DefaultMessage.NOT_ENOUGH_RIGHT);
		return false;
	}
	
	private static boolean playerIsInTheSameGuild(Player player, GuildMember member) {
		if(member != null) {
			return true;
		}
		CommandDefaultMessage.write(player, DefaultMessage.PLAYER_NOT_IN_GUILD);
		return false;
	}
}

package net.game.guild;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;

import jdo.JDOStatement;
import net.Server;
import net.game.ClassType;
import net.game.Player;
import net.sql.SQLRequest;

public class GuildManager {

	private static JDOStatement loadRank;
	private static JDOStatement loadGuildInformation;
	private static JDOStatement loadMember;
	private static JDOStatement loadMemberInformation;
	private static JDOStatement loadPlayerGuild;
	private static JDOStatement removeMemberFromDB;
	private static JDOStatement addMemberInDB;
	private static JDOStatement updateRank;
	private static SQLRequest updateInformation = new SQLRequest("UPDATE guild SET information = ? WHERE id = ?") {
		
		@Override
		public void gatherData() {
			try {
				this.statement.clear();
				this.statement.putString(this.msg);
				this.statement.putInt(this.id);
				this.statement.execute();
			} 
			catch (SQLTimeoutException e) {
				e.printStackTrace();
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	};
	private static SQLRequest updateMotd = new SQLRequest("UPDATE guild SET motd = ? WHERE id = ?") {
		
		@Override
		public void gatherData() {
			try {
				this.statement.clear();
				this.statement.putString(this.msg);
				this.statement.putInt(this.id);
				this.statement.execute();
			} 
			catch (SQLTimeoutException e) {
				e.printStackTrace();
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	};
	
	public void loadGuild(Player player) throws SQLException {
		if(loadPlayerGuild == null) {
			loadPlayerGuild = Server.getJDO().prepare("SELECT guild_id FROM guild_member WHERE member_id = ?");
			loadRank = Server.getJDO().prepare("SELECT rank_order, permission, name FROM guild_rank WHERE guild_id = ?");
			loadMember = Server.getJDO().prepare("SELECT member_id, rank, note, officer_note FROM guild_member WHERE guild_id = ?");
			loadGuildInformation = Server.getJDO().prepare("SELECT name, leader_id, information, motd FROM guild WHERE id = ?");
			loadMemberInformation = Server.getJDO().prepare("SELECT name, online, experience, class FROM `character` WHERE character_id = ?");
		}
		int guildId = 0;
		loadPlayerGuild.clear();
		loadPlayerGuild.putInt(player.getCharacterId());
		loadPlayerGuild.execute();
		if(loadPlayerGuild.fetch()) {
			guildId = loadPlayerGuild.getInt();
		}
		if(guildId != 0) {
			if(Server.getGuildList().containsKey(guildId)) {
				player.setGuild(Server.getGuildList(guildId));
				return;
			}
			ArrayList<GuildRank> rankList = new ArrayList<GuildRank>();
			loadRank.clear();
			loadRank.putInt(guildId);
			loadRank.execute();
			while(loadRank.fetch()) {
				int rank_order = loadRank.getInt();
				int permission = loadRank.getInt();
				String name = loadRank.getString();
				rankList.add(new GuildRank(rank_order, permission, name));
			}
			ArrayList<GuildMember> memberList = new ArrayList<GuildMember>();
			loadMember.clear();
			loadMember.putInt(guildId);
			loadMember.execute();
			while(loadMember.fetch()) {
				int member_id = loadMember.getInt();
				int rank = loadMember.getInt();
				GuildRank guildRank = null;
				int i = 0;
				while(i < rankList.size()) {
					if(rankList.get(i).getOrder() == rank) {
						guildRank = rankList.get(i);
						break;
					}
					i++;
				}
				String note = loadMember.getString();
				String officerNote = loadMember.getString();
				loadMemberInformation.clear();
				loadMemberInformation.putInt(member_id);
				loadMemberInformation.execute();
				if(loadMemberInformation.fetch()) {
					String name = loadMemberInformation.getString();
					boolean isOnline = loadMemberInformation.getInt() == 0 ? false : true;
					int level = Player.getLevel(loadMemberInformation.getInt());
					ClassType type = Player.convStringToClassType(loadMemberInformation.getString());
					memberList.add(new GuildMember(member_id, name, level, guildRank, isOnline, note, officerNote, type));
				}
				else {
					System.out.println("GuildManager:LoadGuild player not found in table character");
				}
			}
			loadGuildInformation.clear();
			loadGuildInformation.putInt(guildId);
			loadGuildInformation.execute();
			if(loadGuildInformation.fetch()) {
				String guildName = loadGuildInformation.getString();
				int leaderId = loadGuildInformation.getInt();
				String information = loadGuildInformation.getString();
				String motd = loadGuildInformation.getString();
				Server.addGuild(new Guild(guildId, leaderId, guildName, information, motd, memberList, rankList));
				player.setGuild(Server.getGuildList(guildId));
			}
		}
	}
	
	public static void removeMemberFromDB(Guild guild, int id) {
		try {
			if(removeMemberFromDB == null) {
				removeMemberFromDB = Server.getJDO().prepare("REMOVE FROM guild_member WHERE member_id = ? AND guild_id = ?");
			}
			removeMemberFromDB.execute();
			removeMemberFromDB.putInt(id);
			removeMemberFromDB.putInt(guild.getId());
			removeMemberFromDB.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void addMemberInDB(Guild guild, int id) {
		try {
			if(addMemberInDB == null) {
				addMemberInDB = Server.getJDO().prepare("INSERT INTO guild_member (member_id, guild_id, rank) VALUES (?, ?, ?)");
			}
			addMemberInDB.clear();
			addMemberInDB.putInt(id);
			addMemberInDB.putInt(guild.getId());
			addMemberInDB.putInt(guild.getRankList().size()-1);
			addMemberInDB.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updatePermission(Guild guild, int rank_order, int permission, String name) {
		try {
			if(updateRank == null) {
				updateRank = Server.getJDO().prepare("UPDATE guild_rank SET permission = ?, name = ? WHERE guild_id = ? AND rank_order = ?");
			}
			updateRank.clear();
			updateRank.putInt(permission);
			updateRank.putString(name);
			updateRank.putInt(guild.getId());
			updateRank.putInt(rank_order);
			updateRank.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateInformation(Guild guild) {
		updateInformation.setId(guild.getId());
		updateInformation.setMsg(guild.getInformation());
		Server.addNewRequest(updateInformation);
	}
	
	public static void updateMotd(Guild guild) {
		updateMotd.setId(guild.getId());
		updateMotd.setMsg(guild.getMotd());
		Server.addNewRequest(updateMotd);
	}
}

package net.game.guild;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

import jdo.JDOStatement;
import net.Server;
import net.game.ClassType;
import net.game.Player;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;

public class GuildMgr {

	private static HashMap<Integer, Guild> guildList = new HashMap<Integer, Guild>();
	private Player player;
	private static JDOStatement loadRank;
	private static JDOStatement loadGuildInformation;
	private static JDOStatement loadMember;
	private static JDOStatement loadMemberInformation;
	private static JDOStatement loadPlayerGuild;
	private static JDOStatement removeOrphanedGuildRank;
	private static JDOStatement removeOrphanedGuildMember;
	private final static SQLRequest updateInformation = new SQLRequest("UPDATE guild SET information = ? WHERE id = ?", "Update guild information") {
		
		@Override
		public void gatherData() {
			try {
				SQLDatas datas = this.datasList.get(0);
				this.statement.clear();
				this.statement.putString(datas.getStringValue1());
				this.statement.putInt(datas.getIValue1());
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
	private final static SQLRequest updateMotd = new SQLRequest("UPDATE guild SET motd = ? WHERE id = ?", "Update guild Motd") {
		
		@Override
		public void gatherData() {
			try {
				SQLDatas datas = this.datasList.get(0);
				this.statement.clear();
				this.statement.putString(datas.getStringValue1());
				this.statement.putInt(datas.getIValue1());
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
	private final static SQLRequest removeMember = new SQLRequest("REMOVE FROM guild_member WHERE member_id = ? AND guild_id = ?", "Remove guild member") {
		
		@Override
		public void gatherData() {
			try {
				SQLDatas datas = this.datasList.get(0);
				this.statement.clear();
				this.statement.putInt(datas.getIValue1());
				this.statement.putInt(datas.getIValue2());
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
	private final static SQLRequest updatePermission = new SQLRequest("UPDATE guild_rank SET permission = ?, name = ? WHERE guild_id = ? AND rank_order = ?", "Update guild permission") {
		
		@Override
		public void gatherData() {
			try {
				SQLDatas datas = this.datasList.get(0);
				this.statement.clear();
				this.statement.putInt(datas.getIValue3());
				this.statement.putString(datas.getStringValue1());
				this.statement.putInt(datas.getIValue1());
				this.statement.putInt(datas.getIValue2());
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
	private final static SQLRequest addMemberInDB = new SQLRequest("INSERT INTO guild_member (member_id, guild_id, rank) VALUES (?, ?, ?)", "Add guild member") {
		
		@Override
		public void gatherData() {
			try {
				SQLDatas datas = this.datasList.get(0);
				this.statement.clear();
				this.statement.putInt(datas.getIValue1());
				this.statement.putInt(datas.getIValue2());
				this.statement.putInt(datas.getIValue3());
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
	private final static SQLRequest updateMemberRank = new SQLRequest("UPDATE guild_member SET rank = ? WHERE guild_id = ? AND member_id = ?", "Update guild member rank") {
		
		@Override
		public void gatherData() {
			try {
				SQLDatas datas = this.datasList.get(0);
				this.statement.clear();
				this.statement.putInt(datas.getIValue1());
				this.statement.putInt(datas.getIValue2());
				this.statement.putInt(datas.getIValue3());
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
	private final static SQLRequest setLeaderInDB = new SQLRequest("UPDATE guild SET leader_id = ? WHERE id = ?", "Set guild leader") {
		
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
	private final static SQLRequest setMemberNoteInDB = new SQLRequest("UPDATE guild_member SET note = ? WHERE member_id = ?", "Set guild member note") {
		
		@Override
		public void gatherData() {
			try {
				SQLDatas datas = this.datasList.get(0);
				this.statement.clear();
				this.statement.putString(datas.getStringValue1());
				this.statement.putInt(datas.getIValue1());
				this.statement.execute();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
	private final static SQLRequest setMemberOfficerNoteInDB = new SQLRequest("UPDATE guild_member SET officer_note = ? WHERE member_id = ?", "Set guild member officerNote") {
		
		@Override
		public void gatherData() {
			try {
				SQLDatas datas = this.datasList.get(0);
				this.statement.clear();
				this.statement.putString(datas.getStringValue1());
				this.statement.putInt(datas.getIValue1());
				this.statement.execute();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
	
	public static void removeOrphanedGuildRank() {
		try {
			if(removeOrphanedGuildRank == null) {
				removeOrphanedGuildRank = Server.getJDO().prepare("DELETE rank FROM guild_rank rank LEFT JOIN guild g ON rank.guild_id = g.id WHERE g.id IS NULL");
			}
			removeOrphanedGuildRank.clear();
			removeOrphanedGuildRank.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void removeOrphanedMember() {
		try {
			if(removeOrphanedGuildMember == null) {
				removeOrphanedGuildMember = Server.getJDO().prepare("DELETE member FROM guild_member member LEFT JOIN guild g ON member.guild_id = g.id WHERE g.id IS NULL");
			}
			removeOrphanedGuildMember.clear();
			removeOrphanedGuildMember.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public GuildMgr(Player player) {
		this.player = player;
	}
	
	public void loadGuild() throws SQLException {
		if(loadPlayerGuild == null) {
			loadPlayerGuild = Server.getJDO().prepare("SELECT guild_id FROM guild_member WHERE member_id = ?");
			loadRank = Server.getJDO().prepare("SELECT rank_order, permission, name FROM guild_rank WHERE guild_id = ?");
			loadMember = Server.getJDO().prepare("SELECT member_id, rank, note, officer_note FROM guild_member WHERE guild_id = ?");
			loadGuildInformation = Server.getJDO().prepare("SELECT name, leader_id, information, motd FROM guild WHERE id = ?");
			loadMemberInformation = Server.getJDO().prepare("SELECT name, online, experience, class, last_login_timer FROM `character` WHERE character_id = ?");
		}
		int guildId = 0;
		loadPlayerGuild.clear();
		loadPlayerGuild.putInt(this.player.getCharacterId());
		loadPlayerGuild.execute();
		if(loadPlayerGuild.fetch()) {
			guildId = loadPlayerGuild.getInt();
		}
		if(guildId != 0) {
			if(guildList.containsKey(guildId)) {
				this.player.setGuild(getGuild(guildId));
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
					long last_login_timer = loadMemberInformation.getLong();
					memberList.add(new GuildMember(member_id, name, level, guildRank, isOnline, note, officerNote, type, last_login_timer));
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
				addGuild(new Guild(guildId, leaderId, guildName, information, motd, memberList, rankList));
				this.player.setGuild(getGuild(guildId));
			}
		}
	}
	
	public static void removeMemberFromDB(Guild guild, int id) {
		removeMember.addDatas(new SQLDatas(id, guild.getId()));
		Server.addNewSQLRequest(removeMember);
	}
	
	public static void addMemberInDB(Guild guild, int id) {
		addMemberInDB.addDatas(new SQLDatas(id, guild.getId(), guild.getRankList().size()-1));
		Server.addNewSQLRequest(addMemberInDB);
	}
	
	public static void updatePermission(Guild guild, int rank_order, int permission, String name) {
		updatePermission.addDatas(new SQLDatas(guild.getId(), rank_order, permission, name));
		Server.addNewSQLRequest(updatePermission);
	}
	
	public static void updateInformation(Guild guild) {
		updateInformation.addDatas(new SQLDatas(guild.getId(), guild.getInformation()));
		Server.addNewSQLRequest(updateInformation);
	}
	
	public static void updateMotd(Guild guild) {
		updateMotd.addDatas(new SQLDatas(guild.getId(), guild.getMotd()));
		Server.addNewSQLRequest(updateMotd);
	}
	
	public static void updateMemberRank(int playerId, int guildId, int rank) {
		updateMemberRank.addDatas(new SQLDatas(rank, guildId, playerId));
		Server.addNewSQLRequest(updateMemberRank);
	}
	
	public static void setLeaderInDB(int player_id, int guild_id) {
		setLeaderInDB.addDatas(new SQLDatas(player_id, guild_id));
		Server.addNewSQLRequest(setLeaderInDB);
	}
	
	public static void updateMemberNote(int player_id, String note) {
		setMemberNoteInDB.addDatas(new SQLDatas(player_id, note));
		Server.addNewSQLRequest(setMemberNoteInDB);
	}
	
	public static void updateMemberOfficerNote(int player_id, String officerNote) {
		setMemberOfficerNoteInDB.addDatas(new SQLDatas(player_id, officerNote));
		Server.addNewSQLRequest(setMemberOfficerNoteInDB);
	}
	
	public static HashMap<Integer, Guild> getGuildList() {
		return guildList;
	}
	
	public static Guild getGuild(int id) {
		return guildList.get(id);
	}
	
	public static void addGuild(Guild guild) {
		if(guild != null) {
			guildList.put(guild.getId(), guild);
		}
	}
}

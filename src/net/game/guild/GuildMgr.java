package net.game.guild;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import jdo.JDOStatement;
import net.Server;
import net.game.manager.CharacterMgr;
import net.game.unit.ClassType;
import net.game.unit.Player;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;
import net.thread.sql.SQLRequestPriority;
import net.thread.sql.SQLTask;

public class GuildMgr {

	public final static int RANK_MEMBER_DEFAULT = 129;
	public final static int RANK_INITIATE_DEFAULT = 129;
	public final static int RANK_VETERAN_DEFAULT = 24705;
	public final static int RANK_OFFICER_DEFAULT = 32231;
	public final static int RANK_GUILD_MASTER_DEFAULT = 32767;
	private final static HashMap<Integer, Guild> guildMap = new HashMap<Integer, Guild>();
	private static JDOStatement loadRank;
	private static JDOStatement loadGuildInformation;
	private static JDOStatement loadMember;
	private static JDOStatement loadJournal;
	private static JDOStatement loadMemberInformation;
	private static JDOStatement loadPlayerGuild;
	private static JDOStatement removeOrphanedGuildRank;
	private static JDOStatement removeOrphanedGuildMember;
	private static JDOStatement createGuild;
	private static JDOStatement createGuildRank;
	private static JDOStatement deleteGuild;
	private static JDOStatement deleteGuildMembers;
	private static JDOStatement deleteGuildRanks;
	private static JDOStatement deleteGuildEvents;
	private static JDOStatement loadGuildIDByName;
	private final static SQLRequest updateInformation = new SQLRequest("UPDATE guild SET information = ? WHERE id = ?", "Update guild information", SQLRequestPriority.LOW) {
		
		@Override
		public void gatherData() {
			try {
				SQLDatas datas = this.datasList.get(0);
				this.statement.clear();
				this.statement.putString(datas.getStringValue1());
				this.statement.putInt(datas.getIValue1());
				this.statement.execute();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	};
	private final static SQLRequest updateMotd = new SQLRequest("UPDATE guild SET motd = ? WHERE id = ?", "Update guild Motd", SQLRequestPriority.LOW) {
		
		@Override
		public void gatherData() {
			try {
				SQLDatas datas = this.datasList.get(0);
				this.statement.clear();
				this.statement.putString(datas.getStringValue1());
				this.statement.putInt(datas.getIValue1());
				this.statement.execute();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	};
	private final static SQLRequest removeMember = new SQLRequest("REMOVE FROM guild_member WHERE member_id = ? AND guild_id = ?", "Remove guild member", SQLRequestPriority.LOW) {
		
		@Override
		public void gatherData() {
			try {
				SQLDatas datas = this.datasList.get(0);
				this.statement.clear();
				this.statement.putInt(datas.getIValue1());
				this.statement.putInt(datas.getIValue2());
				this.statement.execute();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	};
	private final static SQLRequest updatePermission = new SQLRequest("UPDATE guild_rank SET permission = ?, name = ? WHERE guild_id = ? AND rank_order = ?", "Update guild permission", SQLRequestPriority.LOW) {
		
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
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	};
	private final static SQLRequest addMemberInDB = new SQLRequest("INSERT INTO guild_member (member_id, guild_id, rank) VALUES (?, ?, ?)", "Add guild member", SQLRequestPriority.LOW) {
		
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
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	};
	private final static SQLRequest updateMemberRank = new SQLRequest("UPDATE guild_member SET rank = ? WHERE guild_id = ? AND member_id = ?", "Update guild member rank", SQLRequestPriority.LOW) {
		
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
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	};
	private final static SQLRequest setLeaderInDB = new SQLRequest("UPDATE guild SET leader_id = ? WHERE id = ?", "Set guild leader", SQLRequestPriority.LOW) {
		
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
	private final static SQLRequest setMemberNoteInDB = new SQLRequest("UPDATE guild_member SET note = ? WHERE member_id = ?", "Set guild member note", SQLRequestPriority.LOW) {
		
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
	private final static SQLRequest setMemberOfficerNoteInDB = new SQLRequest("UPDATE guild_member SET officer_note = ? WHERE member_id = ?", "Set guild member officerNote", SQLRequestPriority.LOW) {
		
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
	private final static SQLTask fullyDeleteGuild = new SQLTask("Delete guild") {
		
		@Override
		public void gatherData() {
			Guild guild = this.datasList.get(0).getGuild();
			deleteGuild(guild);
			deleteGuildMembers(guild);
		}
	};
	
	static void deleteGuildTable(Guild guild) {
		try {
			if(deleteGuild == null) {
				deleteGuild = Server.getAsyncHighPriorityJDO().prepare("DELETE FROM guild WHERE id = ?");
			}
			deleteGuild.clear();
			deleteGuild.putInt(guild.getId());
			deleteGuild.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	static void deleteGuildRanks(Guild guild) {
		try {
			if(deleteGuildRanks == null) {
				deleteGuildRanks = Server.getAsyncHighPriorityJDO().prepare("DELETE FROM guild_rank WHERE guild_id = ?");
			}
			deleteGuildRanks.clear();
			deleteGuildRanks.putInt(guild.getId());
			deleteGuildRanks.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	static void deleteGuildMembers(Guild guild) {
		try {
			if(deleteGuildMembers == null) {
				deleteGuildMembers = Server.getAsyncHighPriorityJDO().prepare("DELETE FROM guild_member WHERE guild_id = ?");
			}
			deleteGuildMembers.clear();
			deleteGuildMembers.putInt(guild.getId());
			deleteGuildMembers.execute();
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	static void deleteGuildEvents(Guild guild) {
		try {
			if(deleteGuildEvents == null) {
				deleteGuildEvents = Server.getAsyncHighPriorityJDO().prepare("DELETE FROM guild_event WHERE guild_id = ?");
			}
			deleteGuildEvents.clear();
			deleteGuildEvents.putInt(guild.getId());
			deleteGuildEvents.execute();
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
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
	
	public static void createGuild(String guildName, int leaderID) {
		try {
			if(createGuild == null) {
				createGuild = Server.getJDO().prepare("INSERT INTO guild (id, name, leader_id, information, motd) VALUES(?, ?, ?, Guild Information, Message of the day)");
				createGuildRank = Server.getJDO().prepare("INSERT INTO guild_rank (guild_id, rank_order, permission, name) VALUES(?, ?, ?, ?)");
			}
			createGuild.clear();
			createGuild.putString(guildName);
			createGuild.putInt(leaderID);
			createGuild.execute();
			int guildID = loadGuildID(guildName);
			if(guildID == -1) {
				System.out.println("Error in GuildMgr.createGuild, guild not found");
				return;
			}
			createGuildRank.clear();
			createGuildRank.putInt(guildID);
			createGuildRank.putInt(1);
			createGuildRank.putInt(RANK_GUILD_MASTER_DEFAULT);
			createGuildRank.putString("Guild Master");
			createGuildRank.execute();
			createGuildRank.clear();
			createGuildRank.putInt(guildID);
			createGuildRank.putInt(2);
			createGuildRank.putInt(RANK_OFFICER_DEFAULT);
			createGuildRank.putString("Officer");
			createGuildRank.execute();
			createGuildRank.clear();
			createGuildRank.putInt(guildID);
			createGuildRank.putInt(3);
			createGuildRank.putInt(RANK_VETERAN_DEFAULT);
			createGuildRank.putString("Veteran");
			createGuildRank.execute();
			createGuildRank.clear();
			createGuildRank.putInt(guildID);
			createGuildRank.putInt(4);
			createGuildRank.putInt(RANK_MEMBER_DEFAULT);
			createGuildRank.putString("Member");
			createGuildRank.execute();
			createGuildRank.clear();
			createGuildRank.putInt(guildID);
			createGuildRank.putInt(5);
			createGuildRank.putInt(RANK_INITIATE_DEFAULT);
			createGuildRank.putString("Initiate");
			createGuildRank.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static int loadGuildID(String guildName) {
		try {
			if(loadGuildIDByName == null) {
				loadGuildIDByName = Server.getJDO().prepare("SELECT id FROM guild WHERE name = ?");
			}
			loadGuildIDByName.clear();
			loadGuildIDByName.putString(guildName);
			loadGuildIDByName.execute();
			if(loadGuildIDByName.fetch()) {
				return loadGuildIDByName.getInt();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static void deleteGuild(Guild guild) {
		if(guild == null) {
			System.out.println("Error in GuildMgr.deleteGuild : guild = null");
			return;
		}
		fullyDeleteGuild.addDatas(new SQLDatas(guild));
		Server.executeHighPrioritySQLTask(fullyDeleteGuild);
	}
	
	public static void loadGuild(Player player) {
		try {
			if(loadPlayerGuild == null) {
				loadPlayerGuild = Server.getAsyncHighPriorityJDO().prepare("SELECT guild_id FROM guild_member WHERE member_id = ?");
				loadRank = Server.getAsyncHighPriorityJDO().prepare("SELECT rank_order, permission, name FROM guild_rank WHERE guild_id = ?");
				loadMember = Server.getAsyncHighPriorityJDO().prepare("SELECT member_id, rank, note, officer_note FROM guild_member WHERE guild_id = ?");
				loadGuildInformation = Server.getAsyncHighPriorityJDO().prepare("SELECT name, leader_id, information, motd FROM guild WHERE id = ?");
				loadMemberInformation = Server.getAsyncHighPriorityJDO().prepare("SELECT name, online, experience, class, last_login_timer FROM `character` WHERE character_id = ?");
				loadJournal = Server.getAsyncHighPriorityJDO().prepare("SELECT event_type, player1_id, player2_id, date, rank_id FROM guild_event WHERE guild_id = ?");
			}
			int guildId = 0;
			loadPlayerGuild.clear();
			loadPlayerGuild.putInt(player.getUnitID());
			loadPlayerGuild.execute();
			if(loadPlayerGuild.fetch()) {
				guildId = loadPlayerGuild.getInt();
			}
			if(guildId != 0) {
				if(guildMap.containsKey(guildId)) {
					player.setGuild(getGuild(guildId));
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
					player.setGuild(getGuild(guildId));
				}
				loadJournal.clear();
				loadJournal.putInt(guildId);
				loadJournal.execute();
				Guild guild = getGuild(guildId);
				while(loadJournal.fetch()) {
					GuildJournalEventType type = GuildJournalEventType.values()[loadJournal.getByte()];
					String player1Name = getCharacterName(loadJournal.getInt());
					if(player1Name.length() == 0) {
						player1Name = "<Character deleted>";
					}
					String player2Name = getCharacterName(loadJournal.getInt());
					if(player2Name.length() == 0) {
						player2Name = "<Character deleted>";
					}
					long timer = loadJournal.getLong();
					int rankID = loadJournal.getInt();
					if(type == GuildJournalEventType.MEMBER_DEMOTED || type == GuildJournalEventType.MEMBER_PROMOTED) {
						guild.addEvent(new GuildEvent(timer, type, player1Name, player2Name, rankID));
					}
					else if(type == GuildJournalEventType.MEMBER_INVITED || type == GuildJournalEventType.MEMBER_KICKED) {
						guild.addEvent(new GuildEvent(timer, type, player1Name, player2Name));
					}
					else if(type == GuildJournalEventType.MEMBER_LEFT || type == GuildJournalEventType.MEMBER_JOINED) {
						guild.addEvent(new GuildEvent(timer, type, player1Name));
					}
				}
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static String getCharacterName(int id) {
		Player player = null;
		if((player = Server.getInGameCharacter(id)) != null) {
			return player.getName();
		}
		return CharacterMgr.loadCharacterNameFromIDHighAsync(id);
	}
	
	public static void removeMemberFromDB(Guild guild, int id) {
		removeMember.addDatas(new SQLDatas(id, guild.getId()));
		Server.executeSQLRequest(removeMember);
	}
	
	public static void addMemberInDB(Guild guild, int id) {
		addMemberInDB.addDatas(new SQLDatas(id, guild.getId(), guild.getRankList().size()-1));
		Server.executeSQLRequest(addMemberInDB);
	}
	
	public static void updatePermission(Guild guild, int rank_order, int permission, String name) {
		updatePermission.addDatas(new SQLDatas(guild.getId(), rank_order, permission, name));
		Server.executeSQLRequest(updatePermission);
	}
	
	public static void updateInformation(Guild guild) {
		updateInformation.addDatas(new SQLDatas(guild.getId(), guild.getInformation()));
		Server.executeSQLRequest(updateInformation);
	}
	
	public static void updateMotd(Guild guild) {
		updateMotd.addDatas(new SQLDatas(guild.getId(), guild.getMotd()));
		Server.executeSQLRequest(updateMotd);
	}
	
	public static void updateMemberRank(int playerId, int guildId, int rank) {
		updateMemberRank.addDatas(new SQLDatas(rank, guildId, playerId));
		Server.executeSQLRequest(updateMemberRank);
	}
	
	public static void setLeaderInDB(int player_id, int guild_id) {
		setLeaderInDB.addDatas(new SQLDatas(player_id, guild_id));
		Server.executeSQLRequest(setLeaderInDB);
	}
	
	public static void updateMemberNote(int player_id, String note) {
		setMemberNoteInDB.addDatas(new SQLDatas(player_id, note));
		Server.executeSQLRequest(setMemberNoteInDB);
	}
	
	public static void updateMemberOfficerNote(int player_id, String officerNote) {
		setMemberOfficerNoteInDB.addDatas(new SQLDatas(player_id, officerNote));
		Server.executeSQLRequest(setMemberOfficerNoteInDB);
	}
	
	public static HashMap<Integer, Guild> getGuildList() {
		return guildMap;
	}
	
	public static Guild getGuild(int id) {
		return guildMap.get(id);
	}
	
	public static void addGuild(Guild guild) {
		if(guild != null) {
			guildMap.put(guild.getId(), guild);
		}
	}
}

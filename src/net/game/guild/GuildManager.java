package net.game.guild;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;

import jdo.JDOStatement;
import net.Server;
import net.game.ClassType;
import net.game.Player;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;

public class GuildManager {

	private Player player;
	private static JDOStatement loadRank;
	private static JDOStatement loadGuildInformation;
	private static JDOStatement loadMember;
	private static JDOStatement loadMemberInformation;
	private static JDOStatement loadPlayerGuild;
	private final static SQLRequest updateInformation = new SQLRequest("UPDATE guild SET information = ? WHERE id = ?") {
		
		@Override
		public void gatherData() {
			try {
				SQLDatas datas = this.datasList.get(0);
				this.statement.clear();
				this.statement.putString(datas.getText());
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
	private final static SQLRequest updateMotd = new SQLRequest("UPDATE guild SET motd = ? WHERE id = ?") {
		
		@Override
		public void gatherData() {
			try {
				SQLDatas datas = this.datasList.get(0);
				this.statement.clear();
				this.statement.putString(datas.getText());
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
	private final static SQLRequest removeMember = new SQLRequest("REMOVE FROM guild_member WHERE member_id = ? AND guild_id = ?") {
		
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
	private final static SQLRequest updatePermission = new SQLRequest("UPDATE guild_rank SET permission = ?, name = ? WHERE guild_id = ? AND rank_order = ?") {
		
		@Override
		public void gatherData() {
			try {
				SQLDatas datas = this.datasList.get(0);
				this.statement.clear();
				this.statement.putInt(datas.getIValue3());
				this.statement.putString(datas.getText());
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
	private final static SQLRequest addMemberInDB = new SQLRequest("INSERT INTO guild_member (member_id, guild_id, rank) VALUES (?, ?, ?)") {
		
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
	private final static SQLRequest updateMemberRank = new SQLRequest("UPDATE guild_member SET rank = ? WHERE guild_id = ? AND member_id = ?") {
		
		@Override
		public void gatherData() {
			try {
				SQLDatas datas = this.datasList.get(0);
				this.statement.clear();
				this.statement.putInt(datas.getIValue1());
				this.statement.putInt(datas.getIValue2());
				this.statement.putInt(datas.getIValue3());
				//System.out.println("EXECUTE : rank: "+datas.getIValue1()+" guildId: "+datas.getIValue2()+" memberId: "+datas.getIValue3());
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
	
	public GuildManager(Player player) {
		this.player = player;
	}
	
	public void loadGuild() throws SQLException {
		if(loadPlayerGuild == null) {
			loadPlayerGuild = Server.getJDO().prepare("SELECT guild_id FROM guild_member WHERE member_id = ?");
			loadRank = Server.getJDO().prepare("SELECT rank_order, permission, name FROM guild_rank WHERE guild_id = ?");
			loadMember = Server.getJDO().prepare("SELECT member_id, rank, note, officer_note FROM guild_member WHERE guild_id = ?");
			loadGuildInformation = Server.getJDO().prepare("SELECT name, leader_id, information, motd FROM guild WHERE id = ?");
			loadMemberInformation = Server.getJDO().prepare("SELECT name, online, experience, class FROM `character` WHERE character_id = ?");
		}
		int guildId = 0;
		loadPlayerGuild.clear();
		loadPlayerGuild.putInt(this.player.getCharacterId());
		loadPlayerGuild.execute();
		if(loadPlayerGuild.fetch()) {
			guildId = loadPlayerGuild.getInt();
		}
		if(guildId != 0) {
			if(Server.getGuildList().containsKey(guildId)) {
				this.player.setGuild(Server.getGuildList(guildId));
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
				this.player.setGuild(Server.getGuildList(guildId));
			}
		}
	}
	
	public static void removeMemberFromDB(Guild guild, int id) {
		//removeMember.setId(id);
		//removeMember.setId2(guild.getId());
		removeMember.addDatas(new SQLDatas(id, guild.getId()));
		Server.addNewRequest(removeMember);
	}
	
	public static void addMemberInDB(Guild guild, int id) {
		/*addMemberInDB.setId(id);
		addMemberInDB.setId2(guild.getId());
		addMemberInDB.setValue(guild.getRankList().size()-1);*/
		addMemberInDB.addDatas(new SQLDatas(id, guild.getId(), guild.getRankList().size()-1));
		Server.addNewRequest(addMemberInDB);
	}
	
	public static void updatePermission(Guild guild, int rank_order, int permission, String name) {
		/*updatePermission.setValue(permission);
		updatePermission.setName(name);
		updatePermission.setId(guild.getId());
		updatePermission.setId2(rank_order);*/
		updatePermission.addDatas(new SQLDatas(guild.getId(), rank_order, permission, name));
		Server.addNewRequest(updatePermission);
	}
	
	public static void updateInformation(Guild guild) {
		//updateInformation.setId(guild.getId());
		//updateInformation.setMsg(guild.getInformation());
		updateMotd.addDatas(new SQLDatas(guild.getId(), guild.getInformation()));
		Server.addNewRequest(updateInformation);
	}
	
	public static void updateMotd(Guild guild) {
		//updateMotd.setId(guild.getId());
		//updateMotd.setMsg(guild.getMotd());
		updateMotd.addDatas(new SQLDatas(guild.getId(), guild.getMotd()));
		Server.addNewRequest(updateMotd);
	}
	
	public static void updateMemberRank(int playerId, int guildId, int rank) {
		updateMemberRank.addDatas(new SQLDatas(rank, guildId, playerId));
		//System.out.println("PlayerId: "+playerId+" guildId: "+guildId+" rank: "+rank);
		Server.addNewRequest(updateMemberRank);
	}
}

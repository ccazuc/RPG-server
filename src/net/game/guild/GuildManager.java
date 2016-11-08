package net.game.guild;

import java.sql.SQLException;
import java.util.ArrayList;

import jdo.JDOStatement;
import net.Server;
import net.game.ClassType;
import net.game.Player;

public class GuildManager {

	private static JDOStatement loadRank;
	private static JDOStatement loadGuildInformation;
	private static JDOStatement loadMember;
	private static JDOStatement loadMemberInformation;
	private static JDOStatement loadPlayerGuild;
	
	public void loadGuild(Player player) throws SQLException {
		if(loadPlayerGuild == null) {
			loadPlayerGuild = Server.getJDO().prepare("SELECT guild_id FROM guild_member WHERE member_id = ?");
			loadRank = Server.getJDO().prepare("SELECT rank_order, permission, name FROM guild_rank WHERE guild_id = ?");
			loadMember = Server.getJDO().prepare("SELECT member_id, rank, note, officer_note FROM guild_member WHERE guild_id = ?");
			loadGuildInformation = Server.getJDO().prepare("SELECT name, leader_id, information, motd FROM guild WHERE guild_id = ?");
			loadMemberInformation = Server.getJDO().prepare("SELECT name, online, level, class FROM `character` WHERE character_id = ?");
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
					int level = loadMemberInformation.getInt();
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
}

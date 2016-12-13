package net.game.guild;

import java.util.ArrayList;
import java.util.HashMap;

import net.command.player.CommandGuild;

public class Guild {

	private int id;
	private int leader_id;
	private String name;
	private String information;
	private String motd;
	private ArrayList<GuildMember> memberList;
	private HashMap<Integer, GuildMember> memberMap;
	private ArrayList<GuildRank> rankList;
	
	public final static int MEMBER_NOTE_MAX_LENGTH = 50;
	public final static int MEMBER_OFFICER_NOTE_MAX_LENGTH = 50;
	public final static int MOTD_MAX_LENGTH = 200;
	public final static int INFORMATION_MAX_LENGTH = 300;
	public final static int GUILD_MASTER_PERMISSION = 32767;
	
	public Guild(int id, int leader_id, String name, String information, String motd, ArrayList<GuildMember> memberList, ArrayList<GuildRank> rankList) {
		this.information = information;
		this.memberList = memberList;
		this.memberMap = new HashMap<Integer, GuildMember>();
		this.leader_id = leader_id;
		this.rankList = rankList;
		this.name = name;
		this.motd = motd;
		this.id = id;
		initMemberMap();
	}
	
	public GuildMember getMember(int id) {
		return this.memberMap.get(id);
	}
	
	public void addMember(GuildMember member) {
		this.memberList.add(member);
		this.memberMap.put(member.getId(), member);
		CommandGuild.notifyNewMember(this, member);
		GuildManager.addMemberInDB(this, member.getId());
	}
	
	public void removeMember(int id, String name) {
		CommandGuild.notifyKickedMember(this, this.memberMap.get(id), name);
		int i = 0;
		while(i < this.memberList.size()) {
			if(this.memberList.get(i).getId() == id) {
				this.memberList.remove(i);
				break;
			}
		}
		this.memberMap.remove(id);
		GuildManager.removeMemberFromDB(this, id);
	}
	
	public int getLeaderId() {
		return this.leader_id;
	}
	
	public boolean isLeader(int id) {
		return this.leader_id == id;
	}
	
	public void setLeaderId(int id) {
		this.leader_id = id;
	}
	
	public GuildRank getRank(int order) {
		int i = 0;
		while(i < this.rankList.size()) { 
			if(this.rankList.get(i).getOrder() == order) {
				return this.rankList.get(i);
			}
			i++;
		}
		return null;
	}
	
	private void initMemberMap() {
		int i = 0;
		while(i < this.memberList.size()) {
			this.memberMap.put(this.memberList.get(i).getId(), this.memberList.get(i));
			i++;
		}
	}
	
	public void setRankPermission(int rank_order, int permission, String name) {
		int i = 0;
		while(i < this.rankList.size()) {
			if(this.rankList.get(i).getOrder() == rank_order) {
				this.rankList.get(i).setPermission(permission);
				this.rankList.get(i).setName(name);
				GuildManager.updatePermission(this, rank_order, permission, name);
				return;
			}
			i++;
		}
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getInformation() {
		return this.information;
	}
	
	public void setInformation(String msg) {
		this.information = msg;
	}
	
	public String getMotd() {
		return this.motd;
	}
	
	public void setMotd(String msg) {
		this.motd = msg;
	}
	
	public ArrayList<GuildMember> getMemberList() {
		return this.memberList;
	}
	
	public ArrayList<GuildRank> getRankList() {
		return this.rankList;
	}
	
	/*public void sortMemberByName() {
		int i = 0;
		int j = 0;
		GuildMember temp;
		while(i < this.memberList.size()) {
			j = i;
			while(j < this.memberList.size()) {
				if(this.memberList.get(i).getName().compareTo(this.memberList.get(j).getName()) > 0) {
					temp = this.memberList.get(j);
					this.memberList.set(j, this.memberList.get(i));
					this.memberList.set(i, temp);
				}
				j++;
			}
			i++;
		}
	}
	
	public void sortMemberByRank() {
		int i = 0;
		int j = 0;
		GuildMember temp;
		while(i < this.memberList.size()) {
			j = i;
			while(j < this.memberList.size()) {
				if(this.memberList.get(i).getRank().getOrder() > this.memberList.get(i).getRank().getOrder()) {
					temp = this.memberList.get(j);
					this.memberList.set(j, this.memberList.get(i));
					this.memberList.set(i, temp);
				}
				j++;
			}
			i++;
		}
	}
	
	public void sortMemberByLevel() {
		int i = 0;
		int j = 0;
		GuildMember temp;
		while(i < this.memberList.size()) {
			j = i;
			while(j < this.memberList.size()) {
				if(this.memberList.get(i).getLevel() > this.memberList.get(i).getLevel()) {
					temp = this.memberList.get(j);
					this.memberList.set(j, this.memberList.get(i));
					this.memberList.set(i, temp);
				}
				j++;
			}
			i++;
		}
	}
	
	public void sortMemberByNote() {
		int i = 0;
		int j = 0;
		GuildMember temp;
		while(i < this.memberList.size()) {
			j = i;
			while(j < this.memberList.size()) {
				if(this.memberList.get(i).getNote().compareTo(this.memberList.get(j).getNote()) > 0) {
					temp = this.memberList.get(j);
					this.memberList.set(j, this.memberList.get(i));
					this.memberList.set(i, temp);
				}
				j++;
			}
			i++;
		}
	}*/
}

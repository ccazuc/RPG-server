package net.game.guild;

import java.util.ArrayList;
import java.util.HashMap;

import net.Server;
import net.command.player.CommandGuild;
import net.game.unit.Player;

public class Guild {

	private final int id;
	private int leader_id;
	private final String name;
	private String information;
	private String motd;
	private boolean isBeingDeleted;
	private final ArrayList<GuildMember> memberList;
	private final HashMap<Integer, GuildMember> memberMap;
	private final ArrayList<GuildRank> rankList;
	private final ArrayList<GuildEvent> eventList;
	
	public final static int MEMBER_NOTE_MAX_LENGTH = 50;
	public final static int MEMBER_OFFICER_NOTE_MAX_LENGTH = 50;
	public final static int MOTD_MAX_LENGTH = 200;
	public final static int INFORMATION_MAX_LENGTH = 300;
	public final static int GUILD_MASTER_PERMISSION = 32767;
	
	public Guild(int id, int leader_id, String name, String information, String motd, ArrayList<GuildMember> memberList, ArrayList<GuildRank> rankList) {
		this.memberMap = new HashMap<Integer, GuildMember>();
		this.eventList = new ArrayList<GuildEvent>();
		this.information = information;
		this.memberList = memberList;
		this.leader_id = leader_id;
		this.rankList = rankList;
		this.name = name;
		this.motd = motd;
		this.id = id;
		initMemberMap();
	}
	
	public void removeMemberOnDelete() {
		int i = this.memberList.size();
		Player player;
		while(--i >= 0) {
			if((player = Server.getInGameCharacter(this.memberList.get(i).getId())) != null) {
				player.setGuild(null);
			}
		}
	}
	
	public GuildMember getMember(int id) {
		return this.memberMap.get(id);
	}
	
	public void addEvent(GuildEvent event) {
		this.eventList.add(event);
	}
	
	public void addMember(GuildMember member) {
		if(this.isBeingDeleted) {
			System.out.println("Tried to add member whereas the guild is being deleted");
			return;
		}
		this.memberList.add(member);
		this.memberMap.put(member.getId(), member);
		CommandGuild.notifyNewMember(this, member);
		GuildMgr.addMemberInDB(this, member.getId());
	}
	
	public void memberKicked(int officerID, int removedID, String name) {
		CommandGuild.notifyKickedMember(this, this.memberMap.get(removedID), name);
		int i = this.memberList.size();
		while(--i >= 0) {
			if(this.memberList.get(i).getId() == removedID) {
				this.memberList.remove(i);
				break;
			}
		}
		this.memberMap.remove(removedID);
		GuildMgr.memberKicked(this, officerID, removedID);
	}
	
	public void memberLeft(int removedID) {
		//TODO: left the guild
		int i = this.memberList.size();
		while(--i >= 0) {
			if(this.memberList.get(i).getId() == removedID) {
				this.memberList.remove(i);
				break;
			}
		}
		this.memberMap.remove(removedID);
		GuildMgr.memberLeft(this, removedID);
	}
	
	public void setGuildBeingDeleted() {
		this.isBeingDeleted = true;
	}
	
	public boolean isBeingDeleted() {
		return this.isBeingDeleted;
	}
	
	public ArrayList<GuildEvent> getEventList() {
		return this.eventList;
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
		int i = this.rankList.size();
		while(--i >= 0) { 
			if(this.rankList.get(i).getOrder() == order) {
				return this.rankList.get(i);
			}
		}
		return null;
	}
	
	private void initMemberMap() {
		int i = this.memberList.size();
		while(--i >= 0) {
			this.memberMap.put(this.memberList.get(i).getId(), this.memberList.get(i));
		}
	}
	
	public void setRankPermission(int rank_order, int permission, String name) {
		int i = this.rankList.size();
		while(--i >= 0) {
			if(this.rankList.get(i).getOrder() == rank_order) {
				this.rankList.get(i).setPermission(permission);
				this.rankList.get(i).setName(name);
				GuildMgr.updatePermission(this, rank_order, permission, name);
				return;
			}
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

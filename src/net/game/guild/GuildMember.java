package net.game.guild;

import net.game.ClassType;

public class GuildMember {

	private int id;
	private String name;
	private int level;
	private GuildRank rank;
	private boolean isOnline;
	private String note = "";
	private String officer_note = "";
	private ClassType classType;
	
	public GuildMember(int id, String name, int level, GuildRank rank, boolean isOnline, String note, String officer_note, ClassType classType) {
		this.id = id;
		this.name = name;
		this.level = level;
		this.rank = rank;
		this.isOnline = isOnline;
		this.note = note;
		this.officer_note = officer_note;
		this.classType = classType;
	}
	
	public int getId() {
		return this.id;
	}
	
	public ClassType getClassType() {
		return this.classType;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public void setRank(GuildRank rank) {
		this.rank = rank;
	}
	
	public GuildRank getRank() {
		return this.rank;
	}
	
	public void setOnlineStatus(boolean we) {
		this.isOnline = we;
	}
	
	public boolean isOnline() {
		return this.isOnline;
	}
	
	public void setNote(String note) {
		this.note = note;
	}
	
	public String getNote() {
		return this.note;
	}
	
	public void setOfficerNote(String officer_note) {
		this.officer_note = officer_note;
	}
	
	public String getOfficerNote() {
		return this.officer_note;
	}
}

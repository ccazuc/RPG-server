package net.game.guild;

public class GuildRank {

	private int order;
	private int permission;
	private boolean[] permissionList;
	private String name;
	
	//Permission index in the table
	public final static int CAN_LISTEN_GUILD_CHANNEL = 0;
	public final static int CAN_LISTEN_OFFICER_CHANNEL = 1;
	public final static int CAN_PROMOTE = 2;
	public final static int CAN_INVITE_MEMBER = 3;
	public final static int CAN_SET_MOTD = 4;
	public final static int CAN_SEE_OFFICER_NOTE = 5;
	public final static int CAN_MODIFY_GUILD_INFORMATION = 6;
	public final static int CAN_TALK_GUILD_CHANNEL = 7;
	public final static int CAN_TALK_OFFICER_CHANNEL = 8;
	public final static int CAN_DEMOTE = 9;
	public final static int CAN_KICK_MEMBER = 10;
	public final static int CAN_EDIT_PUBLIC_NOTE = 11;
	public final static int CAN_EDIT_OFFICER_NOTE = 12;
	public final static int CAN_TAKE_GOLD = 13;
	public final static int CAN_USE_GOLD_REPARATION = 14;
	
	public GuildRank(int order, int permission, String name) {
		this.order = order;
		this.permission = permission;
		this.name = name;
		this.permissionList = new boolean[15];
	}
	
	private void parsePermission() {
		int i = 0;
		while(i < this.permissionList.length) {
			if((this.permission & (1 << i)) != 0) {
				this.permissionList[i] = true;
			}
			i++;
		}
	}
	
	public boolean canInvitePlayer() {
		return this.permissionList[CAN_INVITE_MEMBER];
	}
	
	public boolean canSeeOfficerNote() {
		return this.permissionList[CAN_SEE_OFFICER_NOTE];
	}
	
	public int getOrder() {
		return this.order;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}
	
	public int getPermission() {
		return this.permission;
	}
	
	public void setPermission(int permission) {
		if(this.permission != permission) {
			this.permission = permission;
			parsePermission();
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}

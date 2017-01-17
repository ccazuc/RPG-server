package net.game.manager;

import java.util.ArrayList;
import java.util.HashMap;

import net.config.ConfigMgr;
import net.game.chat.ChatChannel;
import net.game.unit.Faction;
import net.game.unit.Player;

public class ChannelMgr {

	private final HashMap<String, ChatChannel> channelMap;
	public final static byte MAXIMUM_CHANNEL_JOINED = 10;
	private final static HashMap<Byte, ChannelMgr> channelMgrMap = new HashMap<Byte, ChannelMgr>();
	
	public ChannelMgr() {
		this.channelMap = new HashMap<String, ChatChannel>();
	}
	
	public static void initChannelMgr() {
		if(ConfigMgr.ALLOW_INTERFACTION_CHANNEL) {
			ChannelMgr mgr = new ChannelMgr();
			channelMgrMap.put(Faction.HORDE.getValue(), mgr);
			channelMgrMap.put(Faction.ALLIANCE.getValue(), mgr);
		}
		else {
			channelMgrMap.put(Faction.HORDE.getValue(), new ChannelMgr());
			channelMgrMap.put(Faction.ALLIANCE.getValue(), new ChannelMgr());
		}
	}
	
	public static ChannelMgr getChannelMgr(Faction faction) {
		return channelMgrMap.get(faction.getValue());
	}
	
	public ArrayList<Integer> getPlayerList(String channelID) {
		return this.channelMap.get(channelID).getPlayerList();
	}
	
	public boolean checkPassword(String channelID, String password) {
		if(!this.channelMap.containsKey(channelID)) {
			return true;
		}
		return this.channelMap.get(channelID).passwordMatches(password);
	}
	
	public void addPlayer(String channelID, String password, Player player) {
		if(this.channelMap.containsKey(channelID)) {
			this.channelMap.get(channelID).addPlayer(player);
		}
		else {
			this.channelMap.put(channelID, new ChatChannel(channelID, password, player.getUnitID()));
			this.channelMap.get(channelID).addPlayer(player);
		}
	}
	
	public boolean removePlayer(String channelID, Player player) {
		if(!this.channelMap.containsKey(channelID)) {
			return false;
		}
		return this.channelMap.get(channelID).removePlayer(player.getUnitID());
	}
	
	public boolean playerHasJoinChannel(String channelID, Player player) {
		if(!this.channelMap.containsKey(channelID)) {
			return false;
		}
		return this.channelMap.get(channelID).playerHasJoined(player.getUnitID());
	}
	
	public boolean isMuted(String channelID, Player player) {
		if(!this.channelMap.containsKey(channelID)) {
			return true;
		}
		return this.channelMap.get(channelID).isMuted(player.getUnitID());
	}

	public void mutePlayer(String channelID, Player player) {
		this.channelMap.get(channelID).addMute(player.getUnitID());
	}
	
	public boolean isModerator(String channelID, Player player) {
		if(!this.channelMap.containsKey(channelID)) {
			return true;
		}
		return this.channelMap.get(channelID).isModerator(player.getUnitID());
	}

	public boolean isLeader(String channelID, Player player) {
		if(!this.channelMap.containsKey(channelID)) {
			return true;
		}
		return this.channelMap.get(channelID).isLeader(player.getUnitID());
	}
	
	public void setLeader(String channelID, Player player) {
		this.channelMap.get(channelID).setLeader(player, true);
	}

	public boolean isBanned(String channelID, Player player) {
		if(!this.channelMap.containsKey(channelID)) {
			return true;
		}
		return this.channelMap.get(channelID).isBanned(player.getUnitID());
	}
	
	public void banPlayer(String channelID, Player player) {
		this.channelMap.get(channelID).addBan(player.getUnitID());
	}
	
	public void setPassword(String channelID, String password) {
		this.channelMap.get(channelID).setPassword(password);
	}
}

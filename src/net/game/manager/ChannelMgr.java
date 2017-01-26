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
	
	public static String formatChannelName(String str) {
		if(str.length() == 0) {
			return null;
		}
		char[] table = new char[str.length()];
		int i = -1;
		char c;
		while(++i < str.length()) {
			c = str.charAt(i);
			if(c == ' ') {
				return null;
			}
			if(c >= 'A' && c <= 'Z') {
				c+= 32;
			}
			table[i] = c;
		}
		return new String(table);
	}
	
	public boolean channelExists(String channelID) {
		return this.channelMap.containsKey(channelID);
	}
	
	public void createChannel(String channelID, String password, Player player) {
		this.channelMap.put(channelID, new ChatChannel(channelID, password, player.getUnitID()));
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
			createChannel(channelID, password, player);
			this.channelMap.get(channelID).addPlayer(player);
		}
		player.joinedChannel(channelID);
	}
	
	public boolean removePlayer(String channelID, Player player) {
		if(!this.channelMap.containsKey(channelID)) {
			return false;
		}
		if(this.channelMap.get(channelID).removePlayer(player.getUnitID())) {
			player.leftChannel(channelID);
			return true;
		}
		return false;
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
	
	public void setModerator(String channelID, Player player, boolean isModerator) {
		if(isModerator) {
			this.channelMap.get(channelID).addModerator(player.getUnitID());
		}
		else {
			this.channelMap.get(channelID).removeModerator(player.getUnitID());
		}
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
	
	public int getLeaderID(String channelID) {
		return this.channelMap.get(channelID).getLeaderID();
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

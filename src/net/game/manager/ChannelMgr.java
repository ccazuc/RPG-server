package net.game.manager;

import java.util.ArrayList;
import java.util.HashMap;

import net.game.chat.ChatChannel;
import net.game.unit.Player;

public class ChannelMgr {

	private final static HashMap<String, ChatChannel> channelMap = new HashMap<String, ChatChannel>();
	public final static byte MAXIMUM_CHANNEL_JOINED = 10;
	
	public static ArrayList<Integer> getPlayerList(String channelID) {
		return channelMap.get(channelID).getPlayerList();
	}
	
	public static boolean checkPassword(String channelID, String password) {
		if(!channelMap.containsKey(channelID)) {
			return true;
		}
		return channelMap.get(channelID).passwordMatches(password);
	}
	
	public static void addPlayer(String channelID, String password, Player player) {
		if(channelMap.containsKey(channelID)) {
			channelMap.get(channelID).addPlayer(player);
		}
		else {
			channelMap.put(channelID, new ChatChannel(channelID, password, player.getUnitID()));
			channelMap.get(channelID).addPlayer(player);
		}
	}
	
	public static boolean removePlayer(String channelID, Player player) {
		if(!channelMap.containsKey(channelID)) {
			return false;
		}
		return channelMap.get(channelID).removePlayer(player.getUnitID());
	}
	
	public static boolean playerHasJoinChannel(String channelID, Player player) {
		if(!channelMap.containsKey(channelID)) {
			return false;
		}
		return channelMap.get(channelID).playerHasJoined(player.getUnitID());
	}
	
	public static boolean isMuted(String channelID, Player player) {
		if(!channelMap.containsKey(channelID)) {
			return true;
		}
		return channelMap.get(channelID).isMuted(player.getUnitID());
	}

	public static boolean isModerator(String channelID, Player player) {
		if(!channelMap.containsKey(channelID)) {
			return true;
		}
		return channelMap.get(channelID).isModerator(player.getUnitID());
	}

	public static boolean isLeader(String channelID, Player player) {
		if(!channelMap.containsKey(channelID)) {
			return true;
		}
		return channelMap.get(channelID).isLeader(player.getUnitID());
	}

	public static boolean isBanned(String channelID, Player player) {
		if(!channelMap.containsKey(channelID)) {
			return true;
		}
		return channelMap.get(channelID).isBanned(player.getUnitID());
	}
	
	public static void banPlayer(String channelID, Player player) {
		channelMap.get(channelID).addBan(player.getUnitID());
	}
	
	public static void setPassword(String channelID, String password) {
		channelMap.get(channelID).setPassword(password);
	}
}

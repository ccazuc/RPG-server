package net.game.manager;

import java.util.ArrayList;
import java.util.HashMap;

import net.game.unit.Player;

public class ChannelMgr {

	private final static HashMap<String, ArrayList<Integer>> channelMap = new HashMap<String, ArrayList<Integer>>();
	public final static byte MAXIMUM_CHANNEL_JOINED = 10;
	
	public static ArrayList<Integer> getPlayerList(String channelID) {
		return channelMap.get(channelID);
	}
	
	public static void addPlayer(String channelID, Player player) {
		if(channelMap.containsKey(channelID)) {
			channelMap.get(channelID).add(player.getUnitID());
		}
		else {
			channelMap.put(channelID, new ArrayList<Integer>());
			channelMap.get(channelID).add(player.getUnitID());
		}
	}
	
	public static boolean removePlayer(String channelID, Player player) {
		if(!channelMap.containsKey(channelID)) {
			return false;
		}
		int unitID = player.getUnitID();
		ArrayList<Integer> list = channelMap.get(channelID);
		int i = list.size();
		while(--i >= 0) {
			if(list.get(i) == unitID) {
				list.remove(i);
				return true;
			}
		}
		return false;
	}
	
	public static boolean playerHasJoinChannel(String channelID, Player player) {
		if(!channelMap.containsKey(channelID)) {
			return false;
		}
		int unitID = player.getUnitID();
		ArrayList<Integer> list = channelMap.get(channelID);
		int i = list.size();
		while(--i >= 0) {
			if(list.get(i) == unitID) {
				return true;
			}
		}
		return false;
	}
}

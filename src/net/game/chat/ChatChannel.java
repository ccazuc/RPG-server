package net.game.chat;

import java.util.ArrayList;
import java.util.HashMap;

import net.game.unit.Player;

public class ChatChannel {

	private final HashMap<Integer, Player> playerMap;
	private final ArrayList<Integer> playerList;
	private final ArrayList<Integer> moderatorList;
	private final ArrayList<Integer> muteList;
	private final ArrayList<Integer> banList;
	private final String name;
	private String password;
	private int leaderID;
	
	public ChatChannel(String name, String password, int leaderID) {
		this.name = name;
		this.password = password;
		this.leaderID = leaderID;
		this.playerList = new ArrayList<Integer>();
		this.playerMap = new HashMap<Integer, Player>();
		this.moderatorList = new ArrayList<Integer>();
		this.muteList = new ArrayList<Integer>();
		this.banList = new ArrayList<Integer>();
	}
	
	public void setLeader(int leaderID) {
		this.leaderID = leaderID;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean passwordMatches(String password) {
		return this.password == null || this.password.length() == 0 || this.password.equals(password);
	}
	
	public void addPlayer(Player player) {
		this.playerList.add(player.getUnitID());
		this.playerMap.put(player.getUnitID(), player);
	}
	
	public boolean removePlayer(int unitID) {
		this.playerMap.remove(unitID);
		int i = this.playerList.size();
		while(--i >= 0) {
			if(this.playerList.get(i) == unitID) {
				this.playerList.remove(i);
				return true;
			}
		}
		return false;
	}
	
	public boolean isBanned(int unitID) {
		int i = this.banList.size();
		while(--i >= 0) {
			if(this.banList.get(i) == unitID) {
				return true;
			}
		}
		return false;
	}
	
	public boolean removeBan(int unitID) {
		int i = this.banList.size();
		while(--i >= 0) {
			if(this.banList.get(i) == unitID) {
				this.banList.remove(i);
				return true;
			}
		}
		return false;
	}
	
	public void addBan(int unitID) {
		this.banList.add(unitID);
	}
	
	public boolean isMuted(int unitID) {
		int i = this.muteList.size();
		while(--i >= 0) {
			if(this.muteList.get(i) == unitID) {
				return true;
			}
		}
		return false;
	}
	
	public boolean removeMute(int unitID) {
		int i = this.muteList.size();
		while(--i >= 0) {
			if(this.muteList.get(i) == unitID) {
				this.muteList.remove(i);
				return true;
			}
		}
		return false;
	}
	
	public void addMute(int unitID) {
		this.muteList.add(unitID);
	}
	
	public boolean isModerator(int unitID) {
		int i = this.moderatorList.size();
		while(--i >= 0) {
			if(this.moderatorList.get(i) == unitID) {
				return true;
			}
		}
		return false;
	}
	
	public boolean removeModerator(int unitID) {
		int i = this.moderatorList.size();
		while(--i >= 0) {
			if(this.moderatorList.get(i) == unitID) {
				this.moderatorList.remove(i);
				return true;
			}
		}
		return false;
	}
	
	public void addModerator(int unitID) {
		this.moderatorList.add(unitID);
	}
	
	public boolean isLeader(int unitID) {
		return this.leaderID == unitID;
	}
	
	public boolean playerHasJoined(int unitID) {
		return this.playerMap.containsKey(unitID);
	}
	
	public ArrayList<Integer> getPlayerList() {
		return this.playerList;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public int getLeaderID() {
		return this.leaderID;
	}
}

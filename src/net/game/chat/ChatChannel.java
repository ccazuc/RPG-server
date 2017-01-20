package net.game.chat;

import java.util.ArrayList;
import java.util.HashMap;

import net.Server;
import net.command.chat.CommandChannel;
import net.game.unit.Player;

public class ChatChannel {

	private final HashMap<Integer, Player> playerMap;
	private final ArrayList<Integer> playerList;
	private final ArrayList<Integer> moderatorList;
	private final ArrayList<Integer> muteList;
	private final ArrayList<Integer> banList;
	private final String channelID;
	private String password;
	private int leaderID;
	
	public ChatChannel(String channelID, String password, int leaderID) {
		this.channelID = channelID;
		if(password == null) {
			this.password = "";
		}
		else {
			this.password = password;
		}
		this.leaderID = leaderID;
		this.playerList = new ArrayList<Integer>();
		this.playerMap = new HashMap<Integer, Player>();
		this.moderatorList = new ArrayList<Integer>();
		this.muteList = new ArrayList<Integer>();
		this.banList = new ArrayList<Integer>();
	}
	
	public void setLeader(Player player, boolean chatMessage) {
		this.leaderID = player.getUnitID();
		CommandChannel.notifyPlayerLeader(this.channelID, player, chatMessage);
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean passwordMatches(String password) {
		return this.password.equals(password);
	}
	
	public void addPlayer(Player player) {
		if(this.playerList.size() == 0) {
			setLeader(player, false);
		}
		this.playerList.add(player.getUnitID());
		this.playerMap.put(player.getUnitID(), player);
	}
	
	public boolean designNewLeader() {
		int i = -1;
		Player player = null;
		while(++i < this.moderatorList.size()) {
			if((player = Server.getInGameCharacter(this.moderatorList.get(i))) != null) {
				setLeader(player, true);
				return true;
			}
		}
		i = -1;
		while(++i < this.playerList.size()) {
			if((player = Server.getInGameCharacter(this.playerList.get(i))) != null) {
				setLeader(player, true);
				return true;
			}
		}
		this.leaderID = -1;
		return false;
	}
	
	public boolean removePlayer(int unitID) {
		this.playerMap.remove(unitID);
		int i = this.playerList.size();
		while(--i >= 0) {
			if(this.playerList.get(i) == unitID) {
				this.playerList.remove(i);
				if(isLeader(unitID)) {
					designNewLeader();
				}
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
	
	public String getChannelID() {
		return this.channelID;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public int getLeaderID() {
		return this.leaderID;
	}
}

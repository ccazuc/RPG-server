package net.game;

public class Party {

	private Player[] playerTable;
	private Player partyLeader;
	
	public Party(Player leader, Player member) {
		this.playerTable = new Player[5];
		this.playerTable[0] = leader;
		this.playerTable[1] = member;
		this.partyLeader = leader;
	}
	
	public boolean isPartyLeader(Player player) {
		return player.getCharacterId() == this.playerTable[0].getCharacterId();
	}
	
	public boolean addMember(Player player) {
		int i = 0;
		while(i < this.playerTable.length) {
			if(this.playerTable[i] == null) {
				this.playerTable[i] = player;
				return true;
			}
			i++;
		}
		return false;
	}
	
	public int getNumberMembers() {
		int i = 0;
		int count = 0;
		while(i < this.playerTable.length) {
			if(this.playerTable[i] != null) {
				count++;
			}
			i++;
		}
		return count;
	}
	
	public void removeMember(Player player) {
		int i = 0;
		while(i < this.playerTable.length) {
			if(this.playerTable[i] != null && this.playerTable[i].getCharacterId() == player.getCharacterId()) {
				this.playerTable[i] = null;
			}
			i++;
		}
	}
	
	public void updateMemberPosition() {
		int i = 0;
		int j = 0;
		while(i < this.playerTable.length) {
			if(this.playerTable[i] == null) {
				j = i;
				while(j < this.playerTable.length) {
					if(j != i && this.playerTable[j] != null) {
						this.playerTable[i] = this.playerTable[j];
						this.playerTable[j] = null;
					}
					j++;
				}
			}
			i++;
		}
	}
	
	public Player[] getPlayerList() {
		return this.playerTable;
	}
}

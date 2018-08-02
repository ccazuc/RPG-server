package net.game;

import net.Server;
import net.game.unit.Player;

public class Party { //TODO: replace Player[] by int[] with playerId

	public final static int MAXIMUM_PARTY_SIZE = 5;
	private int[] playerTable;
	private int partyLeaderId;
	
	public Party(Player leader, Player member)
	{
		this.playerTable = new int[MAXIMUM_PARTY_SIZE];
		this.playerTable[0] = leader.getUnitID();
		this.playerTable[1] = member.getUnitID();
		this.partyLeaderId = leader.getUnitID();
	}
	
	public boolean isPartyLeader(Player player)
	{
		return (player.getUnitID() == this.partyLeaderId);
	}
	
	public int getLeaderId()
	{
		return (this.partyLeaderId);
	}
	
	public Player getPlayer(int i)
	{
		return (Server.getInGameCharacter(this.playerTable[i]));
	}
	
	public boolean addMember(Player player)
	{
		int i = 0;
		while (i < this.playerTable.length)
		{
			if (this.playerTable[i] == 0)
			{
				this.playerTable[i] = player.getUnitID();
				return (true);
			}
			i++;
		}
		return (false);
	}
	
	public int getNumberMembers()
	{
		int i = -1;
		int count = 0;
		while (++i < this.playerTable.length)
			if (Server.getInGameCharacter(this.playerTable[i]) != null)
				count++;
		return (count);
	}
	
	public void removeMember(Player player)
	{
		int i = 0;
		while (i < this.playerTable.length)
		{
			if (this.playerTable[i] == player.getUnitID())
				this.playerTable[i] = 0;
			i++;
		}
	}
	
	public void updateMemberPosition()
	{
		int i = 0;
		int j = 0;
		while (i < this.playerTable.length)
		{
			if (this.playerTable[i] == 0)
			{
				j = i;
				while (j < this.playerTable.length)
				{
					if (j != i && this.playerTable[j] != 0)
					{
						this.playerTable[i] = this.playerTable[j];
						this.playerTable[j] = 0;
					}
					j++;
				}
			}
			i++;
		}
	}
	
	public void setLeader(Player player)
	{
		this.partyLeaderId = player.getUnitID();
	}
	
	public int[] getPlayerList()
	{
		return (this.playerTable);
	}
}

package net.game;
import java.util.ArrayList;

import net.Server;
import net.command.player.CommandParty;
import net.game.callback.StaticCallbackMgr;
import net.game.premade_group.PremadeGroup;
import net.game.premade_group.PremadeGroupApplication;
import net.game.unit.Player;

public class Party {

	public final static int MAXIMUM_PARTY_SIZE = 5;
	private int[] playerTable;
	private int partyLeaderId;
	private int numberMembers;
	private PremadeGroup premadeGroup;
	private final ArrayList<PremadeGroupApplication> applicationList;
	
	public Party(Player leader, Player member)
	{
		this.playerTable = new int[MAXIMUM_PARTY_SIZE];
		this.playerTable[0] = leader.getUnitID();
		this.playerTable[1] = member.getUnitID();
		this.partyLeaderId = leader.getUnitID();
		this.premadeGroup = null;
		this.numberMembers = 2;
		this.applicationList = new ArrayList<PremadeGroupApplication>();
	}
	
	public void addPremadeGroupApplication(PremadeGroupApplication application)
	{
		this.applicationList.add(application);
	}
	
	public void removePremadeGroupApplication(PremadeGroupApplication application)
	{
		int i = -1;
		while (++i < this.applicationList.size())
			if (this.applicationList.get(i).getId() == application.getId())
			{
				this.applicationList.remove(i);
				return;
			}
	}
	
	public int getNumberMembers()
	{
		return (this.numberMembers);
	}
	
	public ArrayList<PremadeGroupApplication> getPremadeGroupApplicationList()
	{
		return (this.applicationList);
	}

	public PremadeGroup getPremadeGroup()
	{
		return (this.premadeGroup);
	}
	
	public void setPremadeGroup(PremadeGroup group)
	{
		this.premadeGroup = group;
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
	
	public void disband()
	{
		int i = -1;
		Player tmp = null;
		while (++i < this.playerTable.length)
		{
			if (this.playerTable[i] != 0 && (tmp = Server.getInGameCharacter(this.playerTable[i])) != null)
			{
				CommandParty.sendPartyLeft(tmp);
				tmp.setParty(null);
			}
		}
		StaticCallbackMgr.onPartyDisbanded(this);
	}
	
	public boolean addMember(Player player)
	{
		int i = 0;
		while (i < this.playerTable.length)
		{
			if (this.playerTable[i] == 0)
			{
				this.playerTable[i] = player.getUnitID();
				++this.numberMembers;
				return (true);
			}
			i++;
		}
		return (false);
	}
	
	public int getNumberOnlineMembers()
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
		int i = -1;
		while (++i < this.playerTable.length)
		{
			if (this.playerTable[i] == player.getUnitID())
			{
				this.playerTable[i] = 0;
				--this.numberMembers;
				return;
			}
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
		StaticCallbackMgr.onPartyLeaderChange(player);
	}
	
	public int[] getPlayerList()
	{
		return (this.playerTable);
	}
}

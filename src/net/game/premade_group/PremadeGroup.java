package net.game.premade_group;

import java.util.ArrayList;

import net.Server;
import net.game.Party;
import net.game.unit.Player;

public class PremadeGroup {

	private String leaderName;
	private int leaderId;
	private String name;
	private String description;
	private final long id;
	private final PremadeGroupType type;
	private final ArrayList<PremadeGroupApplication> applicationList;
	private int requiredLevel;
	private Party party;
	private boolean isAutoAccept;
	private final long createTimer;
	
	public PremadeGroup(Player player, long id, String name, String description, PremadeGroupType type, int requiredLevel)
	{
		this.party = player.getParty();
		this.leaderName = player.getName();
		this.leaderId = player.getUnitID();
		this.id = id;
		this.name = name;
		this.description = description;
		this.type = type;
		this.applicationList = new ArrayList<PremadeGroupApplication>();
		this.requiredLevel = requiredLevel;
		this.createTimer = Server.getLoopTickTimer();
	}
	
	public final void addApplication(PremadeGroupApplication application)
	{
		this.applicationList.add(application);
	}
	
	public final PremadeGroupApplication removeApplication(long applicationId)
	{
		int i = -1;
		while (++i < this.applicationList.size())
			if (this.applicationList.get(i).getId() == applicationId)
			{
				PremadeGroupApplication tmp = this.applicationList.get(i);
				this.applicationList.remove(i);
				return (tmp);
			}
		return (null);
	}
	
	public final PremadeGroupApplication getApplication(long applicationId)
	{
		int i = -1;
		while (++i < this.applicationList.size())
			if (this.applicationList.get(i).getId() == applicationId)
				return (this.applicationList.get(i));
		return (null);
	}
	
	public final void updateLeader(Player player)
	{
		this.leaderName = player.getName();
		this.leaderId = player.getUnitID();
	}
	
	public final void setAutoAccept(boolean we)
	{
		this.isAutoAccept = we;
	}
	
	public final boolean getIsAutoAccept()
	{
		return (this.isAutoAccept);
	}
	
	public final int getLeaderId()
	{
		return (this.leaderId);
	}
	
	public final String getLeaderName()
	{
		return (this.leaderName);
	}
	
	public final void updateParty(Party party)
	{
		this.party = party;
	}
	
	public final long getCreateTimer()
	{
		return (this.createTimer);
	}
	
	public final int getRequiredLevel()
	{
		return (this.requiredLevel);
	}
	
	public final long getId()
	{
		return (this.id);
	}
	
	public final Party getParty()
	{
		return (this.party);
	}
	
	public final String getName()
	{
		return (this.name);
	}
	
	public final String getDescription()
	{
		return (this.description);
	}
	
	public final PremadeGroupType getType()
	{
		return (this.type);
	}
	
	public final ArrayList<PremadeGroupApplication> getApplicationList()
	{
		return (this.applicationList);
	}
}

package net.game.premade_group;

import java.util.ArrayList;

import net.game.Party;
import net.game.unit.Player;

public class PremadeGroup {

	private String name;
	private String description;
	private final long id;
	private final PremadeGroupType type;
	private final ArrayList<PremadeGroupApplication> applicationList;
	private int requiredLevel;
	private Party party;
	
	public PremadeGroup(Player player, long id, String name, String description, PremadeGroupType type, int requiredLevel)
	{
		this.party = player.getParty();
		this.id = id;
		this.name = name;
		this.description = description;
		this.type = type;
		this.applicationList = new ArrayList<PremadeGroupApplication>();
		this.requiredLevel = requiredLevel;
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

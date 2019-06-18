package net.game.premade_group;

import java.util.ArrayList;
import java.util.HashMap;

import net.Server;
import net.command.player.CommandPremadeGroup;
import net.game.Party;
import net.game.unit.Player;

public class PremadeGroupFactionMgr {

	private final HashMap<PremadeGroupType, ArrayList<PremadeGroup>> groupMap = new HashMap<PremadeGroupType, ArrayList<PremadeGroup>>();
	private final HashMap<Long, PremadeGroupApplication> applicationMap = new HashMap<Long, PremadeGroupApplication>();
	private long currentGroupId;
	private long currentApplicationId;
	
	public PremadeGroupFactionMgr()
	{
		int i = -1;
		while (++i < PremadeGroupType.values().length)
			this.groupMap.put(PremadeGroupType.values()[i], new ArrayList<PremadeGroup>());
	}
	
	public ArrayList<PremadeGroup> getGroupList(PremadeGroupType type)
	{
		return (this.groupMap.get(type));
	}
	
	public void addPremadeGroup(Player player, String name, String description, PremadeGroupType type, int requiredLevel)
	{
		PremadeGroup group = new PremadeGroup(player, generatePremadeGroupId(), name, description, type, requiredLevel);
		this.groupMap.get(type).add(group);
		if (player.getParty() != null)
		{
			int i = -1;
			player.getParty().setPremadeGroup(group);
			while (++i < player.getParty().getPlayerList().length)
			{
				Player tmp = player.getParty().getPlayer(i);
				tmp.setPremadeGroup(group);
				CommandPremadeGroup.sendGroupCreated(tmp);
			}
		}
		else
		{
			player.setPremadeGroup(group);
			CommandPremadeGroup.sendGroupCreated(player);
		}
	}
	
	public void delistPremadeGroup(Player player)
	{
		int i = -1;
		ArrayList<PremadeGroup> groupList = this.groupMap.get(player.getPremadeGroup().getType());
		PremadeGroup group = null;
		while (++i < groupList.size())
			if (groupList.get(i).getId() == player.getPremadeGroup().getId())
			{
				group = groupList.get(i);
				groupList.remove(i);
				break;
			}
		if (group == null)
			return;
		ArrayList<PremadeGroupApplication> applicationList = group.getApplicationList();
		i = -1;
		while (++i < applicationList.size())
			CommandPremadeGroup.sendGroupDelisted(group, applicationList.get(i));
		if (group.getParty() != null)
		{
			i = -1;
			Player tmp = null;
			group.getParty().setPremadeGroup(null);
			while (++i < group.getParty().getPlayerList().length)
			{
				if ((tmp = group.getParty().getPlayer(i)) != null)
				{
					CommandPremadeGroup.sendGroupDelisted(tmp);
					tmp.setPremadeGroup(null);
				}
			}
		}
		else
		{
			CommandPremadeGroup.sendGroupDelisted(player);
			player.setPremadeGroup(null);
		}
	}
	
	public void acceptApplication(Player player, long applicationId)
	{
		PremadeGroup group = player.getPremadeGroup();
		PremadeGroupApplication application = group.getApplication(applicationId);
		if (application == null)
			return;
		if (group.getParty() == null)
		{
			
		}
		else
		{
			if (application.getParty() != null)
			{
				
			}
			else
			{
				
			}
		}
	}
	
	public void refuseApplication(Player player, long applicationId)
	{
		PremadeGroup group = player.getPremadeGroup();
		PremadeGroupApplication application = group.getApplication(applicationId);
		if (application == null)
			return;
		if (group.getParty() == null)
		{
			//CommandPremadeGroup.sendApplicationRefused(player);
		}
		else
		{
			
		}
		this.applicationMap.remove(application.getId());
	}
	
	public boolean checkPremadeGroupConditionsMet(PremadeGroup group, Player player)
	{
		if (player.getParty() != null)
		{
			int i = -1;
			while (++i < player.getParty().getPlayerList().length)
				if (player.getParty().getPlayer(i) != null && player.getParty().getPlayer(i).getLevel() < group.getRequiredLevel())
				{
					//TODO: Send red alert level condition not met
					return (false);
				}
		}
		else
		{
			if (player.getLevel() < group.getRequiredLevel())
			{
				//TODO: Send red alert level condition not met
				return (false);
			}
		}
		return (true);
	}
	
	public void addApplication(Player player, PremadeGroupType type, long groupId, String description)
	{
		PremadeGroup group = getPremadeGroup(type, groupId);
		if (group == null)
			return;
		if (!checkPremadeGroupConditionsMet(group, player))
			return;
		PremadeGroupApplication application = null;
		if (player.getParty() != null)
			application = new PremadeGroupApplication(group, generatePremadeGroupApplicationId(), player.getParty(), description);
		else
			application = new PremadeGroupApplication(group, generatePremadeGroupApplicationId(), player.getUnitID(), description);
		group.addApplication(application);
		this.applicationMap.put(application.getId(), application);
		Player tmp = null;
		if (application.getPlayerList() != null)
		{
			for (int i = 0; i < application.getPlayerList().length; ++i)
				//TODO: CommandPremadeGroup.sendAppliedToAGroup(group, application.getPlayerList()[i]);
				;
		}
		else
			;
			//TODO: CommandPremadeGroup.sendAppliedToAGroup(group, player);
		if (group.getParty() != null)
		{
			for (int i = 0; i < group.getParty().getPlayerList().length; ++i)
				if ((tmp = group.getParty().getPlayer(i)) != null)
					CommandPremadeGroup.sendApplicationReceived(application, tmp);
					
		}
		else
		{
			tmp = Server.getInGameCharacter(group.getLeaderId());
			CommandPremadeGroup.sendApplicationReceived(application, tmp);
		}
	}
	
	public void cancelApplicationOnLogout(Player player)
	{
		if (player.getPremadeGroupApplicationList().size() == 0)
			return;
		for (int i = 0; i < player.getPremadeGroupApplicationList().size(); ++i)
			cancelApplication(player, player.getPremadeGroupApplicationList().size(), false);
		player.clearPremadeGroupApplication();
	}
	
	public void cancelApplication(Player player, long applicationId, boolean shouldSendDataToApplicationClient)
	{
		PremadeGroupApplication application = this.applicationMap.get(applicationId);
		if (application == null)
			return;
		if ((application.getParty() == null && application.getPlayerId() != player.getUnitID()) || (application.getParty() != null && application.getParty().getLeaderId() != player.getUnitID()))
			return;
		this.applicationMap.remove(application.getId());
		PremadeGroup group = application.getPremadeGroup();
		Player tmp = null;
		if (group.removeApplication(applicationId) == null)
			return;
		if (shouldSendDataToApplicationClient)
		{
			if (application.getParty() != null)
			{
				int i = -1;
				while (++i < application.getPlayerList().length)
				{
					tmp = Server.getInGameCharacter(application.getPlayerList()[i]);
					if (tmp == null)
						continue;
					CommandPremadeGroup.sendApplicationCanceled(group, tmp);
				}
			}
			else
				CommandPremadeGroup.sendApplicationCanceled(group, player);
		}
		if (group.getParty() != null)
		{
			int i = -1;
			while (++i < group.getParty().getPlayerList().length)
				if ((tmp = group.getParty().getPlayer(i)) != null)
					CommandPremadeGroup.sendApplicationCanceledFromGroup(application, tmp);
		}
		else
		{
			tmp = Server.getInGameCharacter(group.getLeaderId());
			if (tmp != null)
				CommandPremadeGroup.sendApplicationCanceledFromGroup(application, tmp);
		}
	}
	
	public void removeAllApplicationOnPartyDisband(Party party)
	{
		int i = -1;
		ArrayList<PremadeGroupApplication> applicationList = party.getPremadeGroupApplicationList();
		while (++i < applicationList.size())
			cancelApplication(null, applicationList.get(i).getId(), false);
	}
	
	public PremadeGroup getPremadeGroup(PremadeGroupType type, long groupId)
	{
		ArrayList<PremadeGroup> list = this.groupMap.get(type);
		if (list == null)
			return (null);
		int i = -1;
		while (++i < list.size())
			if (list.get(i).getId() == groupId)
				return (list.get(i));
		return (null);
	}
	
	public long generatePremadeGroupId()
	{
		return (++this.currentGroupId);
	}
	
	public long generatePremadeGroupApplicationId()
	{
		return (++this.currentApplicationId);
	}
}

package net.game.premade_group;

import java.util.ArrayList;
import java.util.HashMap;

import net.Server;
import net.command.player.CommandPremadeGroup;
import net.game.Party;
import net.game.unit.Player;

public class PremadeGroupMgr {

	private final static HashMap<PremadeGroupType, ArrayList<PremadeGroup>> groupMap = new HashMap<PremadeGroupType, ArrayList<PremadeGroup>>();
	private final static HashMap<Long, PremadeGroupApplication> applicationMap = new HashMap<Long, PremadeGroupApplication>();
	public final static long APPLICATION_DURATION = 1000 * 60 * 5;
	private static long currentGroupId;
	private static long currentApplicationId;
	
	public static void initPremadeGroupMap()
	{
		int i = -1;
		while (++i < PremadeGroupType.values().length)
			groupMap.put(PremadeGroupType.values()[i], new ArrayList<PremadeGroup>());
	}
	
	public static void addPremadeGroup(Player player, String name, String description, PremadeGroupType type, int requiredLevel)
	{
		PremadeGroup group = new PremadeGroup(player, generatePremadeGroupId(), name, description, type, requiredLevel);
		groupMap.get(type).add(group);
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
	
	public static void delistPremadeGroup(Player player)
	{
		int i = -1;
		ArrayList<PremadeGroup> groupList = groupMap.get(player.getPremadeGroup().getType());
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
	
	public static void acceptApplication(Player player, long applicationId)
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
	
	public static void refuseApplication(Player player, long applicationId)
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
			
		}
		applicationMap.remove(application.getId());
	}
	
	public static boolean checkPremadeGroupConditionsMet(PremadeGroup group, Player player)
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
	
	public static void addApplication(Player player, PremadeGroupType type, long groupId, String description)
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
		applicationMap.put(application.getId(), application);
		if (application.getPlayerList() != null)
		{
			int i = -1;
			while (++i < application.getPlayerList().length)
				//TODO: CommandPremadeGroup.sendAppliedToAGroup(group, application.getPlayerList()[i]);
				;
		}
		else
			;
			//TODO: CommandPremadeGroup.sendAppliedToAGroup(group, player);
		if (group.getParty() != null)
		{
			int i = -1;
			while (++i < group.getParty().getPlayerList().length)
				if (group.getParty().getPlayer(i) != null)
					;
					//TODO: CommandPremadeGroup.sendAddApplication
		}
		else
			//TODO: CommandPremadeGroup.sendAddApplication
			;
	}
	
	public static void cancelApplication(Player player, long applicationId, boolean shouldSendDataToApplicationClient)
	{
		PremadeGroupApplication application = applicationMap.get(applicationId);
		if (application == null)
			return;
		applicationMap.remove(application.getId());
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
	
	public static void removeAllApplicationOnPartyDisband(Party party)
	{
		int i = -1;
		ArrayList<PremadeGroupApplication> applicationList = party.getPremadeGroupApplicationList();
		while (++i < applicationList.size())
			cancelApplication(null, applicationList.get(i).getId(), false);
	}
	
	public static PremadeGroup getPremadeGroup(PremadeGroupType type, long groupId)
	{
		ArrayList<PremadeGroup> list = groupMap.get(type);
		if (list == null)
			return (null);
		int i = -1;
		while (++i < list.size())
			if (list.get(i).getId() == groupId)
				return (list.get(i));
		return (null);
	}
	
	public static long generatePremadeGroupId()
	{
		return (++currentGroupId);
	}
	
	public static long generatePremadeGroupApplicationId()
	{
		return (++currentApplicationId);
	}
}

package net.game.premade_group;

import java.util.ArrayList;
import java.util.HashMap;

import net.game.unit.Player;

public class PremadeGroupMgr {

	private static HashMap<PremadeGroupType, ArrayList<PremadeGroup>> groupMap = new HashMap<PremadeGroupType, ArrayList<PremadeGroup>>();
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
		player.setPremadeGroup(group);
		//CommandPremadeGroup.sendGroupCreated
	}
	
	public static void removePremadeGroup(Player player)
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
			//TODO: CommandPremadeGroup.sendGroupDisbanded(i)
			;
		if (group.getParty() != null)
		{
			i = -1;
			while (++i < group.getParty().getPlayerList().length)
				if (group.getParty().getPlayer(i) != null)
					//TODO: CommandPremadeGroup.sendGroupDisbanded(i)
					;
		}
		else
			//TODO: CommandPremadeGroup.sendGroupDisbanded
			;
		player.setPremadeGroup(null);
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
			application = new PremadeGroupApplication(generatePremadeGroupApplicationId(), player.getParty(), description);
		else
			application = new PremadeGroupApplication(generatePremadeGroupApplicationId(), player.getUnitID(), description);
		group.addApplication(application);
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
	
	public static void cancelApplication(Player player, PremadeGroupType type, long groupId, long applicationId)
	{
		PremadeGroup group = getPremadeGroup(type, groupId);
		if (group == null)
			return;
		PremadeGroupApplication application = null;
		if ((application = group.removeApplication(applicationId)) == null)
			return;
		if (application.getPlayerList() != null)
		{
			int i = -1;
			while (++i < application.getPlayerList().length)
				//TODO: CommandPremadeGroup.sendCanceledApplicationToAGroup(group, application.getPlayerList()[i]);
				;
		}
		else
			;
			//TODO: CommandPremadeGroup.sendCanceledApplicationToAGroup(group, player);
		if (group.getParty() != null)
		{
			int i = -1;
			while (++i < group.getParty().getPlayerList().length)
				if (group.getParty().getPlayer(i) != null)
					//TODO: CommandPremadeGroup.sendCanceledApplication
					;
		}
		else
			//TODO: CommandPremadeGroup.sendCanceledApplication
			;
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

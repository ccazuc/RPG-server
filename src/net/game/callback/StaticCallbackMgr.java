package net.game.callback;

import net.game.Party;
import net.game.premade_group.PremadeGroupFactionMgr;
import net.game.premade_group.PremadeGroupMgr;
import net.game.unit.Player;

public class StaticCallbackMgr {

	public static void onPartyLeaderChange(Player player)
	{
		if (player.getPremadeGroup() != null)
			player.getPremadeGroup().updateLeader(player);
	}
	
	public static void onPartyDisbanded(Party party)
	{
		PremadeGroupFactionMgr premadeGroupMgr = PremadeGroupMgr.getPremadeGroupMgr(party.getFaction());
		if (premadeGroupMgr == null)
			return;
		premadeGroupMgr.removeAllApplicationOnPartyDisband(party);
	}
}

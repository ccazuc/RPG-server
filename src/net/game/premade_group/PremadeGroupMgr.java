package net.game.premade_group;

import net.game.unit.Faction;
import net.thread.log.LogRunnable;

public class PremadeGroupMgr {

	private final static PremadeGroupFactionMgr alliancePremadeGroupMgr = new PremadeGroupFactionMgr();
	private final static PremadeGroupFactionMgr hordePremadeGroupMgr = new PremadeGroupFactionMgr();
	public final static long APPLICATION_DURATION = 1000 * 60 * 5;
	public final static long FETCH_TIMER_FREQUENCE = 1000 * 2;
	
	public final static PremadeGroupFactionMgr getPremadeGroupMgr(Faction type)
	{
		if (type == Faction.ALLIANCE)
			return (alliancePremadeGroupMgr);
		if (type == Faction.HORDE)
			return (hordePremadeGroupMgr);
		LogRunnable.addErrorLog("Error, Faction not found");
		return (null);
	}
}

package net.game.premade_group;

import net.Server;
import net.game.Party;

public class PremadeGroupApplication {

	private final long applyTimer;
	private final long id;
	private final int playerId;
	private final String description;
	private final Party party;
	private final PremadeGroup group;
	
	public PremadeGroupApplication(PremadeGroup group, long id, int playerId, String description)
	{
		this.group = group;
		this.id = id;
		this.playerId = playerId;
		this.description = description;
		this.party = null;
		this.applyTimer = Server.getLoopTickTimer();
	}
	
	public PremadeGroupApplication(PremadeGroup group, long id, Party party, String description)
	{
		this.group = group;
		this.id = id;
		this.playerId = party.getLeaderId();
		this.party = party;
		this.description = description;
		this.applyTimer = Server.getLoopTickTimer();
	}
	
	public final long getApplyTimer()
	{
		return (this.applyTimer);
	}
	
	public final boolean hasApplicationExpired()
	{
		return (Server.getLoopTickTimer() < this.applyTimer + PremadeGroupMgr.APPLICATION_DURATION);
	}
	
	public final PremadeGroup getPremadeGroup()
	{
		return (this.group);
	}
	
	public final long getId()
	{
		return (this.id);
	}
	
	public final Party getParty()
	{
		return (this.party);
	}
	
	public final int[] getPlayerList()
	{
		return (this.party != null ? this.party.getPlayerList() : null);
	}
	
	public final int getPlayerId()
	{
		return (this.playerId);
	}
	
	public final String getDescription()
	{
		return (this.description);
	}
}

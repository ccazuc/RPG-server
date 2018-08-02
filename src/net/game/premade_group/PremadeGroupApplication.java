package net.game.premade_group;

import net.game.Party;
import net.game.unit.Player;

public class PremadeGroupApplication {

	private final long id;
	private final int playerId;
	private final String description;
	private final Party party;
	
	public PremadeGroupApplication(long id, int playerId, String description)
	{
		this.id = id;
		this.playerId = playerId;
		this.description = description;
		this.party = null;
	}
	
	public PremadeGroupApplication(long id, Party party, String description)
	{
		this.id = id;
		this.playerId = party.getLeaderId();
		this.party = party;
		this.description = description;
	}
	
	public final long getId()
	{
		return (this.id);
	}
	
	public final Player[] getPlayerList()
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

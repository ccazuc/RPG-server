package net.game.guild;

public class GuildEvent {

	private final long timer;
	private final GuildJournalEventType type;
	private final String player1Name;
	private final String player2Name;
	private final int rankID;
	
	public GuildEvent(long timer, GuildJournalEventType type, String player1Name) {
		this(timer, type, player1Name, "", -1);
	}
	
	public GuildEvent(long timer, GuildJournalEventType type, String player1Name, String player2Name) {
		this(timer, type, player1Name, player2Name, -1);
	}
	
	public GuildEvent(long timer, GuildJournalEventType type, String player1Name, String player2Name, int rankID) {
		this.player1Name = player1Name;
		this.player2Name = player2Name;
		this.rankID = rankID;
		this.timer = timer;
		this.type = type;
	}
	
	public long getTimer() {
		return this.timer;
	}
	
	public GuildJournalEventType getEventType() {
		return this.type;
	}
	
	public String getPlayer1Name() {
		return this.player1Name;
	}
	
	public String getPlayer2Name() {
		return this.player2Name;
	}
	
	public int getRankID() {
		return this.rankID;
	}
}

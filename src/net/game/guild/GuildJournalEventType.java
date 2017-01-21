package net.game.guild;

public enum GuildJournalEventType {

	MEMBER_INVITED((byte)0),
	MEMBER_JOINED((byte)1),
	MEMBER_LEFT((byte)2),
	MEMBER_KICKED((byte)3),
	MEMBER_PROMOTED((byte)4),
	MEMBER_DEMOTED((byte)5),
	;
	
	private final byte value;
	
	private GuildJournalEventType(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}
}

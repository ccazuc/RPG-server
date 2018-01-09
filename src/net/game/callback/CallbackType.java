package net.game.callback;

public enum CallbackType {

	GUILD_MOTD_CHANGED((byte)0),
	LEVEL_CHANGED((byte)1),
	PLAYER_HEALTH_CHANGED((byte)2),
	PLAYER_MANA_CHANGED((byte)3),
	TARGET_HEALTH_CHANGED((byte)4),
	TARGET_MANA_CHANGED((byte)5),
	EXPERIENCE_CHANGED((byte)6),
	BAG_CHANGED((byte)7),
	QUEST_COMPLETED((byte)8),	//0: Player, 1: QuestId
	PLAYER_LOGGED_OUT((byte)9),	//0: Player
	;
	
	private final byte value;
	
	private CallbackType(byte value)
	{
		this.value = value;
	}
	
	public byte getValue()
	{
		return (this.value);
	}
}

package net.game.premade_group;

public enum PremadeGroupType {

	QUESTING((byte)0),
	DUNGEONS((byte)1),
	SCENARIOS((byte)2),
	RAIDS_CURR((byte)3),
	RAIDS_LEGACY((byte)4),
	ARENAS((byte)5),
	ARENAS_SKIRMISHES((byte)6),
	BATTLEGROUNDS((byte)7),
	RATED_BATTLEGROUNDS((byte)8),
	ASHRAN((byte)9),
	CUSTOM((byte)10),
	;

	private final byte value;
	
	private PremadeGroupType(byte value)
	{
		this.value = value;
	}

	public byte getValue()
	{
		return (this.value);
	}

	public static PremadeGroupType getValue(byte value)
	{
		if (value >= 0 && value < PremadeGroupType.values().length)
			return (PremadeGroupType.values()[value]);
		return (null);
	}
}

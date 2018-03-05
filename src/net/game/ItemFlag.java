package net.game;

public enum ItemFlag {

	QUEST_ITEM(0x01),
	SOULDBOUND_ON_LOOT(0x02),
	SOULBOUND_ON_EQUIP(0x04),
	ACCOUNT_BOUND(0x08),
	PARTY_LOOT(0x010),
	UNIQUE(0x020),
	UNIQUE_EQUIPPED(0x040),
	HAS_NORMAL_PRICE(0x080),
	HORDE_ONLY(0x0100),
	ALLIANCE_ONLY(0x0200),
	;
	
	private final int value;
	
	private ItemFlag(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return (this.value);
	}
}

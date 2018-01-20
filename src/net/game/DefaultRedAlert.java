package net.game;

public enum DefaultRedAlert {

	ALREADY_CASTING((byte)0, "I'm already casting !"),
	STUNNED((byte)1, "I cannot do this while stunned"),
	DEAD((byte)2, "I cannot do this while dead"),
	CANNOT_EQUIP_ITEM((byte)3, "I cannot equip this item"),
	CANNOT_STACK_ITEM((byte)4, "This item cannot stack"),
	SPELL_NOT_READY_YET((byte)5, "This spell is not ready yet."),
	NOTHING_TO_ATTACK((byte)6, "There is nothing to attack."),
	NOT_ENOUGH_MANA(((byte)7), "I don't have enough mana to do this."),
	NOT_ENOUGH_GOLD(((byte)8), "I don't have enough gold."),
	MUST_MEET_MIN_BID(((byte)9), "You must meet min bid."),
	QUEST_DISABLED((byte)10, "This quest is disabled."),
	QUEST_NOT_COMPLETED((byte)11, "You did not complete the objectives."),
	TOO_MUCH_QUESTS((byte)12, "Your quest log is full."),
	QUEST_NOT_UNLOCKED((byte)13, "You did not unlock that quest yet."),
	QUEST_ALREADY_ACCEPTED((byte)14, "You have already accepted this quest."),
	CANNOT_FIND_RECIPIENT((byte)15, "Cannot find recipient"),
	;
	
	private final byte value;
	private final String message;
	
	private DefaultRedAlert(byte value, String message) {
		this.message = message;
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}
	
	public String getMessage() {
		return this.message;
	}
}

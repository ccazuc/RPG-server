package net.game;

public enum DefaultRedAlert {

	ALREADY_CASTING((byte)0, "I'm already casting !"),
	STUNNED((byte)1, "I cannot do this while stunned"),
	DEAD((byte)2, "I cannot do this while dead"),
	CANNOT_EQUIP_ITEM((byte)3, "I cannot equip this item"),
	CANNOT_STACK_ITEM((byte)4, "This item cannot stack"),
	SPELL_NOT_READY_YET((byte)5, "This spell is not ready yet."),
	;
	
	private byte value;
	private String message;
	
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

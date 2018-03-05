package net.game.item;

public enum ItemSourceType {

	DROP_MOB((byte)0),
	DROP_CONTAINER((byte)1),
	ACHIVEMENT((byte)2),
	;
	
	private final byte value;
	
	private ItemSourceType(byte value)
	{
		this.value = value;
	}
	
	public byte getValue()
	{
		return (this.value);
	}
}

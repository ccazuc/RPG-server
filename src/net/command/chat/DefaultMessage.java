package net.command.chat;

public enum DefaultMessage {

	NOT_ENOUGH_RIGHT((byte)0, "You don't have the right to do this."),
	NOT_IN_GUILD((byte)1, "You are not in a guild."),
	PLAYER_NOT_IN_GUILD((byte)2, "This player is not in your guild."),
	;
	
	private String message;
	private byte value;
	
	private DefaultMessage(byte value, String message)
	{
		this.message = message;
		this.value = value;
	}
	
	public String getMessage()
	{
		return (this.message);
	}
	
	public byte getValue()
	{
		return (this.value);
	}
}

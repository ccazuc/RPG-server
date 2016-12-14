package net.command.chat;

public enum DefaultMessage {

	NOT_ENOUGH_RIGHT((char)0, "You don't have the right to do this."),
	NOT_IN_GUILD((char)1, "You are not in a guild."),
	PLAYER_NOT_IN_GUILD((char)2, "This player is not in your guild."),
	;
	
	private String message;
	private char value;
	
	private DefaultMessage(char value, String message)  {
		this.message = message;
		this.value = value;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public char getValue() {
		return this.value;
	}
}

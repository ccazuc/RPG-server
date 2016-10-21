package net.command.chat;

public enum MessageType {

	SAY((char)0),
	GUILD((char)1),
	PARTY((char)2),
	RAID((char)3),
	BATTLEGROUND((char)4),
	YELL((char)5),
	DISCUSSION((char)6),
	SELF((char)7),
	WHISPER((char)8);
	
	private char value;
	
	private MessageType(char value) {
		this.value = value;
	}
	
	public char getValue() {
		return this.value;
	}
}

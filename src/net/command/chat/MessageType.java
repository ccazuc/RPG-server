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
	WHISPER((char)8),
	EMOTE((char)9),
	PARTY_LEADER((char)10),
	OFFICER_CHAT((char)11),
	GM_ANNOUNCE((char)12);
	
	private char value;
	
	private MessageType(char value) {
		this.value = value;
	}
	
	public char getValue() {
		return this.value;
	}
}

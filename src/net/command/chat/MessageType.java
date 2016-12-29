package net.command.chat;

public enum MessageType {

	SAY((byte)0),
	GUILD((byte)1),
	PARTY((byte)2),
	RAID((byte)3),
	BATTLEGROUND((byte)4),
	YELL((byte)5),
	DISCUSSION((byte)6),
	SELF((byte)7),
	WHISPER((byte)8),
	EMOTE((byte)9),
	PARTY_LEADER((byte)10),
	OFFICER_CHAT((byte)11),
	GM_ANNOUNCE((byte)12);
	
	private byte value;
	
	private MessageType(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}
}

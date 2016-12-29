package net.command.chat;

import net.utils.Color;

public enum MessageType {

	SAY((byte)0, Color.WHITE, "Say : ", " say : "),
	GUILD((byte)1, new Color(64/255f, 1, 64f/255f), "Guild : ", "[Guild] "),
	PARTY((byte)2, new Color(170/255f, 170/255f, 1), "Party : ", "[Party] "),
	RAID((byte)3, new Color(1, 127/255f, 0), "Raid : ", "[Raid] "),
	BATTLEGROUND((byte)4, Color.WHITE, "Battleground : ", "[Battleground] "),
	YELL((byte)5, new Color(1, 63/255f, 64/255f), "Yell : ", " Yell : "),
	CHANNEL((byte)6, new Color(1, 192/255f, 192/255f), "Channel : ", ""),
	SELF((byte)7, new Color(1, 1, 0), "", ""),
	WHISPER((byte)8, new Color(1, 128/255f, 1), "Say to ", ""),
	EMOTE((byte)9, new Color(1, 251/255f, 1), "", ""),
	PARTY_LEADER((byte)10, new Color(118/255f, 197/255f, 1), "Party : ", "[Party leader] "),
	OFFICER((byte)11, new Color(64/255f, 192/255f, 64/255f), "Officer : ", "[Officer] "),
	ANNOUNCE((byte)12, new Color(0/255f, 208/255f, 225/255f), "Announce : ", "[Announce] "),
	GM_ANNOUNCE((byte)13, new Color(0/255f, 208/255f, 225/255f), "GM announce : ", "[GM Announce] "),
	IGNORE((byte)14, new Color(1, 0, 0), "" ,"");
	
	private byte value;
	private Color color;
	private String defaultText;
	private String chatDisplay;
	
	private MessageType(byte value, Color color, String defaultText, String chatDisplay) {
		this.value = value;
		this.color = color;
		this.defaultText = defaultText;
		this.chatDisplay = chatDisplay;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public byte getValue() {
		return this.value;
	}
	
	public String getDefaultText() {
		return this.defaultText;
	}
	
	public String getChatDisplay() {
		return this.chatDisplay;
	}
}

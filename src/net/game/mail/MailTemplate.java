package net.game.mail;

public enum MailTemplate {

	CLASSIC((byte)0),
	AH((byte)1),
	SUPPORT((byte)2),
	;
	
	private final byte value;
	
	private MailTemplate(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}
}

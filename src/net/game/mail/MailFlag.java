package net.game.mail;

public enum MailFlag
{
	MAIL_RETURNED((short)0x01),
	MAIL_READ((short)0x02),
	CR_PAID((short)0x03),	
	;
	
	private final short value;
	
	private MailFlag(short value)
	{
		this.value = value;
	}
	
	public short getValue()
	{
		return (this.value);
	}
}

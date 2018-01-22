package net.game.mail;

import net.game.manager.CharacterMgr;

public class Mail {

	private final int authorID;
	private final String authorName;
	private final int destID;
	private final String title;
	private final String content;
	private final long deleteDate;
	private final byte template;
	private final int gold;
	private final boolean isCR;
	private final long GUID;
	private short flag;
	
	public Mail(long GUID, int authorID, int destID, String title, String content, long deleteDate, int gold, boolean isCR, byte template, short flag)
	{
		this.authorID = authorID;
		if (authorID == -1)
			this.authorName = "Unknown";
		else
			this.authorName = CharacterMgr.loadCharacterNameFromID(this.authorID);
		this.destID = destID;
		this.title = title;
		this.content = content;
		this.deleteDate = deleteDate;
		this.gold = gold;
		this.isCR = isCR;
		this.GUID = GUID;
		this.template = template;
		this.flag = flag;
	}
	
	public Mail(long GUID, int authorID, String destName, int destID, String title, String content, long deleteDate, int gold, boolean isCR, byte template, short flag)
	{
		this.authorID = authorID;
		this.authorName = destName;
		this.destID = destID;
		this.title = title;
		this.content = content;
		this.deleteDate = deleteDate;
		this.gold = gold;
		this.isCR = isCR;
		this.GUID = GUID;
		this.template = template;
		this.flag = flag;
	}
	
	public long getGUID()
	{
		return (this.GUID);
	}

	public int getAuthorID()
	{
		return (this.authorID);
	}
	
	public byte getTemplate()
	{
		return (this.template);
	}
	
	public String getAuthorName()
	{
		return (this.authorName);
	}
	
	public int getDestID()
	{
		return (this.destID);
	}
	
	public String getTitle()
	{
		return (this.title);
	}
	
	public String getContent()
	{
		return (this.content);
	}
	
	public long getDeleteDate()
	{
		return (this.deleteDate);
	}
	
	public int getGold()
	{
		return (this.gold);
	}
	
	public boolean getIsCR()
	{
		return (this.isCR);
	}
	
	public boolean getCRPaid()
	{
		return ((this.flag & MailFlag.CR_PAID.getValue()) != 0);
	}
	
	public void payCR()
	{
		this.flag |= MailFlag.CR_PAID.getValue();
	}
	
	public short getFlag()
	{
		return (this.flag);
	}
	
	public void setMailReturnedFlag()
	{
		this.flag |= MailFlag.MAIL_RETURNED.getValue();
	}
	
	public boolean wasAlreadyReturned()
	{
		return ((this.flag & MailFlag.MAIL_RETURNED.getValue()) != 0);
	}
	
	public boolean getRead()
	{
		return ((this.flag & MailFlag.MAIL_READ.getValue()) != 0);
	}
	
	public void setRead()
	{
		this.flag |= MailFlag.MAIL_READ.getValue();
	}
	
	public boolean canReply()
	{
		return ((this.flag & MailFlag.CAN_REPLY.getValue()) != 0);
	}
}

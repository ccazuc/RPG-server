package net.game.mail;

import net.game.manager.CharacterMgr;

public class Mail {

	private final int autorID;
	private final String autorName;
	private final int destID;
	private final String title;
	private final String content;
	private final long deleteDate;
	private final byte template;
	private final int gold;
	private final boolean isCR;
	private final long GUID;
	private boolean read;
	
	public Mail(long GUID, int autorID, int destID, String title, String content, long deleteDate, int gold, boolean isCR, byte template, boolean read) {
		this.autorID = autorID;
		this.autorName = CharacterMgr.loadCharacterNameFromID(this.autorID);
		this.destID = destID;
		this.title = title;
		this.content = content;
		this.deleteDate = deleteDate;
		this.gold = gold;
		this.isCR = isCR;
		this.GUID = GUID;
		this.template = template;
		this.read = read;
	}
	
	public Mail(long GUID, int autorID, String destName, int destID, String title, String content, long deleteDate, int gold, boolean isCR, byte template, boolean read) {
		this.autorID = autorID;
		this.autorName = destName;
		this.destID = destID;
		this.title = title;
		this.content = content;
		this.deleteDate = deleteDate;
		this.gold = gold;
		this.isCR = isCR;
		this.GUID = GUID;
		this.template = template;
		this.read = read;
	}
	
	public void read() {
		this.read = true;
	}
	
	public boolean getRead() {
		return this.read;
	}
	
	public long getGUID() {
		return this.GUID;
	}

	public int getAutorID() {
		return this.autorID;
	}
	
	public byte getTemplate() {
		return this.template;
	}
	
	public String getAutorName() {
		return this.autorName;
	}
	
	public int getDestID() {
		return this.destID;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public long getDeleteDate() {
		return this.deleteDate;
	}
	
	public int getGold() {
		return this.gold;
	}
	
	public boolean getIsCR() {
		return this.isCR;
	}
}

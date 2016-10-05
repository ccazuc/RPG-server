package net.connection;

public class PacketID {
	
	public static final byte LOGIN = 1;
	public static final byte LOGOUT = 2;
	public static final byte ALREADY_LOGGED = 3;
	public static final byte LOGIN_ACCEPT = 4;
	public static final byte ACCOUNT_BANNED_TEMP = 5;
	public static final byte ACCOUNT_BANNED_PERM = 6;
	public static final byte LOGIN_WRONG = 7;
	public static final byte SELECT_SCREEN_LOAD_CHARACTERS = 8;
	public static final byte CREATE_CHARACTER = 9;
	public static final byte ERROR_NAME_ALPHABET = 10;
	public static final byte ERROR_NAME_ALREADY_TAKEN = 11;
	public static final byte ERROR_NAME_LENGTH = 12;
	public static final byte CHARACTER_CREATED = 13;
	public static final byte DELETE_CHARACTER = 14;
	public static final byte LOAD_CHARACTER = 15;
	public static final byte LOAD_EQUIPPED_ITEMS = 16;
	public static final byte LOAD_BAG_ITEMS = 17;
	public static final byte LOAD_SPELLBAR = 18;
	public static final byte STUFF = 19;
	public static final byte WEAPON = 20;
	public static final byte GEM = 21;
	public static final byte POTION = 22;
	public static final byte SEND_EQUIPPED_ITEMS = 23;
	public static final byte SEND_BAG_ITEMS = 24;
	public static final byte PING = 25;
	public static final byte PING_CONFIRMED = 26;
	public static final byte SEND_SINGLE_BAG_ITEM = 27;
	public static final byte SEND_SINGLE_EQUIPPED_ITEM = 28;
	public static final byte SEND_SINGLE_SPELLBAR_ITEM = 29;
	public static final byte LOAD_STATS = 30;
	public static final byte ADD_ITEM = 31;
	public static final byte ADD_ITEM_CONFIRMED = 32;
	public static final byte REQUEST_ITEM = 33;
	public static final byte KNOWN_ITEM = 34;
	public static final byte UNKNOWN_ITEM = 35;
	public static final byte CHAT_LIST_PLAYER = 36;
}

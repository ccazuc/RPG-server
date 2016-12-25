package net.game.manager;

import java.awt.image.LookupOp;

public class DebugMgr {

	private static boolean CHAT_COMMAND_TIMER = true;
	private static boolean SQL_REQUEST_TIMER = true;
	private static boolean WRITE_LOG_FILE_TIMER = true;
	private static int LOOP_TOO_LONG_VALUE = 5;
	
	public static boolean getChatCommandTimer() {
		return CHAT_COMMAND_TIMER;
	}
	
	public static void setChatCommandTimer(boolean we) {
		CHAT_COMMAND_TIMER = we;
	}
	
	public static boolean getSQLRequestTimer() {
		return SQL_REQUEST_TIMER;
	}
	
	public static void setSQLRequestTimer(boolean we) {
		SQL_REQUEST_TIMER = we;
	}
	
	public static boolean getWriteLogFileTimer() {
		return WRITE_LOG_FILE_TIMER;
	}
	
	public static void setWriteLogFileTimer(boolean we) {
		WRITE_LOG_FILE_TIMER = we;
	}
	
	public static int getLoopTooLongValue() {
		return LOOP_TOO_LONG_VALUE;
	}
	
	public static void setLoopTooLongValue(int value) {
		LOOP_TOO_LONG_VALUE = value;
	}
}

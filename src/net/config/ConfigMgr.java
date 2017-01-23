package net.config;

public class ConfigMgr {

	private static String SERVER_MESSAGE_OF_THE_DAY = "Welcome on blabla";
	
	public final static String REALM_NAME = "World Server";
	public final static int REALM_ID = 15;
	public final static int PORT = 5721;
	public final static int KEY_TIMEOUT_TIMER = 15000;
	public final static boolean ALLOW_INTERFACTION_CHANNEL = true;
	public final static boolean TCP_NO_DELAY_ENABLED = false;
	
	public static String getServerMessageOfTheDay() {
		return SERVER_MESSAGE_OF_THE_DAY;
	}
	
	public static void setServerMessageOfTheDay(String message) {
		SERVER_MESSAGE_OF_THE_DAY = message;
	}
}

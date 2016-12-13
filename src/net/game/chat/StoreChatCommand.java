package net.game.chat;

import java.util.HashMap;

public class StoreChatCommand {

	private final static HashMap<String, ChatCommand> commandMap = new HashMap<String, ChatCommand>();
	
	public static void initChatCommandMap() {
		
	}
	
	public static boolean contains(String command) {
		return commandMap.containsKey(command);
	}
	
	public static void get(String command) {
		commandMap.get(command);
	}
}

package net.game.callback;

import java.util.ArrayList;

import net.game.quest.PlayerQuestMgr;

public class CallbackMgr {

	@SuppressWarnings("unchecked")
	private final static ArrayList<Callback>[] callbackArray = (ArrayList<Callback>[]) new ArrayList<?>[CallbackType.values().length];
	
	public static void initCallbackList() {
		int i = -1;
		while (++i < callbackArray.length)
			callbackArray[i] = new ArrayList<Callback>();
	}
	
	public static void registerCallback(CallbackType type, Callback callback) {
		callbackArray[type.getValue()].add(callback);
	}
	
	public static void unregisterCallback(CallbackType type, Callback callback) {
		int i = -1;
		while (++i < callbackArray[type.getValue()].size())
			if (callbackArray[type.getValue()].get(i) == callback)
				callbackArray[type.getValue()].remove(i);
	}
	
	public static void executeCallback(CallbackType type, Object ...obj) {
		int i = -1;
		while (++i < callbackArray[type.getValue()].size())
			callbackArray[type.getValue()].get(i).handleCallback(obj);
	}
	
	public static void registerAllCallback()
	{
		PlayerQuestMgr.initCallback();
	}
}

package net.game.manager;

import java.util.ArrayList;

import net.Server;
import net.command.player.CommandLoginQueue;
import net.game.callback.CallbackMgr;
import net.game.callback.CallbackType;
import net.game.callback.UpdateLoginQueueOnPlayerLogoutCallback;
import net.game.unit.Player;

public class LoginQueueMgr {

	private final static ArrayList<Player> playerList = new ArrayList<Player>();
	private final static UpdateLoginQueueOnPlayerLogoutCallback callback = new UpdateLoginQueueOnPlayerLogoutCallback();
	
	public static void initCallback()
	{
		CallbackMgr.registerCallback(CallbackType.PLAYER_LOGGED_OUT, callback);
	}
	
	public static void addPlayerInQueue(Player player)
	{
		playerList.add(player);
		Server.removeNonLoggedPlayer(player);
	}
	
	public static void loginAccepted(Player player)
	{
		int i = -1;
		Server.addLoggedPlayer(player);
		while (++i < playerList.size())
			if (playerList.get(i) == player)
				playerList.remove(i);
		//CommandLoginQueue.confirmLoginRequest(player);
	}
	
	public static void playerLoggedOut()
	{
		if (playerList.size() == 0)
			return;
		int i = 0;
		//playerList.get(0).setLoginQueueRequestSent();
		//CommandLoginQueue.logPlayer(playerList.get(0));
		while (++i < playerList.size())
			CommandLoginQueue.updatePosition(playerList.get(0), i + 1, playerList.size());
	}
	
	public static ArrayList<Player> getLoginQueueList()
	{
		return (playerList);
	}
}

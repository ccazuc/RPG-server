package net.game.manager;

import java.util.ArrayList;

import net.Server;
import net.command.player.CommandLoginQueue;
import net.config.ConfigMgr;
import net.game.callback.CallbackMgr;
import net.game.callback.CallbackType;
import net.game.callback.UpdateLoginQueueOnPlayerLogoutCallback;
import net.game.unit.Player;

public class LoginQueueMgr {

	private final static ArrayList<Player> playerList = new ArrayList<Player>();
	private final static UpdateLoginQueueOnPlayerLogoutCallback callback = new UpdateLoginQueueOnPlayerLogoutCallback();
	public final static int UPDATE_POSITION_FREQUENCE = 1000;
	public final static int LOGIN_REQUEST_TIMEOUT = 5000;
	private static long lastPositionUpdateTimer;
	private static boolean shouldUpdatePosition;
	
	public static void initCallback()
	{
		CallbackMgr.registerCallback(CallbackType.PLAYER_LOGGED_OUT, callback);
	}
	
	public static void addPlayerInQueue(Player player)
	{
		playerList.add(player);
		Server.removeNonLoggedPlayer(player);
		CommandLoginQueue.playerAddedInQueue(player);
		player.setIsInLoginQueue(true);
		updatePosition();
	}
	
	public static void loginAccepted(Player player)
	{
		if (!player.isInLoginQueue())
			return;
		int i = -1;
		while (++i < playerList.size())
			if (playerList.get(i) == player)
			{
				playerList.remove(i);
				break;
			}
		Server.addLoggedPlayer(player);		
		player.setIsInLoginQueue(false);
		updatePosition();
		CommandLoginQueue.logPlayer(player);
	}
	
	public static void playerLoggedOut(Player player)
	{
		if (playerList.size() == 0)
			return;
		if (player.isInLoginQueue())
		{
			int i = -1;
			while (++i < playerList.size())
				if (playerList.get(i) == player)
				{
					playerList.remove(i);
					updatePosition();
					break;
				}
		}
		if (playerList.size() == 0)
			return;
		if (Server.getNumberInGameCharacter() + Server.getNumberLoggedAccount() < ConfigMgr.GetServerMaxCapacity())
		{
			loginAccepted(playerList.get(0));
			updatePosition();
		}
	}
	
	public static void updatePosition()
	{
		shouldUpdatePosition = true;
	}
	
	public static void tick()
	{
		int i = -1;
		if (shouldUpdatePosition && lastPositionUpdateTimer + UPDATE_POSITION_FREQUENCE <= Server.getLoopTickTimer())
		{
			while (++i < playerList.size())
			{
				CommandLoginQueue.updatePosition(playerList.get(i), i + 1, playerList.size());
				playerList.get(i).getConnectionManager().read();
			}
			lastPositionUpdateTimer = Server.getLoopTickTimer();
			shouldUpdatePosition = false;
		}
		else
			while (++i < playerList.size())
				playerList.get(i).getConnectionManager().read();
	}
	
	public static ArrayList<Player> getLoginQueueList()
	{
		return (playerList);
	}
}

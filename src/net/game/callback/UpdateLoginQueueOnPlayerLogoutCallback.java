package net.game.callback;

import net.game.manager.LoginQueueMgr;
import net.game.unit.Player;

public class UpdateLoginQueueOnPlayerLogoutCallback implements Callback {

	@Override
	public void handleCallback(Object ...obj)
	{
		Player player = (Player)obj[0];
		if (player.isInLoginQueue())
			LoginQueueMgr.playerLoggedOut((Player)obj[0]);
	}
}

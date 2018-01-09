package net.game.callback;

import net.game.manager.LoginQueueMgr;

public class UpdateLoginQueueOnPlayerLogoutCallback implements Callback {

	@Override
	public void handleCallback(Object ...obj)
	{
		LoginQueueMgr.playerLoggedOut();
	}
}

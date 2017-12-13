package net.game.callback;

import net.game.unit.Player;

public class UpdateAvailableQuestOnLevelupCallback implements Callback {

	@Override
	public void handleCallback(Object ...obj) {
		Player player = (Player)obj[0];
		player.getQuestManager().updateAvailableQuest();
	}
}

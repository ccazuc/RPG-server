package net.game.log;

import net.game.unit.Player;
import net.thread.log.LogRunnable;

public class Log {
	
	public static void writePlayerLog(Player player, String text) {
		LogRunnable.writePlayerLog(player, text);
	}
}

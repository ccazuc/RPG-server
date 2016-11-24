package net.command;

import net.game.Player;

public class CommandLogout extends Command {

	@Override
	public final void read(Player player) {
		player.close();
	}
}

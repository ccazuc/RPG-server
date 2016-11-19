package net.command;

import net.game.Player;

public class CommandLogout extends Command {
	
	public CommandLogout() {}

	@Override
	public final void read(Player player) {
		player.close();
	}
}

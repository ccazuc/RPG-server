package net.command.player;

import net.command.Command;
import net.game.unit.Player;

public class CommandLogout extends Command {

	@Override
	public final void read(Player player) {
		player.close();
	}
}

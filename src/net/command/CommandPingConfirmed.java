package net.command;

import net.game.Player;

public class CommandPingConfirmed extends Command {
	
	@Override
	public void read(Player player) {
		player.setPingStatus(false);
	}
}

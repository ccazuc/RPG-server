package net.command.player;

import net.Server;
import net.command.Command;
import net.game.Player;
import net.thread.sql.Who;

public class CommandWho extends Command {
	
	@Override
	public void read(Player player) {
		String word = player.getConnection().readString();
		Server.addNewWhoRequest(new Who(word, player.getConnection()));
	}
}

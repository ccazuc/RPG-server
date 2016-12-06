package net.command.player;

import net.command.Command;
import net.connection.Connection;
import net.game.Player;

public class CommandSpellCast extends Command {
	
	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		int spellId = connection.readInt();
		if(player.getSpellUnlocked().containsKey((spellId))) {
			player.getSpellUnlocked(spellId).action(player, player.getTarget());
		}
	}
}

package net.command.player;

import net.command.Command;
import net.connection.Connection;
import net.game.Player;

public class CommandCast extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		int id = connection.readInt();
		if(player.getStamina() <= 0) {
			return;
		}
		/*if(!player.getSpellTable.contains(id)) {
		 	player.close();
			return;
		}*/
		/*if(player.isCasting()) {
			return;
		}*/
		/*if(!player.canCastSpell()) {
			return;
		}*/
	}
}

package net.command.player;

import net.command.Command;
import net.connection.Connection;
import net.game.Player;
import net.game.log.Log;
import net.game.manager.AccountMgr;
import net.game.manager.CharacterMgr;

public class CommandDeleteCharacter extends Command {
	
	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		int id = connection.readInt();
		if(AccountMgr.loadAccountIDFromCharacterID(id) != player.getAccountId()) {
			Log.writePlayerLog(player, "Tried to delete someone else's character (id = "+id+')');
			player.close();
			return;
		}
		CharacterMgr.deleteCharacterByID(id);
	}
}
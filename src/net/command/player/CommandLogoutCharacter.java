package net.command.player;

import java.sql.SQLException;

import net.Server;
import net.command.Command;
import net.game.log.Log;
import net.game.unit.Player;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;
import net.thread.sql.SQLRequestPriority;

public class CommandLogoutCharacter extends Command {
	
	private static SQLRequest setOffline = new SQLRequest("UPDATE `character` SET online = 0 WHERE character_id = ?", "Set offline", SQLRequestPriority.LOW) {
		
		@Override
		public void gatherData() throws SQLException {
			this.statement.putInt((int)this.datasList.get(0).getNextObject());
		}
	};

	@Override
	public void read(Player player) {
		if(!player.isOnline()) {
			Log.writePlayerLog(player, "tried to logout whereas he's not in-game");
			player.close();
			return;
		}
		setPlayerOfflineInDB(player);
		player.logoutCharacter();
	}
	
	public static void setPlayerOfflineInDB(Player player) {
		if(player.getUnitID() != 0) {
			setOffline.addDatas(new SQLDatas(player.getUnitID()));
			Server.executeSQLRequest(setOffline);
		}
	}
}

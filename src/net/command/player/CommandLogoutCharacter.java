package net.command.player;

import java.sql.SQLException;

import net.Server;
import net.command.Command;
import net.game.Player;
import net.game.log.Log;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;
import net.thread.sql.SQLRequestPriority;

public class CommandLogoutCharacter extends Command {
	
	private static SQLRequest setOffline = new SQLRequest("UPDATE `character` SET online = 0 WHERE character_id = ?", "Set offline", SQLRequestPriority.LOW) {
		
		@Override
		public void gatherData() {
			try {
				this.statement.clear();
				this.statement.putInt(this.datasList.get(0).getIValue1());
				this.statement.execute();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}	
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
		if(player.getCharacterId() != 0) {
			setOffline.addDatas(new SQLDatas(player.getCharacterId()));
			Server.executeSQLRequest(setOffline);
		}
	}
}

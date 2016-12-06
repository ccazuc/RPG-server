package net.command.player;

import java.sql.SQLException;

import net.Server;
import net.command.Command;
import net.game.Player;
import net.game.manager.FriendManager;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;

public class CommandLogoutCharacter extends Command {
	
	private static SQLRequest setOffline = new SQLRequest("UPDATE `character` SET online = 0 WHERE character_id = ?", "Set offline") {
		
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
		if(!Server.getInGamePlayerList().containsKey(player.getCharacterId())) {
			return;
		}
		setPlayerOfflineInDB(player);
		player.notifyFriendOffline();
		player.setGuildRequest(0);
		if(player.getGuild() != null) {
			CommandGuild.notifyOfflinePlayer(player);
		}
		Server.addLoggedPlayer(player);
		Server.removeInGamePlayer(player);
		FriendManager.getFriendMap().remove(player.getCharacterId());
		player.resetDatas();
		CommandTrade.closeTrade(player);
	}
	
	public static void setPlayerOfflineInDB(Player player) {
		if(player.getCharacterId() != 0) {
			//setOffline.setId(player.getCharacterId());
			setOffline.addDatas(new SQLDatas(player.getCharacterId()));
			Server.addNewRequest(setOffline);
		}
	}
}

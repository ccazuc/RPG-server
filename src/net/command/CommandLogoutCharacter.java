package net.command;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Server;
import net.game.Player;

public class CommandLogoutCharacter extends Command {
	
	private static JDOStatement setOffline;
	
	public CommandLogoutCharacter() {}

	@Override
	public void read(Player player) {
		if(Server.getInGamePlayerList().containsKey(player.getCharacterId())) {
			setPlayerOfflineInDB(player);
			player.notifyFriendOffline();
			player.setGuildRequest(0);
			if(player.getGuild() != null) {
				CommandGuild.notifyOfflinePlayer(player);
			}
			Server.addLoggedPlayer(player);
			Server.removeInGamePlayer(player);
			Server.getFriendMap().remove(player.getCharacterId());
			player.resetDatas();
			CommandTrade.closeTrade(player);
		}
		else {
			//player is using WPE
		}
	}
	
	public static void setPlayerOfflineInDB(Player player) {
		if(player.getCharacterId() != 0) {
			try {
				if(setOffline == null) {
					setOffline = Server.getJDO().prepare("UPDATE `character` SET online = 0 WHERE character_id = ?");
				}
				setOffline.clear();
				setOffline.putInt(player.getCharacterId());
				setOffline.execute();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
}

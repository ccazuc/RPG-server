package net.command;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Server;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.Player;

public class CommandLogoutCharacter extends Command {
	
	private static JDOStatement setOffline;
	
	public CommandLogoutCharacter(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	@Override
	public void read() {
		if(Server.getInGamePlayerList().containsKey(this.player.getCharacterId())) {
			setPlayerOfflineInDB(this.player);
			this.player.notifyFriendOffline();
			this.player.setGuildRequest(0);
			if(this.player.getGuild() != null) {
				CommandGuild.notifyOfflinePlayer(this.player);
			}
			Server.addLoggedPlayer(this.player);
			Server.removeInGamePlayer(this.player);
			Server.getFriendMap().remove(this.player.getCharacterId());
			this.player.resetDatas();
			CommandTrade.closeTrade(this.player);
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

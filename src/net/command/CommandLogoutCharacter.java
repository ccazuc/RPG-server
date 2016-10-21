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
		sendOfflineToFriend(this.player);
		setPlayerOfflineInDB(this.player);
		Server.addLoggedPlayer(this.player);
		Server.removeInGamePlayer(this.player);
		this.player.resetDatas();
		CommandTrade.closeTrade(this.player);
	}
	
	public static void sendOfflineToFriend(Player player) {
		int i = 0;
		while(i < Server.getFriendMap().get(player.getCharacterId()).size()) {
			if(Server.getInGamePlayerList().containsKey(Server.getFriendMap().get(player.getCharacterId()).get(i))) {
				writeOfflineMessage(Server.getInGamePlayerList().get(Server.getFriendMap().get(player.getCharacterId()).get(i)), player.getName());
				Server.getFriendMap().get(player.getCharacterId()).remove(i);
			}
			i++;
		}
	}
	
	private static void writeOfflineMessage(Player player, String name) {
		player.getConnection().writeByte(PacketID.FRIEND);
		player.getConnection().writeByte(PacketID.FRIEND_OFFLINE);
		player.getConnection().writeString(name);
		player.getConnection().send();
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

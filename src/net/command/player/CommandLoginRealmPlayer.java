package net.command.player;

import net.Server;
import net.command.Command;
import net.command.auth.CommandPlayerIsLoggedOnWorldServer;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.AccountRank;
import net.game.Player;
import net.game.log.Log;

public class CommandLoginRealmPlayer extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		short packetId = connection.readShort();
		if(packetId == PacketID.LOGIN_REALM_REQUEST) {
			double key = connection.readDouble();
			int account_id = connection.readInt();
			if(!Server.hasKey(key, account_id)) {
				Log.write(player, "Unknown loggin key");
				player.close();
				return;
			}
			if(!Server.isAcceptingConnection()) {
				connectionRefused(connection);
				return;
			}
			connectionAccepted(connection);
			player.setAccountRank(AccountRank.values()[Server.getKey(key).getAccountRank()-1]);
			player.setAccountId(account_id);
			player.setAccountName(Server.getKey(key).getAccountName());
			Server.addLoggedPlayer(player);
			Server.removeNonLoggedPlayer(player);
			Server.removeKey(key);
			CommandPlayerIsLoggedOnWorldServer.write(player, true);
		}
	}
	
	private static void connectionRefused(Connection connection) {
		connection.writeShort(PacketID.LOGIN_REALM);
		connection.writeShort(PacketID.LOGIN_REALM_DOESNT_ACCEPT_CONNECTION);
		connection.send();
	}
	
	private static void connectionAccepted(Connection connection) {
		connection.writeShort(PacketID.LOGIN_REALM);
		connection.writeShort(PacketID.LOGIN_REALM_SUCCESS);
		connection.send();
	}
}

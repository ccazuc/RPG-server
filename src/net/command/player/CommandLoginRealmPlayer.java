package net.command.player;

import net.Server;
import net.command.Command;
import net.command.auth.CommandPlayerIsLoggedOnWorldServer;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.AccountRank;
import net.game.Player;

public class CommandLoginRealmPlayer extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		short packetId = connection.readShort();
		if(packetId == PacketID.LOGIN_REALM_REQUEST) {
			double key = connection.readDouble();
			int account_id = connection.readInt();
			if(!Server.hasKey(key, account_id)) {
				player.close();
				return;
			}
			connectionAccepted(connection);
			player.setAccountRank(AccountRank.values()[Server.getKey(key).getAccountRank()]);
			player.setAccountId(account_id);
			Server.addLoggedPlayer(player);
			Server.removeNonLoggedPlayer(player);
			Server.removeKey(key);
			CommandPlayerIsLoggedOnWorldServer.write(player, true);
		}
	}
	
	private static void connectionAccepted(Connection connection) {
		connection.writeShort(PacketID.LOGIN_REALM);
		connection.writeShort(PacketID.LOGIN_REALM_SUCCESS);
		connection.send();
	}
}

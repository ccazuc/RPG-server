package net.command.player;

import net.Server;
import net.command.Command;
import net.command.auth.CommandPlayerIsLoggedOnWorldServer;
import net.config.ConfigMgr;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.AccountRank;
import net.game.log.Log;
import net.game.manager.LoginQueueMgr;
import net.game.unit.Player;

public class CommandLoginRealmPlayer extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		short packetId = connection.readShort();
		if(packetId == PacketID.LOGIN_REALM_REQUEST) {
			double key = connection.readDouble();
			int account_id = connection.readInt();
			System.out.println("Loggin requestyed");
			if(!Server.hasKey(key, account_id)) {
				Log.writePlayerLog(player, "Unknown loggin key");
				player.close();
				return;
			}
			if(!Server.isAcceptingConnection()) {
				connectionRefused(connection);
				return;
			}
			player.setAccountRank(AccountRank.values()[Server.getKey(key).getAccountRank()-1]);
			player.setAccountId(account_id);
			player.setAccountName(Server.getKey(key).getAccountName());
			if (Server.getInGamePlayerList().size() + Server.getLoggedPlayerList().size() >= ConfigMgr.GetServerMaxCapacity())
			{
				LoginQueueMgr.addPlayerInQueue(player);
				CommandLoginQueue.playerAddedInQueue(player);
			}
			else
			{
				connectionAccepted(connection);
				Server.addLoggedPlayer(player);
				Server.removeNonLoggedPlayer(player);
				System.out.println("Connection accepted");
			}
			CommandPlayerIsLoggedOnWorldServer.write(player, true);
			Server.removeKey(key);
		}
	}
	
	private static void connectionRefused(Connection connection) {
		connection.startPacket();
		connection.writeShort(PacketID.LOGIN_REALM);
		connection.writeShort(PacketID.LOGIN_REALM_DOESNT_ACCEPT_CONNECTION);
		connection.endPacket();
		connection.send();
	}
	
	private static void connectionAccepted(Connection connection) {
		connection.startPacket();
		connection.writeShort(PacketID.LOGIN_REALM);
		connection.writeShort(PacketID.LOGIN_REALM_SUCCESS);
		connection.endPacket();
		connection.send();
	}
}

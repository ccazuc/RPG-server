package net.command.player;

import net.Server;
import net.command.Command;
import net.command.auth.CommandPlayerIsLoggedOnWorldServer;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.AccountRank;
import net.game.log.Log;
import net.game.manager.LoginQueueMgr;
import net.game.unit.Player;

public class CommandLoginRealmPlayer extends Command {

	public CommandLoginRealmPlayer(String name, boolean debug)
	{
		super(name, debug);
	}
	
	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		short packetId = connection.readShort();
		if(packetId == PacketID.LOGIN_REALM_REQUEST) {
			double key = connection.readDouble();
			int account_id = connection.readInt();
			//System.out.println("Key: " + key);
			System.out.println(player.getIpAdress());
			if(!Server.hasKey(player, key, account_id)) {
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
			//System.out.println("Online player : " + (Server.getInGamePlayerList().size() + Server.getLoggedPlayerList().size()) + ", InGame: " + Server.getInGamePlayerList().size() + ", Logged: " + Server.getLoggedPlayerList().size());
			if (LoginQueueMgr.isServerFull())
			{
				Server.removeNonLoggedPlayer(player);
				LoginQueueMgr.addPlayerInQueue(player);
			}
			else
			{
				connectionAccepted(connection);
				Server.removeNonLoggedPlayer(player);
				Server.addLoggedPlayer(player);
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

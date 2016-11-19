package net.command;

import net.Server;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.Player;

public class CommandLoginRealmPlayer extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		byte packetId = connection.readByte();
		if(packetId == PacketID.LOGIN_REALM_REQUEST) {
			double key = connection.readDouble();
			int account_id = connection.readInt();
			if(Server.hasKey(key, account_id)) {
				connectionAccepted(connection);
				Server.removeKey(key);
				player.setAccountId(account_id);
				Server.addLoggedPlayer(player);
				Server.removeNonLoggedPlayer(player);
			}
			else {
				player.close();
			}
		}
	}
	
	private static void connectionAccepted(Connection connection) {
		connection.writeByte(PacketID.LOGIN_REALM);
		connection.writeByte(PacketID.LOGIN_REALM_SUCCESS);
		connection.send();
	}
}

package net.command.auth;

import net.command.Command;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.Player;

public class CommandPlayerIsLoggedOnWorldServer extends Command {
	
	public static void write(Player player, boolean we) {
		ConnectionManager.getAuthConnection().startPacket();
		ConnectionManager.getAuthConnection().writeShort(PacketID.PLAYER_LOGGED_WORLD_SERVER);
		ConnectionManager.getAuthConnection().writeInt(player.getAccountId());
		ConnectionManager.getAuthConnection().writeBoolean(we);
		ConnectionManager.getAuthConnection().endPacket();
		ConnectionManager.getAuthConnection().send();
	}
}

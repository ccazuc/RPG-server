package net.command.auth;

import net.command.Command;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.unit.Player;

public class CommandPlayLoggedOut extends Command {

	public static void write(Player player) {
		ConnectionManager.getAuthConnection().startPacket();
		ConnectionManager.getAuthConnection().writeShort(PacketID.PLAYER_LOGGED_OUT);
		ConnectionManager.getAuthConnection().writeInt(player.getAccountId());
		ConnectionManager.getAuthConnection().endPacket();
		ConnectionManager.getAuthConnection().send();
	}
}

package net.command.player;

import net.command.Command;
import net.connection.PacketID;
import net.game.Player;

public class CommandUpdateStats extends Command {
	
	public static void write(Player player, short packetID, int id, int value) {
		player.getConnection().writeShort(PacketID.UPDATE_STATS);
		player.getConnection().writeShort(packetID);
		player.getConnection().writeInt(id);
		player.getConnection().writeInt(value);
		player.getConnection().send();
	}
}

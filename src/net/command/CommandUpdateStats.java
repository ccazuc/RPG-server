package net.command;

import net.connection.PacketID;
import net.game.Player;

public class CommandUpdateStats extends Command {

	public CommandUpdateStats() {}
	
	public static void write(Player player, byte packetID, int id, int value) {
		player.getConnection().writeByte(PacketID.UPDATE_STATS);
		player.getConnection().writeByte(packetID);
		player.getConnection().writeInt(id);
		player.getConnection().writeInt(value);
		player.getConnection().send();
	}
}

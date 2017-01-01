package net.command.player;

import net.command.Command;
import net.connection.PacketID;
import net.game.Player;

public class CommandUpdateStats extends Command {
	
	public static void write(Player player, short packetID, int id, int value) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.UPDATE_STATS);
		player.getConnection().writeShort(packetID);
		player.getConnection().writeInt(id);
		player.getConnection().writeInt(value);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	public static void updateStamina(Player player, int unitID, int value) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.UPDATE_STATS);
		player.getConnection().writeShort(PacketID.UPDATE_STATS_STAMINA);
		player.getConnection().writeInt(unitID);
		player.getConnection().writeInt(value);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
}

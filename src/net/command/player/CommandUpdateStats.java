package net.command.player;

import net.command.Command;
import net.connection.PacketID;
import net.game.unit.Player;

public class CommandUpdateStats extends Command {
	
	public CommandUpdateStats(String name, boolean debug)
	{
		super(name, debug);
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
	
	public static void updateMana(Player player, int unitID, int value) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.UPDATE_STATS);
		player.getConnection().writeShort(PacketID.UPDATE_STATS_MANA);
		player.getConnection().writeInt(unitID);
		player.getConnection().writeInt(value);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	public static void updateMaxStamina(Player player, int unitID, int value) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.UPDATE_STATS);
		player.getConnection().writeShort(PacketID.UPDATE_STATS_MAX_STAMINA);
		player.getConnection().writeInt(unitID);
		player.getConnection().writeInt(value);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
}

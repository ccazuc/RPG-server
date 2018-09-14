package net.command.player;

import net.connection.Connection;
import net.connection.PacketID;
import net.game.unit.Player;

public class CommandSendCharacterStats
{

	public static void write(Player player)
	{
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.LOAD_STATS);
		connection.writeInt(player.getUnitID());
		connection.writeLong(player.getExp());
		connection.writeLong(player.getGold());
		connection.writeInt(player.getAccountRank().getValue());
		connection.endPacket();
		connection.send();
	}
}

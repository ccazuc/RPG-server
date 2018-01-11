package net.command.player;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.manager.LoginQueueMgr;
import net.game.unit.Player;

public class CommandLoginQueue extends Command {

	@Override
	public void read(Player player)
	{
		Connection connection = player.getConnection();
		short packetId = connection.readShort();
		//if (packetId == PacketID.LOGIN_QUEUE_ACCEPTED)
		//{
			//if (!player.getLoginQueueRequestSent())
				return;
			//LoginQueueMgr.loginAccepted(player);
		//}
		
	}
	
	public static void playerAddedInQueue(Player player)
	{
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.LOGIN_QUEUE);
		connection.writeShort(PacketID.LOGIN_QUEUE_JOINED);
		connection.writeInt(LoginQueueMgr.getLoginQueueList().size());
		connection.endPacket();
		connection.send();
	}
	
	public static void updatePosition(Player player, int position, int queueLength)
	{
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.LOGIN_QUEUE);
		connection.writeShort(PacketID.LOGIN_QUEUE_UPDATE_POSITION);
		connection.writeInt(position);
		connection.writeInt(queueLength);
		connection.endPacket();
		connection.send();
	}
}

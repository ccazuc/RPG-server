package net.command.item;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.item.stuff.StuffManager;
import net.game.unit.Player;

public class CommandStuff extends Command {

	public CommandStuff(String name, boolean debug)
	{
		super(name, debug);
	}
	
	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		int id = connection.readInt();
		if(StuffManager.exists(id)) {
			connection.startPacket();
			connection.writeShort(PacketID.STUFF);
			connection.writeStuff(StuffManager.getStuff(id));
			connection.endPacket();
			connection.send();
		}
	}
}

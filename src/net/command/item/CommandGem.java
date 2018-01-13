package net.command.item;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.item.gem.GemManager;
import net.game.unit.Player;;

public class CommandGem extends Command {

	public CommandGem(String name, boolean debug)
	{
		super(name, debug);
	}
	
	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		int id = connection.readInt();
		if(GemManager.exists(id)) {
			connection.startPacket();
			connection.writeShort(PacketID.GEM);
			connection.writeGem(GemManager.getGem(id));
			connection.endPacket();
			connection.send();
		}
	}
}

package net.command.player;

import net.command.Command;
import net.connection.PacketID;
import net.game.unit.Player;

public class CommandMail extends Command {

	@Override
	public void read(Player player) {
		short packetId = player.getConnection().readShort();
		if(packetId == PacketID.MAIL_DELETE) {
			
		}
	}
}

package net.command.chat;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.Player;

public class CommandPlayerNotFound extends Command {

	public CommandPlayerNotFound() {}
	
	@Override
	public void read(Player player) {
		
	}
	
	public static void write(Connection connection, String name) {
		connection.writeByte(PacketID.PLAYER_NOT_FOUND);
		connection.writeString(name);
		connection.send();
	}
}

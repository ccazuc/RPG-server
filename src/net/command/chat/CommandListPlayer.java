package net.command.chat;

import net.Server;
import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.Player;

public class CommandListPlayer extends Command {

	public CommandListPlayer() {}
	
	@Override
	public void read(Player player) {
		write(player.getConnection());
	}

	public static void write(Connection connection) {
		connection.writeByte(PacketID.CHAT_LIST_PLAYER);
		connection.writeInt(Server.getInGamePlayerList().size());
		for(Player player : Server.getInGamePlayerList().values()) {
			connection.writeString(player.getName()+" "+Player.convClassTypeToString(player.getClasse())+" level "+player.getLevel());
			connection.writeChar(player.getClasse().getValue());
		}
		connection.send();
	}
}

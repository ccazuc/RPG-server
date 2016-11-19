package net.command.chat;

import net.Server;
import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.Player;

public class CommandPlayerInfo extends Command {

	public CommandPlayerInfo() {}

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		int id = connection.readInt();
		if(player.getAccountRank() >= 1) {
			if(id == player.getCharacterId()) {
				write(player);
			}
			else {
				write(Server.getInGameCharacter(id));
			}
		}
		else {
			connection.writeByte(PacketID.CHAT_NOT_ALLOWED);
			connection.send();
		}
	}
	
	public void write(Player player) {
		if(player != null) {
			Connection connection = player.getConnection();
			connection.writeString(player.getName());
			connection.writeInt(player.getAccountId());
			connection.writeInt(player.getAccountRank());
			connection.writeString(player.getIpAdress());
			connection.send();
		}
	}
}

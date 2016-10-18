package net.command.chat;

import net.Server;
import net.command.Command;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.Player;

public class CommandPlayerInfo extends Command {

	public CommandPlayerInfo(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	@Override
	public void read() {
		int id = this.connection.readInt();
		if(this.player.getAccountRank() >= 1) {
			if(id == this.player.getCharacterId()) {
				write(this.player);
			}
			else {
				write(Server.getPlayerList().get(id));
			}
		}
		else {
			this.connection.writeByte(PacketID.CHAT_NOT_ALLOWED);
			this.connection.send();
		}
	}
	
	public void write(Player player) {
		this.connection.writeString(player.getName());
		this.connection.writeInt(player.getAccountId());
		this.connection.writeInt(player.getAccountRank());
		this.connection.writeString(player.getIpAdresse());
		this.connection.send();
	}
}

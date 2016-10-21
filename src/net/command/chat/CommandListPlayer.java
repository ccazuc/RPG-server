package net.command.chat;

import net.Server;
import net.command.Command;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.Player;

public class CommandListPlayer extends Command {

	public CommandListPlayer(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	@Override
	public void read() {
		write();
	}
	
	@Override
	public void write() {
		this.connection.writeByte(PacketID.CHAT_LIST_PLAYER);
		this.connection.writeInt(Server.getInGamePlayerList().size());
		for(Player player : Server.getInGamePlayerList().values()) {
			this.connection.writeString(player.getName()+" "+Player.convClassTypeToString(player.getClasse())+" level "+player.getLevel());
			this.connection.writeChar(player.getClasse().getValue());
		}
		this.connection.send();
	}
}

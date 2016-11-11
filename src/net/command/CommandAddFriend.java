package net.command;

import net.Server;
import net.command.chat.CommandPlayerNotFound;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.Player;

public class CommandAddFriend extends Command {

	public CommandAddFriend(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	@Override
	public void read() {
		String name = this.connection.readString();
		if(name.length() > 2) {
			name = name.substring(0, 1).toUpperCase()+name.substring(1).toLowerCase();
			Player player = Server.getCharacter(name);
			if(player != null) {
				this.player.addFriend(player.getCharacterId());
				writeAddFriend(this.player, player);
			}
		}
		else {
			CommandPlayerNotFound.write(this.connection, name);
		}
	}
	
	private static void writeAddFriend(Player player, Player friend) {
		player.getConnection().writeByte(PacketID.FRIEND);
		player.getConnection().writeByte(PacketID.FRIEND_SEND_INFO);
		player.getConnection().writeString(friend.getName());
		player.getConnection().writeInt(friend.getLevel());
		player.getConnection().writeChar(friend.getRace().getValue());
		player.getConnection().writeChar(friend.getClasse().getValue());
		player.getConnection().send();
	}
}

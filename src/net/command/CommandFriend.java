package net.command;

import net.Servers;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.Player;

public class CommandFriend extends Command {

	public CommandFriend(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	@Override
	public void read() {
		byte packetId = this.connection.readByte();
		if(packetId == PacketID.FRIEND_ADD) {
			String name = this.connection.readString();
			Player player = Servers.getCharacter(name);
			if(player != null) {
				if(!name.equals(this.player.getName())) {
					if(this.player.addFriend(player.getCharacterId())) {
						writeAddFriend(this.player, player);
					}
					else {
						//friend list full
					}
				}
				else {
					//can't add yourself as friend
				}
			}
			else {
				//send character not found
			}
		}
	}
	
	private static void writeAddFriend(Player player, Player friend) {
		player.getConnection().writeByte(PacketID.FRIEND);
		player.getConnection().writeByte(PacketID.FRIEND_ADD);
		player.getConnection().writeString(friend.getName());
		player.getConnection().writeInt(friend.getLevel());
		player.getConnection().writeChar(friend.getRace().getValue());
		player.getConnection().writeChar(friend.getClasse().getValue());
		player.getConnection().send();
	}
}

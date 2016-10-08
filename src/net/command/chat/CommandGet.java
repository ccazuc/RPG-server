package net.command.chat;

import net.Server;
import net.command.Command;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.Player;

public class CommandGet extends Command {

	public CommandGet(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	@Override
	public void read() {
		byte packetID = this.connection.readByte();
		if(packetID == PacketID.CHAT_GET_STAMINA) {
			int id = this.connection.readInt();
			write((id == this.player.getCharacterId() ? this.player : Server.getPlayerList().get(id)).getStamina());
		}
		else if(packetID == PacketID.CHAT_GET_MANA) {
			int id = this.connection.readInt();
			write((id == this.player.getCharacterId() ? this.player : Server.getPlayerList().get(id)).getMana());
		}
		else if(packetID == PacketID.CHAT_GET_EXPERIENCE) {
			int id = this.connection.readInt();
			write((id == this.player.getCharacterId() ? this.player : Server.getPlayerList().get(id)).getExp());
		}
		else if(packetID == PacketID.CHAT_GET_GOLD) {
			int id = this.connection.readInt();
			write((id == this.player.getCharacterId() ? this.player : Server.getPlayerList().get(id)).getGold());
		}
		else if(packetID == PacketID.CHAT_GET_ID) {
			String name = this.connection.readString();
			for(Player player : Server.getPlayerList().values()) {
				if(player.getName().equals(name)) {
					write(player.getCharacterId());
					return;
				}
			}
		}
	}
	
	public void write(int value) {
		this.connection.writeByte(PacketID.CHAT_GET);
		this.connection.writeInt(value);
		this.connection.send();
	}
}

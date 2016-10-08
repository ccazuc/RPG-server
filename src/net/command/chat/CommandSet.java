package net.command.chat;

import net.Server;
import net.command.Command;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.Player;

public class CommandSet extends Command {
	
	public CommandSet(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	@Override
	public void read() {
		byte packetID = this.connection.readByte();
		int value = this.connection.readInt();
		int id = this.connection.readInt();
		Player player = id == this.player.getCharacterId() ? this.player : Server.getPlayerList().get(id);
		if(this.player.getAccountRank() >= 1) {
			if(packetID == PacketID.CHAT_SET_STAMINA) {
				if(value >= 0 && value <= this.player.getMaxStamina()) {
					player.setStamina(value);
				}
			}
			else if(packetID == PacketID.CHAT_SET_MANA) {
				if(value >= 0 && value <= this.player.getMaxMana()) {
					player.setMana(value);
				}
			}
			else if(packetID == PacketID.CHAT_SET_EXPERIENCE) {
				if(value >= 0) {
					player.setExperience(value);
				}
			}
			else if(packetID == PacketID.CHAT_SET_GOLD) {
				if(value <= 0) {
					player.setGold(value);
				}
			}
		}
		else {
			this.connection.writeByte(PacketID.CHAT_NOT_ALLOWED);
			this.connection.send();
		}
	}
}

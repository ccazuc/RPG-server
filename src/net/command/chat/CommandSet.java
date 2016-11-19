package net.command.chat;

import net.Server;
import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.Player;

public class CommandSet extends Command {
	
	public CommandSet() {
	}
	
	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		byte packetID = connection.readByte();
		int id = connection.readInt();
		int value = connection.readInt();
		Player member = id == player.getCharacterId() ? player : Server.getInGameCharacter(id);
		if(member != null) {
			if(player.getAccountRank() >= 1) {
				if(packetID == PacketID.CHAT_SET_STAMINA) {
					if(value >= 0 && value <= player.getMaxStamina()) {
						member.setStamina(value);
					}
				}
				else if(packetID == PacketID.CHAT_SET_MANA) {
					if(value >= 0 && value <= player.getMaxMana()) {
						member.setMana(value);
					}
				}
				else if(packetID == PacketID.CHAT_SET_EXPERIENCE) {
					if(value >= 0) {
						member.setExperience(value);
					}
				}
				else if(packetID == PacketID.CHAT_SET_GOLD) {
					if(value >= 0) {
						member.setGold(value);
					}
				}
			}
			else {
				connection.writeByte(PacketID.CHAT_NOT_ALLOWED);
				connection.send();
			}
		}
	}
}

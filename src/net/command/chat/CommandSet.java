package net.command.chat;

import net.Server;
import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.AccountRank;
import net.game.unit.Player;

public class CommandSet extends Command {
	
	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		short packetID = connection.readShort();
		int id = connection.readInt();
		int value = connection.readInt();
		Player member = id == player.getUnitID() ? player : Server.getInGameCharacter(id);
		if(member == null) {
			return;
		}
		/*if(player.getAccountRank().getValue() >= AccountRank.GAMEMASTER.getValue()) {
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
			connection.writeShort(PacketID.CHAT_NOT_ALLOWED);
			connection.send();
		}*/
	}
}

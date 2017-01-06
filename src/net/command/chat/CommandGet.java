package net.command.chat;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Server;
import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.unit.Player;

public class CommandGet extends Command {

	private static JDOStatement statement;
	
	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		short packetID = connection.readShort();
		if(packetID == PacketID.CHAT_GET_STAMINA) {
			int id = connection.readInt();
			Player member = id == player.getCharacterId() ? player : Server.getInGameCharacter(id);
			if(member != null) {
				write(connection, member.getStamina());
			}
		}
		else if(packetID == PacketID.CHAT_GET_MANA) {
			int id = connection.readInt();
			Player member = id == player.getCharacterId() ? player : Server.getInGameCharacter(id);
			if(member != null) {
				write(connection, member.getMana());
			}
		}
		else if(packetID == PacketID.CHAT_GET_EXPERIENCE) {
			int id = connection.readInt();
			Player member = id == player.getCharacterId() ? player : Server.getInGameCharacter(id);
			if(member != null) {
				write(connection, member.getExp());
			}
		}
		else if(packetID == PacketID.CHAT_GET_GOLD) {
			int id = connection.readInt();
			Player member = id == player.getCharacterId() ? player : Server.getInGameCharacter(id);
			if(member != null) {
				write(connection, member.getGold());
			}
		}
		else if(packetID == PacketID.CHAT_GET_ID) {
			String name = connection.readString();
			try {
				if(statement == null) {
					statement = Server.getJDO().prepare("SELECT character_id FROM `character` WHERE name = ?");
				}
				statement.clear();
				statement.putString(name);
				statement.execute();
				if(statement.fetch()) {
					write(connection, statement.getInt());
				}
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
		else if(packetID == PacketID.CHAT_GET_IP) {
			int id = connection.readInt();
			Player member = id == player.getCharacterId() ? player : Server.getInGameCharacter(id);
			if(member != null) {
				write(connection, member.getIpAdress().substring(1));
			}
		}
	}
	
	public void write(Connection connection, int value) {
		connection.startPacket();
		connection.writeShort(PacketID.CHAT_GET);
		connection.writeShort(PacketID.INT);
		connection.writeInt(value);
		connection.endPacket();
		connection.send();
	}
	
	public void write(Connection connection, String msg) {
		connection.startPacket();
		connection.writeShort(PacketID.CHAT_GET);
		connection.writeShort(PacketID.STRING);
		connection.writeString(msg);
		connection.endPacket();
		connection.send();
	}
}

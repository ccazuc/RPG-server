package net.command.chat;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Server;
import net.command.Command;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.Player;

public class CommandGet extends Command {

	private static JDOStatement statement;
	public CommandGet(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	@Override
	public void read() {
		byte packetID = this.connection.readByte();
		if(packetID == PacketID.CHAT_GET_STAMINA) {
			int id = this.connection.readInt();
			Player player = id == this.player.getCharacterId() ? this.player : Server.getCharacter(id);
			if(player != null) {
				write(player.getStamina());
			}
		}
		else if(packetID == PacketID.CHAT_GET_MANA) {
			int id = this.connection.readInt();
			Player player = id == this.player.getCharacterId() ? this.player : Server.getCharacter(id);
			if(player != null) {
				write(player.getMana());
			}
		}
		else if(packetID == PacketID.CHAT_GET_EXPERIENCE) {
			int id = this.connection.readInt();
			Player player = id == this.player.getCharacterId() ? this.player : Server.getCharacter(id);
			if(player != null) {
				write(player.getExp());
			}
		}
		else if(packetID == PacketID.CHAT_GET_GOLD) {
			int id = this.connection.readInt();
			Player player = id == this.player.getCharacterId() ? this.player : Server.getCharacter(id);
			if(player != null) {
				write(player.getGold());
			}
		}
		else if(packetID == PacketID.CHAT_GET_ID) {
			String name = this.connection.readString();
			try {
				if(statement == null) {
					statement = Server.getJDO().prepare("SELECT character_id FROM `character` WHERE name = ?");
				}
				statement.clear();
				statement.putString(name);
				statement.execute();
				if(statement.fetch()) {
					write(statement.getInt());
				}
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
		else if(packetID == PacketID.CHAT_GET_IP) {
			int id = this.connection.readInt();
			Player player = id == this.player.getCharacterId() ? this.player : Server.getCharacter(id);
			if(player != null) {
				write(player.getIpAdress().substring(1));
			}
		}
	}
	
	public void write(int value) {
		this.connection.writeByte(PacketID.CHAT_GET);
		this.connection.writeByte(PacketID.INT);
		this.connection.writeInt(value);
		this.connection.send();
	}
	
	public void write(String msg) {
		this.connection.writeByte(PacketID.CHAT_GET);
		this.connection.writeByte(PacketID.STRING);
		this.connection.writeString(msg);
		this.connection.send();
		
	}
}

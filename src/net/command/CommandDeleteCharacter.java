package net.command;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Server;
import net.connection.ConnectionManager;
import net.connection.PacketID;

public class CommandDeleteCharacter extends Command {
	
	private static JDOStatement delete_character;
	
	public CommandDeleteCharacter(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	public void read() {
		if(delete_character == null) {
			try {
				delete_character = Server.getJDO().prepare("DELETE FROM `character` WHERE character_id = ?");
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		delete_character.clear();
		int id = this.connection.readInt();
		try {
			delete_character.putInt(id);
			delete_character.execute();
			this.connection.writeByte(PacketID.DELETE_CHARACTER);
			this.connection.send();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
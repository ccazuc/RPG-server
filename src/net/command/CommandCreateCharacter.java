package net.command;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Servers;
import net.connection.ConnectionManager;
import net.connection.PacketID;

public class CommandCreateCharacter extends Command {
	
	private static JDOStatement create_character;
	private static JDOStatement check_character;
	
	public CommandCreateCharacter(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	@Override
	public void read() {
		if(create_character == null) {
			try {
				create_character = Servers.getJDO().prepare("INSERT INTO `character` (account_id, name, level, class, race) VALUES (?, ?, 1, ?, ?)");
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
		create_character.clear();
		String name = this.connection.readString();
		int accountId = this.connection.readInt();
		String classe = this.connection.readString();
		String race = this.connection.readString();
		try {
			if(checkCharacterName(name)) {
				create_character.putInt(accountId);
				create_character.putString(name.substring(0, 1).toUpperCase()+name.substring(1).toLowerCase());
				create_character.putString(classe);
				create_character.putString(race);
				create_character.execute();
				this.connection.writeByte(PacketID.CREATE_CHARACTER);
				this.connection.writeByte(PacketID.CHARACTER_CREATED);
				this.connection.send();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private boolean checkCharacterName(String name) throws SQLException {
		int i = 0;
		if(name.length() >= 2 && name.length() <= 10) {
			while(i < name.length()) {
				char temp = name.charAt(i);
				if(!((temp >= 'A' && temp <= 'Z') || (temp >= 'a' && temp <= 'z')) && temp != 'é' && temp != 'è' && temp != 'ç' && temp != 'à' && temp != 'ê' && temp != 'â' && temp != 'û' && temp != 'ë' && temp != 'ä' && temp != 'ü') {
					this.connection.writeByte(PacketID.CREATE_CHARACTER);
					this.connection.writeByte(PacketID.ERROR_NAME_ALPHABET);
					this.connection.send();
					return false;
				}
				if(i < name.length()-3) {
					if(name.charAt(i) == name.charAt(i+1) && name.charAt(i+1) == name.charAt(i+2)) {
						return false;
					}
				}
				i++;
			}
		}
		else {
			this.connection.writeByte(PacketID.CREATE_CHARACTER);
			this.connection.writeByte(PacketID.ERROR_NAME_LENGTH);
			this.connection.send();
			return false;
		}
		if(check_character == null) {
			check_character = Servers.getJDO().prepare("SELECT character_id FROM `character` WHERE name = ?");
		}
		check_character.clear();
		check_character.putString(name);
		check_character.execute();
		if(check_character.fetch()) {
			int id = check_character.getInt();
			if(id != 0) {	
				this.connection.writeByte(PacketID.CREATE_CHARACTER);
				this.connection.writeByte(PacketID.ERROR_NAME_ALREADY_TAKEN);
				this.connection.send();
				return false;
			}
		}
		return true;
	}
}

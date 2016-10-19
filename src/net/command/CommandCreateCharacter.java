package net.command;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Server;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.sql.SQLRequest;

public class CommandCreateCharacter extends Command {
	
	private static JDOStatement create_character;
	private static JDOStatement check_character;
	private static JDOStatement character_id;
	private static SQLRequest insert_bag = new SQLRequest("INSERT INTO `bag` (character_id) VALUES (?)") {
		@Override
		public void gatherData() {
			this.statement.clear();
			try {
				this.statement.putInt(this.id);
				this.statement.execute();
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	};
	private static SQLRequest character_containers = new SQLRequest("INSERT INTO character_containers (character_id) VALUES (?)") {
		@Override
		public void gatherData() {
			this.statement.clear();
			try {
				this.statement.putInt(this.id);
				this.statement.execute();
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	};
	private static SQLRequest character_stuff = new SQLRequest("INSERT INTO character_stuff (character_id) VALUES (?)") {
		@Override
		public void gatherData() {
			this.statement.clear();
			try {
				this.statement.putInt(this.id);
				this.statement.execute();
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	};
	private static SQLRequest spellbar = new SQLRequest("INSERT INTO spellbar (character_id) VALUES (?)") {
		@Override
		public void gatherData() {
			this.statement.clear();
			try {
				this.statement.putInt(this.id);
				this.statement.execute();
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	};
	
	public CommandCreateCharacter(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	@Override
	public void read() {
		if(create_character == null) {
			try {
				create_character = Server.getJDO().prepare("INSERT INTO `character` (account_id, name, class, race, experience, gold) VALUES (?, ?, ?, ?, 0, 1000000)");
				character_id = Server.getJDO().prepare("SELECT character_id FROM `character` WHERE name = ?");
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
		int characterId = 0;
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
				
				character_id.clear();
				character_id.putString(name.substring(0, 1).toUpperCase()+name.substring(1).toLowerCase());
				character_id.execute();
				if(character_id.fetch()) {
					characterId = character_id.getInt();
				}
				if(characterId != 0) {
					insert_bag.setId(characterId);
					Server.addNewRequest(insert_bag);
					character_containers.setId(characterId);
					Server.addNewRequest(character_containers);
					character_stuff.setId(characterId);
					Server.addNewRequest(character_stuff);
					spellbar.setId(characterId);
					Server.addNewRequest(spellbar);
				}
				
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
			check_character = Server.getJDO().prepare("SELECT character_id FROM `character` WHERE name = ?");
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

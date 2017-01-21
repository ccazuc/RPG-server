package net.command.player;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Server;
import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.log.Log;
import net.game.unit.Player;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;
import net.thread.sql.SQLRequestPriority;
import net.utils.StringUtils;

public class CommandCreateCharacter extends Command {
	
	private static JDOStatement create_character;
	private static JDOStatement check_character;
	private static JDOStatement character_id;
	private static SQLRequest insert_bag = new SQLRequest("INSERT INTO `bag` (character_id) VALUES (?)", "Create character insert_bag", SQLRequestPriority.HIGH) {
		@Override
		public void gatherData() {
			this.statement.clear();
			try {
				this.statement.putInt(this.datasList.get(0).getIValue1());
				this.statement.execute();
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	};
	private static SQLRequest character_containers = new SQLRequest("INSERT INTO character_containers (character_id) VALUES (?)", "Create character character_containers", SQLRequestPriority.HIGH) {
		@Override
		public void gatherData() {
			this.statement.clear();
			try {
				this.statement.putInt(this.datasList.get(0).getIValue1());
				this.statement.execute();
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	};
	private static SQLRequest character_stuff = new SQLRequest("INSERT INTO character_stuff (character_id) VALUES (?)", "Create character character_stuff", SQLRequestPriority.HIGH) {
		@Override
		public void gatherData() {
			this.statement.clear();
			try {
				this.statement.putInt(this.datasList.get(0).getIValue1());
				this.statement.execute();
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	};
	private static SQLRequest spellbar = new SQLRequest("INSERT INTO spellbar (character_id) VALUES (?)", "Create character spellbar", SQLRequestPriority.HIGH) {
		@Override
		public void gatherData() {
			this.statement.clear();
			try {
				this.statement.putInt(this.datasList.get(0).getIValue1());
				this.statement.execute();
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	};
	
	@Override
	public void read(Player player) {
		if(Server.getInGameCharacter(player.getUnitID()) != null) {
			Log.writePlayerLog(player, "Tried to create a character while beeing in-game");
			player.close();
			return;
		}
		Connection connection = player.getConnection();
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
		String name = connection.readString();
		name = StringUtils.formatPlayerName(name);
		String classe = connection.readString();
		String race = connection.readString();
		int characterId = 0;
		try {
			if(!checkCharacterName(connection, name)) {
				return;
			}
			create_character.putInt(player.getAccountId());
			create_character.putString(name);
			create_character.putString(classe);
			create_character.putString(race);
			create_character.execute();
			connection.writeShort(PacketID.CREATE_CHARACTER);
			connection.writeShort(PacketID.CHARACTER_CREATED);
			connection.send();
			
			character_id.clear();
			character_id.putString(name);
			character_id.execute();
			if(character_id.fetch()) {
				characterId = character_id.getInt();
			}
			if(characterId == 0) {
				return;
			}
			SQLDatas datas = new SQLDatas(characterId);
			insert_bag.addDatas(datas);
			Server.executeSQLRequest(insert_bag);
			character_containers.addDatas(datas);
			Server.executeSQLRequest(character_containers);
			character_stuff.addDatas(datas);
			Server.executeSQLRequest(character_stuff);
			spellbar.addDatas(datas);
			Server.executeSQLRequest(spellbar);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static boolean checkCharacterName(Connection connection, String name) throws SQLException {
		int i = 0;
		if(name.length() >= 2 && name.length() <= 10) {
			while(i < name.length()) {
				char temp = name.charAt(i);
				if(!((temp >= 'A' && temp <= 'Z') || (temp >= 'a' && temp <= 'z')) && temp != 'é' && temp != 'è' && temp != 'ç' && temp != 'à' && temp != 'ê' && temp != 'â' && temp != 'û' && temp != 'ë' && temp != 'ä' && temp != 'ü') {
					connection.startPacket();
					connection.writeShort(PacketID.CREATE_CHARACTER);
					connection.writeShort(PacketID.ERROR_NAME_ALPHABET);
					connection.endPacket();
					connection.send();
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
			connection.startPacket();
			connection.writeShort(PacketID.CREATE_CHARACTER);
			connection.writeShort(PacketID.ERROR_NAME_LENGTH);
			connection.endPacket();
			connection.send();
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
				connection.startPacket();
				connection.writeShort(PacketID.CREATE_CHARACTER);
				connection.writeShort(PacketID.ERROR_NAME_ALREADY_TAKEN);
				connection.endPacket();
				connection.send();
				return false;
			}
		}
		return true;
	}
}

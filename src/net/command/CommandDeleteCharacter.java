package net.command;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Server;
import net.connection.ConnectionManager;
import net.connection.PacketID;

public class CommandDeleteCharacter extends Command {
	
	private static JDOStatement delete_character;
	private static JDOStatement delete_bag;
	private static JDOStatement delete_containers;
	private static JDOStatement delete_stuff;
	private static JDOStatement delete_spellbar;
	private static JDOStatement check_character_account;
	
	public CommandDeleteCharacter(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	@Override
	public void read() {
		if(delete_character == null) {
			try {
				delete_character = Server.getJDO().prepare("DELETE FROM `character` WHERE character_id = ?");
				delete_bag = Server.getJDO().prepare("DELETE FROM bag WHERE character_id = ?");
				delete_containers = Server.getJDO().prepare("DELETE FROM character_containers WHERE character_id = ?");
				delete_stuff = Server.getJDO().prepare("DELETE FROM character_stuff WHERE character_id = ?");
				delete_spellbar = Server.getJDO().prepare("DELETE FROM spellbar WHERE character_id = ?");
				check_character_account = Server.getJDO().prepare("SELECT account_id FROM `character` WHERE character_id = ?");
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		delete_character.clear();
		int id = this.connection.readInt();
		try {
			check_character_account.clear();
			check_character_account.putInt(id);
			check_character_account.execute();
			if(check_character_account.fetch()) {
				if(this.player.getAccountId() == check_character_account.getInt()) {
					delete_character.putInt(id);
					delete_character.execute();
					this.connection.writeByte(PacketID.DELETE_CHARACTER);
					this.connection.send();
					delete_bag.clear();
					delete_bag.putInt(id);
					delete_bag.execute();
					delete_containers.clear();
					delete_containers.putInt(id);
					delete_containers.execute();
					delete_stuff.clear();
					delete_stuff.putInt(id);
					delete_stuff.execute();
					delete_spellbar.clear();
					delete_spellbar.putInt(id);
					delete_spellbar.execute();
				}
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
package net.command;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Servers;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.Player;


public class CommandSelectScreenLoadCharacters extends Command {
	
	private static JDOStatement write_statement;
	
	public CommandSelectScreenLoadCharacters(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	@Override
	public void read() {
		int accountId = this.connection.readInt();
		write(accountId);
	}
	
	private void write(int accountId) {
		try {
			if(write_statement == null) {
				write_statement = Servers.getJDO().prepare("SELECT character_id, name, experience, class, race FROM `character` WHERE account_id = ?");
			}
			boolean hasSendId = true;
			write_statement.clear();
			write_statement.putInt(accountId);
			write_statement.execute();
			while(write_statement.fetch()) {
				if(hasSendId) {
					this.connection.writeByte(PacketID.SELECT_SCREEN_LOAD_CHARACTERS);
					hasSendId = false;
				}
				int id = write_statement.getInt();
				String name = write_statement.getString();
				int level = Player.getLevel(write_statement.getInt());
				String classe = write_statement.getString();
				String race = write_statement.getString();
				this.connection.writeInt(id);
				this.connection.writeString(name);
				this.connection.writeInt(level);
				this.connection.writeString(classe);
				this.connection.writeString(race);
			}
			this.connection.send();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
}

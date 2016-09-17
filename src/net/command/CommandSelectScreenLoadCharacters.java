package net.command;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Server;
import net.connection.ConnectionManager;
import net.connection.PacketID;


public class CommandSelectScreenLoadCharacters extends Command {
	
	private static JDOStatement write_statement;
	
	public CommandSelectScreenLoadCharacters(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	public void read() {
		System.out.println("read select");
		int accountId = this.connection.readInt();
		write(accountId);
	}
	
	private void write(int accountId) {
		System.out.println("write select");
		try {
			if(write_statement == null) {
				write_statement = Server.getJDO().prepare("SELECT character_id, name, level, class, race FROM `character` WHERE account_id = ?");
			}
			boolean packet = true;
			write_statement.clear();
			write_statement.putInt(accountId);
			write_statement.execute();
			while(write_statement.fetch()) {
				if(packet) {
					this.connection.writeByte(PacketID.SELECT_SCREEN_LOAD_CHARACTERS);
					packet = false;
				}
				int id = write_statement.getInt();
				String name = write_statement.getString();
				int level = write_statement.getInt();
				String classe = write_statement.getString();
				String race = write_statement.getString();
				this.connection.writeInt(id);
				this.connection.writeString(name);
				this.connection.writeInt(level);
				this.connection.writeString(classe);
				this.connection.writeString(race);
				System.out.println(id+" "+name);
			}
			this.connection.send();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		
	}
}

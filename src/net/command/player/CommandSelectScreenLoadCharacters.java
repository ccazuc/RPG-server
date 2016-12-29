package net.command.player;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Server;
import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.Player;
import net.game.log.Log;


public class CommandSelectScreenLoadCharacters extends Command {
	
	private static JDOStatement write_statement;

	@Override
	public void read(Player player) {
		if(!player.isOnline()) {
			write(player, player.getAccountId());
		}
		else {
			Log.writePlayerLog(player, "tried to load select screen characters while beeing online");
		}
	}
	
	private static void write(Player player, int accountId) {
		try {
			long timer = System.nanoTime();
			Connection connection = player.getConnection();
			if(write_statement == null) {
				write_statement = Server.getJDO().prepare("SELECT character_id, name, experience, class, race FROM `character` WHERE account_id = ?");
			}
			boolean hasSendId = true;
			write_statement.clear();
			write_statement.putInt(accountId);
			write_statement.execute();
			while(write_statement.fetch()) {
				if(hasSendId) {
					connection.startPacket();
					connection.writeShort(PacketID.SELECT_SCREEN_LOAD_CHARACTERS);
					hasSendId = false;
				}
				int id = write_statement.getInt();
				String name = write_statement.getString();
				int level = Player.getLevel(write_statement.getInt());
				String classe = write_statement.getString();
				String race = write_statement.getString();
				connection.writeInt(id);
				connection.writeString(name);
				connection.writeInt(level);
				connection.writeString(classe);
				connection.writeString(race);
			}
			if(!hasSendId) {
				connection.endPacket();
				connection.send();
			}
			System.out.println("[SQL REQUEST] load characters took "+(System.nanoTime()-timer)/1000+" µs to execute.");
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
}

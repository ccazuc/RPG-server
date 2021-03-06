package net.command.player;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Server;
import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.log.Log;
import net.game.unit.Player;


public class CommandSelectScreenLoadCharacters extends Command
{
	
	private static JDOStatement write_statement; //TODO: move this out of Command

	public CommandSelectScreenLoadCharacters(String name, boolean debug)
	{
		super(name, debug);
	}
	
	@Override
	public void read(Player player)
	{
		if(!player.isOnline())
			write(player, player.getAccountId());
		else
			Log.writePlayerLog(player, "tried to load select screen characters while beeing online");
	}
	
	private static void write(Player player, int accountId)
	{
		try
		{
			long timer = System.nanoTime();
			Connection connection = player.getConnection();
			if(write_statement == null)
				write_statement = Server.getJDO().prepare("SELECT character_id, name, experience, class, race FROM `character` WHERE account_id = ?");
			write_statement.clear();
			write_statement.putInt(accountId);
			write_statement.execute();
			connection.startPacket();
			connection.writeShort(PacketID.SELECT_SCREEN_LOAD_CHARACTERS);
			while(write_statement.fetch())
			{
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
			connection.writeInt(-1);
			connection.endPacket();
			connection.send();
			System.out.println("[SQL REQUEST] load characters took "+(System.nanoTime()-timer)/1000+" �s to execute.");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
}

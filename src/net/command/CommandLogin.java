package net.command;
import java.io.IOException;
import java.sql.SQLException;

import jdo.JDOStatement;
import net.PacketID;
import net.Server;
import net.connection.ConnectionManager;



public class CommandLogin extends Command {
	
	public CommandLogin(ConnectionManager connectionManager) {
		super(connectionManager);
}

	public final void read() throws SQLException {
		String username = this.connection.readString();
		String password = this.connection.readString();
		JDOStatement statement = Server.getJDO().prepare("SELECT name, password, rank, banned, ban_duration FROM account WHERE name = ?");
		statement.putString(username);
		statement.execute();
		while(statement.fetch()) {
			String goodUsername = statement.getString();
			String goodPassword = statement.getString();
			if(goodPassword.equals(password) && goodUsername.equals(username)) {
				int id = statement.getInt();
				int ban = statement.getInt();
				int banDuration = statement.getInt();
				if(ban == 1 && banDuration > System.currentTimeMillis()) {
					this.connection.writeByte(PacketID.LOGIN);
					this.connection.writeByte(PacketID.ACCOUNT_BANNED);
					this.connection.send();
					return;
				}
				if((ban == 0 && banDuration > 0) || (ban == 1 && banDuration < System.currentTimeMillis())) {
					updateBan(id, ban, banDuration);
				}
				if(Server.getPlayerList().containsKey(id)) {
					this.connection.writeByte(PacketID.LOGIN);
					this.connection.writeByte(PacketID.ALREADY_LOGGED);
					this.connection.send();
					this.player.close();
					Server.removePlayer(this.player);
					return;
				}
				this.connection.writeByte(PacketID.LOGIN);
				this.connection.writeByte(PacketID.LOGIN_ACCEPT);
				this.connection.send();
				this.player.setId(id);
			}
			else {
				this.connection.writeByte(PacketID.LOGIN);
				this.connection.writeByte(PacketID.LOGIN_WRONG);
				this.connection.send();
				this.player.close();
				Server.removePlayer(this.player);
				return;
			}
		}
		//System.out.println(this.connection.hasRemaining());
		//this.connection.clearRBuffer();
		//System.out.println(this.connection.hasRemaining());
		statement.close();
	}
	
	private static void updateBan(int id, int ban, int banDuration) throws SQLException {
		JDOStatement statement = Server.getJDO().prepare("UPDATE banned, ban_duration FROM acount WHERE id = ?");
		statement.putInt(id);
		statement.execute();
		if(ban == 0) {
			if(banDuration > System.currentTimeMillis()) {
				statement.putInt(1);
				statement.putInt(banDuration);
			}
			else {
				statement.putInt(0);
				statement.putInt(0);
			}
		}
		else if(ban == 1) {
			if(banDuration > System.currentTimeMillis()) {
				statement.close();
			}
			else {
				statement.putInt(0);
				statement.putInt(0);
			}
		}
	}
}

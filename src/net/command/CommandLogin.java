package net.command;
import java.sql.SQLException;

import jdo.JDOStatement;
import net.Server;
import net.connection.ConnectionManager;
import net.connection.PacketID;

public class CommandLogin extends Command {
	
	private static JDOStatement read_statement;
	private static JDOStatement write_statement;
	
	public CommandLogin(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	@Override
	public void read() {
		try {
			if(read_statement == null) {
				read_statement = Server.getJDO().prepare("SELECT name, password, id, rank, banned, ban_duration FROM account WHERE name = ?");
			}
			String username = this.connection.readString();
			String password = this.connection.readString();
			read_statement.clear();
			read_statement.putString(username);
			read_statement.execute();
			if(read_statement.fetch()) {
				String goodUsername = read_statement.getString();
				String goodPassword = read_statement.getString();
				if(goodPassword.equals(password) && goodUsername.equals(username)) {
					int id = read_statement.getInt();
					int rank = read_statement.getInt();
					int ban = read_statement.getInt();
					int banDuration = read_statement.getInt();
					if(ban == 1) {
						if(banDuration > System.currentTimeMillis()) {
							this.connection.writeByte(PacketID.LOGIN);
							this.connection.writeByte(PacketID.ACCOUNT_BANNED_TEMP);
							this.connection.send();
							this.player.close();
							return;
						}
						if(banDuration == -1) {
							this.connection.writeByte(PacketID.LOGIN);
							this.connection.writeByte(PacketID.ACCOUNT_BANNED_PERM);
							this.connection.send();
							this.player.close();
							return;
						}
					}
					if((ban == 0 && banDuration > 0) || (ban == 1 && banDuration < System.currentTimeMillis())) {
						updateBan(id, ban, banDuration);
					}
					if(Server.getPlayerList().containsKey(id)) {
						this.connection.writeByte(PacketID.LOGIN);
						this.connection.writeByte(PacketID.ALREADY_LOGGED);
						this.connection.send();
						this.player.close();
						return;
					}
					this.connection.writeByte(PacketID.LOGIN);
					this.connection.writeByte(PacketID.LOGIN_ACCEPT);
					this.connection.writeInt(id);
					this.connection.writeInt(rank);
					this.connection.send();
					this.player.setId(id);
					Server.removeNonLoggedPlayer(this.player);
					Server.addLoggedPlayer(this.player);
					return;
				}
				else {
					this.connection.writeByte(PacketID.LOGIN);
					this.connection.writeByte(PacketID.LOGIN_WRONG);
					this.connection.send();
					this.player.close();
					return;
				}
			}
			else {
				this.connection.writeByte(PacketID.LOGIN);
				this.connection.writeByte(PacketID.LOGIN_WRONG);
				this.connection.send();
				this.player.close();
				return;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void updateBan(int id, int ban, int banDuration) throws SQLException {
		if(write_statement == null) {
			write_statement = Server.getJDO().prepare("UPDATE banned, ban_duration FROM acount WHERE id = ?");
		}
		write_statement.clear();
		write_statement.putInt(id);
		write_statement.execute();
		if(ban == 0) {
			if(banDuration > System.currentTimeMillis()) {
				write_statement.putInt(1);
				write_statement.putInt(banDuration);
			}
			else {
				write_statement.putInt(0);
				write_statement.putInt(0);
			}
		}
		else if(ban == 1) {
			if(banDuration > System.currentTimeMillis()) {
				write_statement.close();
			}
			else {
				write_statement.putInt(0);
				write_statement.putInt(0);
			}
		}
	}
}

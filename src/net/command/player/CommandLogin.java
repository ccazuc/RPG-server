package net.command.player;

import net.command.Command;

public class CommandLogin extends Command { //UNUSED

	public CommandLogin(String name, boolean debug)
	{
		super(name, debug);
	}
	
	/*static JDOStatement read_statement;
	private static JDOStatement write_statement;
	private static SQLRequest loginRequest = new SQLRequest("SELECT name, password, id, rank, banned, ban_duration FROM account WHERE name = ?") {
		@Override
		public void gatherData() {
			try {
				this.statement.clear();
				this.statement.putString(this.userName);
				this.statement.execute();
				if(this.statement.fetch()) {
					String goodUsername = this.statement.getString().toLowerCase();
					String goodPassword = this.statement.getString();
					if(goodPassword.equals(this.password) && goodUsername.equals(this.userName.toLowerCase())) {
						int id = this.statement.getInt();
						int rank = this.statement.getInt();
						int ban = this.statement.getInt();
						int banDuration = this.statement.getInt();
						if(ban == 1) {
							if(banDuration > System.currentTimeMillis()) {
								this.player.getConnectionManager().getConnection().writeByte(PacketID.LOGIN);
								this.player.getConnectionManager().getConnection().writeByte(PacketID.ACCOUNT_BANNED_TEMP);
								this.player.getConnectionManager().getConnection().send();
								this.player.close();
								return;
							}
							if(banDuration == -1) {
								this.player.getConnectionManager().getConnection().writeByte(PacketID.LOGIN);
								this.player.getConnectionManager().getConnection().writeByte(PacketID.ACCOUNT_BANNED_PERM);
								this.player.getConnectionManager().getConnection().send();
								this.player.close();
								return;
							}
						}
						if((ban == 0 && banDuration > 0) || (ban == 1 && banDuration < System.currentTimeMillis())) {
							updateBan(id, ban, banDuration);
						}
						if(Server.getLoggedPlayerList().containsKey(id)) {
							this.player.getConnectionManager().getConnection().writeByte(PacketID.LOGIN);
							this.player.getConnectionManager().getConnection().writeByte(PacketID.ALREADY_LOGGED);
							this.player.getConnectionManager().getConnection().send();
							this.player.close();
							return;
						}
						this.player.getConnectionManager().getConnection().writeByte(PacketID.LOGIN);
						this.player.getConnectionManager().getConnection().writeByte(PacketID.LOGIN_ACCEPT);
						this.player.getConnectionManager().getConnection().writeInt(id);
						this.player.getConnectionManager().getConnection().writeInt(rank);
						this.player.getConnectionManager().getConnection().send();
						this.player.setAccountId(id);
						this.player.setAccountRank(rank);
						Server.removeNonLoggedPlayer(this.player);
						Server.addLoggedPlayer(this.player);
						return;
					}
					else {
						this.player.getConnectionManager().getConnection().writeByte(PacketID.LOGIN);
						this.player.getConnectionManager().getConnection().writeByte(PacketID.LOGIN_WRONG);
						this.player.getConnectionManager().getConnection().send();
						this.player.close();
						return;
					}
				}
				else {
					this.player.getConnectionManager().getConnection().writeByte(PacketID.LOGIN);
					this.player.getConnectionManager().getConnection().writeByte(PacketID.LOGIN_WRONG);
					this.player.getConnectionManager().getConnection().send();
					this.player.close();
					return;
				}
			}
			catch(SQLException e) {
			}
		}
	};

	@Override
	public void read(Player player) {
		if(!Server.getInGamePlayerList().containsKey(player.getCharacterId()) && !Server.getLoggedPlayerList().containsKey(player.getCharacterId())) {
			Connection connection = player.getConnection();
			loginRequest.setPlayer(player);
			loginRequest.setUserName(connection.readString().toLowerCase());
			loginRequest.setPassword(connection.readString());
			Server.addNewRequest(loginRequest);
		}
		else {
			//player is using WPE
		}
		
	}
	
	static void updateBan(int id, int ban, int banDuration) throws SQLException {
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
	}*/
}

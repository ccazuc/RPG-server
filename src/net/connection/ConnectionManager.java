package net.connection;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;

import static net.PacketID.*;
import net.Player;
import net.Server;
import net.command.CommandLogin;

public class ConnectionManager {
	
	private Player player;
	private Connection connection;
	private CommandLogin commandLogin;
	
	public ConnectionManager(Player player, SocketChannel socket) {
		this.player = player;
		this.connection = new Connection(socket);
		this.commandLogin = new CommandLogin(this);
	}
	
	public void read() throws SQLException {
		System.out.println("read");
		byte packetId = -1;
		try {
			this.connection.read();
			if(this.connection.hasRemaining()) {
				packetId = this.connection.readByte();
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
			this.connection.close();
			Server.removePlayer(player);
		}
		if(this.connection.hasRemaining()) {
			System.out.println('b');
			if(packetId != -1)
			System.out.println(packetId);
			if(!this.player.isLoggedIn()) {
				if(packetId == LOGIN) {
					this.commandLogin.read();
				}
				else {
					System.out.println("no login");
					//this.connection.clearRBuffer();
					Server.removePlayer(this.player);
					this.player.close();
				}
			}
			else {
				System.out.println("not logged");
				//this.connection.clearRBuffer();
			}
			System.out.println(this.connection.hasRemaining());
		}
	}
	
	public Connection getConnection() {
		return this.connection;
	}
	
	public Player getPlayer() {
		return this.player;
	}
}

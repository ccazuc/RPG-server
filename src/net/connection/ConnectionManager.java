package net.connection;

import static net.connection.PacketID.*;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.HashMap;

import net.Player;
import net.command.Command;
import net.command.CommandCreateCharacter;
import net.command.CommandLogin;
import net.command.CommandLogout;
import net.command.CommandSelectScreenLoadCharacters;

public class ConnectionManager {
	
	private Player player;
	private Connection connection;
	private CommandLogin commandLogin;
	private CommandLogout commandLogout;
	private CommandSelectScreenLoadCharacters commandSelectScreenLoadCharacters;
	private CommandCreateCharacter commandCreateCharacter;
	private HashMap<Integer, Command> commandList = new HashMap<Integer, Command>();
	
	public ConnectionManager(Player player, SocketChannel socket) {
		this.player = player;
		this.connection = new Connection(socket);
		this.commandLogin = new CommandLogin(this);
		this.commandLogout = new CommandLogout(this);
		this.commandSelectScreenLoadCharacters = new CommandSelectScreenLoadCharacters(this);
		this.commandCreateCharacter = new CommandCreateCharacter(this);
		commandList.put((int)LOGIN, this.commandLogin);
		commandList.put((int)LOGOUT, this.commandLogout);
		commandList.put((int)SELECT_SCREEN_LOAD_CHARACTERS, this.commandSelectScreenLoadCharacters);
		commandList.put((int)CREATE_CHARACTER, this.commandCreateCharacter);
	}
	
	public void read() throws SQLException {
		byte packetId = 0;
		try {
			this.connection.read();
			if(this.connection.hasRemaining()) {
				packetId = this.connection.readByte();
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
			this.player.close();
		}
		if(this.connection.hasRemaining()) {
			if(commandList.containsKey((int)packetId)) {
				commandList.get((int)packetId).read();
			}
			else {
				this.player.close();
			}
		}
	}
	
	public Connection getConnection() {
		return this.connection;
	}
	
	public Player getPlayer() {
		return this.player;
	}
}

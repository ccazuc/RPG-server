package net.connection;

import static net.connection.PacketID.*;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.HashMap;

import net.Player;
import net.command.Command;
import net.command.CommandCreateCharacter;
import net.command.CommandDeleteCharacter;
import net.command.CommandLogin;
import net.command.CommandLogout;
import net.command.CommandSelectScreenLoadCharacters;

public class ConnectionManager {
	
	private Player player;
	private Connection connection;
/*	private CommandLogin commandLogin;
	private CommandLogout commandLogout;
	private CommandSelectScreenLoadCharacters commandSelectScreenLoadCharacters;
	private CommandCreateCharacter commandCreateCharacter;*/
	private HashMap<Integer, Command> commandList = new HashMap<Integer, Command>();
	
	public ConnectionManager(Player player, SocketChannel socket) {
		this.player = player;
		this.connection = new Connection(socket);
		/*this.commandLogin = new CommandLogin(this);
		this.commandLogout = new CommandLogout(this);
		this.commandSelectScreenLoadCharacters = new CommandSelectScreenLoadCharacters(this);
		this.commandCreateCharacter = new CommandCreateCharacter(this);
		commandList.put((int)LOGIN, this.commandLogin);
		commandList.put((int)LOGOUT, this.commandLogout);
		commandList.put((int)SELECT_SCREEN_LOAD_CHARACTERS, this.commandSelectScreenLoadCharacters);
		commandList.put((int)CREATE_CHARACTER, this.commandCreateCharacter);*/
		commandList.put((int)LOGIN, new CommandLogin(this));
		commandList.put((int)LOGOUT, new CommandLogout(this));
		commandList.put((int)SELECT_SCREEN_LOAD_CHARACTERS, new CommandSelectScreenLoadCharacters(this));
		commandList.put((int)CREATE_CHARACTER, new CommandCreateCharacter(this));
		commandList.put((int)DELETE_CHARACTER, new CommandDeleteCharacter(this));
	}
	
	public void read() throws SQLException {
		byte packetId = -1;
		int readedByte = 0;
		try {
			if((readedByte = this.connection.read()) == 1) {
				packetId = this.connection.readByte();
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
			this.player.close();
		}
		if(packetId != -1 && commandList.containsKey((int)packetId)) {
			commandList.get((int)packetId).read();
		}
		else if(readedByte > 0 && packetId != -1) {
			System.out.println(readedByte+": "+packetId);
			System.out.println("Disconnected client "+this.player.getId());
			this.player.close();
		}
	}
	
	public Connection getConnection() {
		return this.connection;
	}
	
	public Player getPlayer() {
		return this.player;
	}
}

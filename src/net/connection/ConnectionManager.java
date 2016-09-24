package net.connection;

import static net.connection.PacketID.*;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

import net.command.Command;
import net.command.CommandCreateCharacter;
import net.command.CommandDeleteCharacter;
import net.command.CommandLoadCharacter;
import net.command.CommandLogin;
import net.command.CommandLogout;
import net.command.CommandSelectScreenLoadCharacters;
import net.command.item.CommandGem;
import net.command.item.CommandPotion;
import net.command.item.CommandStuff;
import net.command.item.CommandWeapon;
import net.game.Player;

public class ConnectionManager {
	
	private Player player;
	private Connection connection;
	private HashMap<Integer, Command> commandList = new HashMap<Integer, Command>();
	
	public ConnectionManager(Player player, SocketChannel socket) {
		this.player = player;
		this.connection = new Connection(socket);
		this.commandList.put((int)LOGIN, new CommandLogin(this));
		this.commandList.put((int)LOGOUT, new CommandLogout(this));
		this.commandList.put((int)SELECT_SCREEN_LOAD_CHARACTERS, new CommandSelectScreenLoadCharacters(this));
		this.commandList.put((int)CREATE_CHARACTER, new CommandCreateCharacter(this));
		this.commandList.put((int)DELETE_CHARACTER, new CommandDeleteCharacter(this));
		this.commandList.put((int)LOAD_CHARACTER, new CommandLoadCharacter(this));
		this.commandList.put((int)STUFF, new CommandStuff(this));
		this.commandList.put((int)WEAPON, new CommandWeapon(this));
		this.commandList.put((int)GEM, new CommandGem(this));
		this.commandList.put((int)POTION, new CommandPotion(this));
	}
	
	public void read() {
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
		if(packetId != -1 && this.commandList.containsKey((int)packetId)) {
			this.commandList.get((int)packetId).read();
		}
		else if(readedByte > 0 && packetId != -1) {
			System.out.println("Disconnected client account: "+this.player.getAccountId()+" for unknown packetid: "+packetId+" and "+readedByte+" byte readed");
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

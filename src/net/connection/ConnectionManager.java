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
import net.command.CommandPing;
import net.command.CommandPingConfirmed;
import net.command.CommandSelectScreenLoadCharacters;
import net.command.CommandSendSingleBagItem;
import net.command.chat.CommandListPlayer;
import net.command.item.CommandGem;
import net.command.item.CommandPotion;
import net.command.item.CommandRequestItem;
import net.command.item.CommandStuff;
import net.command.item.CommandWeapon;
import net.game.Player;

public class ConnectionManager {
	
	private Player player;
	private Connection connection;
	private HashMap<Integer, Command> commandList = new HashMap<Integer, Command>();
	private final static int TIMEOUT_TIMER = 10000;
	
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
		this.commandList.put((int)PING, new CommandPing(this));
		this.commandList.put((int)PING_CONFIRMED, new CommandPingConfirmed(this));
		this.commandList.put((int)SEND_SINGLE_BAG_ITEM, new CommandSendSingleBagItem(this));
		this.commandList.put((int)CHAT_LIST_PLAYER, new CommandListPlayer(this));
		this.commandList.put((int)REQUEST_ITEM, new CommandRequestItem(this));
	}
	
	public void read() {
		if(this.player.getPingStatus() && System.currentTimeMillis()-this.player.getPingTimer() > TIMEOUT_TIMER) {
			this.player.close();
		}
		try {
			if(this.connection.read() == 1) {
				readPacket();
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
			this.player.close();
		}
	}
	
	public Connection getConnection() {
		return this.connection;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	private void readPacket() {
		while(this.connection != null && this.connection.hasRemaining()) {
			byte packetId = this.connection.readByte();
			if(this.commandList.containsKey((int)packetId)) {
				this.commandList.get((int)packetId).read();
			}
			else {
				System.out.println("Unknown packet: "+(int)packetId);
			}
		}
	}
}

package net.connection;

import static net.connection.PacketID.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

import net.command.Command;
import net.command.CommandAddItem;
import net.command.CommandCreateCharacter;
import net.command.CommandDeleteCharacter;
import net.command.CommandFriend;
import net.command.CommandGuild;
import net.command.CommandLoadCharacter;
import net.command.CommandLogin;
import net.command.CommandLoginRealm;
import net.command.CommandLogout;
import net.command.CommandLogoutCharacter;
import net.command.CommandParty;
import net.command.CommandPing;
import net.command.CommandPingConfirmed;
import net.command.CommandRegisterToAuthServer;
import net.command.CommandSelectScreenLoadCharacters;
import net.command.CommandSendSingleBagItem;
import net.command.CommandSpellCast;
import net.command.CommandTrade;
import net.command.CommandUpdateStats;
import net.command.chat.CommandGet;
import net.command.chat.CommandListPlayer;
import net.command.chat.CommandPlayerInfo;
import net.command.chat.CommandSendMessage;
import net.command.chat.CommandSet;
import net.command.item.CommandContainer;
import net.command.item.CommandGem;
import net.command.item.CommandPotion;
import net.command.item.CommandRequestItem;
import net.command.item.CommandStuff;
import net.command.item.CommandWeapon;
import net.game.Player;

public class ConnectionManager {
	
	private Player player;
	private Connection connection;
	private static SocketChannel authSocket;
	private static Connection authConnection;
	private HashMap<Integer, Command> commandList = new HashMap<Integer, Command>();
	private static HashMap<Integer, Command> authCommand = new HashMap<Integer, Command>();
	private final static int TIMEOUT_TIMER = 10000;
	private byte lastPacketReaded;
	private final static String AUTH_SERVER_IP = "127.0.0.1";
	private final static int AUTH_SERVER_PORT = 5725;
	private int numberException = 0;
	
	public ConnectionManager(Player player, SocketChannel socket) {
		this.player = player;
		this.connection = new Connection(socket, player);
		this.commandList.put((int)SELECT_SCREEN_LOAD_CHARACTERS, new CommandSelectScreenLoadCharacters(this));
		this.commandList.put((int)SEND_SINGLE_BAG_ITEM, new CommandSendSingleBagItem(this));
		this.commandList.put((int)CREATE_CHARACTER, new CommandCreateCharacter(this));
		this.commandList.put((int)DELETE_CHARACTER, new CommandDeleteCharacter(this));
		this.commandList.put((int)PING_CONFIRMED, new CommandPingConfirmed(this));
		this.commandList.put((int)LOAD_CHARACTER, new CommandLoadCharacter(this));
		this.commandList.put((int)CHAT_LIST_PLAYER, new CommandListPlayer(this));
		this.commandList.put((int)CHAT_PLAYER_INFO, new CommandPlayerInfo(this));
		this.commandList.put((int)REQUEST_ITEM, new CommandRequestItem(this));
		this.commandList.put((int)ADD_ITEM, new CommandAddItem(this));
		this.commandList.put((int)WEAPON, new CommandWeapon(this));
		this.commandList.put((int)POTION, new CommandPotion(this));
		this.commandList.put((int)LOGOUT, new CommandLogout(this));
		this.commandList.put((int)CHAT_SET, new CommandSet(this));
		this.commandList.put((int)CHAT_GET, new CommandGet(this));
		this.commandList.put((int)LOGIN, new CommandLogin(this));
		this.commandList.put((int)STUFF, new CommandStuff(this));
		this.commandList.put((int)PING, new CommandPing(this));
		this.commandList.put((int)GEM, new CommandGem(this));
		this.commandList.put((int)CONTAINER, new CommandContainer(this));
		this.commandList.put((int)SPELL_CAST, new CommandSpellCast(this));
		this.commandList.put((int)UPDATE_STATS, new CommandUpdateStats(this));
		this.commandList.put((int)CHARACTER_LOGOUT, new CommandLogoutCharacter(this));
		this.commandList.put((int)TRADE, new CommandTrade(this));
		this.commandList.put((int)FRIEND, new CommandFriend(this));
		this.commandList.put((int)LOGIN_REALM, new CommandLoginRealm(this));
		this.commandList.put((int)SEND_MESSAGE, new CommandSendMessage(this));
		this.commandList.put((int)PARTY, new CommandParty(this));
		this.commandList.put((int)GUILD, new CommandGuild(this));
	}
	
	public static void initAuthCommand() {
		authCommand.put((int)LOGIN_REALM, new CommandLoginRealm(authConnection));
	}
	public static final boolean connectAuthServer() {
		try {
			authSocket = SocketChannel.open();
			authSocket.socket().connect(new InetSocketAddress(AUTH_SERVER_IP, AUTH_SERVER_PORT), 5000);
			if(authSocket.isConnected()) {
				authSocket.socket().setTcpNoDelay(true);
				authSocket.configureBlocking(false);
				if(authConnection == null) {
					authConnection = new Connection(authSocket);
				}
				else {
					authConnection.setSocket(authSocket);
				}
				return true;
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return false;
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
			//e.printStackTrace();
			System.out.println("IOException on read.");
			this.player.close();
		}
	}
	
	public static void readAuthServer() {
		if(authConnection != null) {
			try {
				if(authConnection.read() == 1) {
					readAuthPacket();
				}
			} 
			catch (IOException e) {
				e.printStackTrace();
				authConnection = null;
			}
		}
	}
	
	public static void registerToAuthServer() {
		if(authConnection != null) {
			CommandRegisterToAuthServer.write(authConnection);
		}
	}
	
	public String getIpAdress() {
		return this.connection.getIpAdress();
	}
	
	public Connection getConnection() {
		return this.connection;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public HashMap<Integer, Command> getCommandList() {
		return this.commandList;
	}
	
	private void readPacket() {
		while(this.connection != null && this.connection.hasRemaining()) {
			byte packetId = this.connection.readByte();
			if(this.commandList.containsKey((int)packetId)) {
				this.lastPacketReaded = packetId;
				this.commandList.get((int)packetId).read();
			}
			else {
				System.out.println("Unknown packet: "+(int)packetId+", last packet readed: "+this.lastPacketReaded+" for player "+this.player.getAccountId());
			}
		}
	}
	
	private static void readAuthPacket() {
		while(authConnection != null && authConnection.hasRemaining()) {
			byte packetId = authConnection.readByte();
			if(authCommand.containsKey((int)packetId)) {
				authCommand.get((int)packetId).read();
			}
			else {
				System.out.println("Unknown packet: "+(int)packetId+" for authServer");
			}
		}
	}
}

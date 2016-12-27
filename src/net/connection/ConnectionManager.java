package net.connection;

import static net.connection.PacketID.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

import net.command.Command;
import net.command.auth.CommandLoginRealmAuth;
import net.command.auth.CommandRegisterToAuthServer;
import net.command.chat.CommandGet;
import net.command.chat.CommandListPlayer;
import net.command.chat.CommandPlayerInfo;
import net.command.chat.CommandSendMessage;
import net.command.chat.CommandSet;
import net.command.item.CommandContainer;
import net.command.item.CommandDeleteItem;
import net.command.item.CommandDragItems;
import net.command.item.CommandGem;
import net.command.item.CommandPotion;
import net.command.item.CommandRequestItem;
import net.command.item.CommandStuff;
import net.command.item.CommandWeapon;
import net.command.player.CommandAddItem;
import net.command.player.CommandCreateCharacter;
import net.command.player.CommandDeleteCharacter;
import net.command.player.CommandFriend;
import net.command.player.CommandGuild;
import net.command.player.CommandIgnore;
import net.command.player.CommandLoadCharacter;
import net.command.player.CommandLogin;
import net.command.player.CommandLoginRealmPlayer;
import net.command.player.CommandLogout;
import net.command.player.CommandLogoutCharacter;
import net.command.player.CommandParty;
import net.command.player.CommandPing;
import net.command.player.CommandPingConfirmed;
import net.command.player.CommandSelectScreenLoadCharacters;
import net.command.player.CommandSendSingleBagItem;
import net.command.player.CommandSpellCast;
import net.command.player.CommandTrade;
import net.command.player.CommandUpdateStats;
import net.command.player.CommandWho;
import net.game.Player;

public class ConnectionManager {
	
	private Player player;
	private Connection connection;
	private static SocketChannel authSocket;
	private static Connection authConnection;
	private static HashMap<Integer, Command> commandList = new HashMap<Integer, Command>();
	private static HashMap<Integer, Command> authCommand = new HashMap<Integer, Command>(); //TODO: make InGameCommand HM and selectedScreenCommand HM
	private final static int TIMEOUT_TIMER = 10000;
	private short lastPacketReaded;
	private final static String AUTH_SERVER_IP = "127.0.0.1";
	private final static int AUTH_SERVER_PORT = 5725;
	
	public ConnectionManager(Player player, SocketChannel socket) {
		this.player = player;
		this.connection = new Connection(socket, player);
	}
	
	public static void initPlayerCommand() {
		commandList.put((int)SELECT_SCREEN_LOAD_CHARACTERS, new CommandSelectScreenLoadCharacters());
		commandList.put((int)SEND_SINGLE_BAG_ITEM, new CommandSendSingleBagItem());
		commandList.put((int)CREATE_CHARACTER, new CommandCreateCharacter());
		commandList.put((int)DELETE_CHARACTER, new CommandDeleteCharacter());
		commandList.put((int)PING_CONFIRMED, new CommandPingConfirmed());
		commandList.put((int)LOAD_CHARACTER, new CommandLoadCharacter());
		commandList.put((int)CHAT_LIST_PLAYER, new CommandListPlayer());
		commandList.put((int)CHAT_PLAYER_INFO, new CommandPlayerInfo());
		commandList.put((int)REQUEST_ITEM, new CommandRequestItem());
		commandList.put((int)ADD_ITEM, new CommandAddItem());
		commandList.put((int)WEAPON, new CommandWeapon());
		commandList.put((int)POTION, new CommandPotion());
		commandList.put((int)LOGOUT, new CommandLogout());
		commandList.put((int)CHAT_SET, new CommandSet());
		commandList.put((int)CHAT_GET, new CommandGet());
		commandList.put((int)LOGIN, new CommandLogin());
		commandList.put((int)STUFF, new CommandStuff());
		commandList.put((int)PING, new CommandPing());
		commandList.put((int)GEM, new CommandGem());
		commandList.put((int)CONTAINER, new CommandContainer());
		commandList.put((int)SPELL_CAST, new CommandSpellCast());
		commandList.put((int)UPDATE_STATS, new CommandUpdateStats());
		commandList.put((int)CHARACTER_LOGOUT, new CommandLogoutCharacter());
		commandList.put((int)TRADE, new CommandTrade());
		commandList.put((int)FRIEND, new CommandFriend());
		commandList.put((int)LOGIN_REALM, new CommandLoginRealmPlayer());
		commandList.put((int)SEND_MESSAGE, new CommandSendMessage());
		commandList.put((int)PARTY, new CommandParty());
		commandList.put((int)GUILD, new CommandGuild());
		commandList.put((int)IGNORE, new CommandIgnore());
		commandList.put((int)WHO, new CommandWho());
		commandList.put((int)DRAG_ITEM, new CommandDragItems());
		commandList.put((int)DELETE_ITEM, new CommandDeleteItem());
	}
	
	public static void initAuthCommand() {
		authCommand.put((int)LOGIN_REALM, new CommandLoginRealmAuth());
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
		//long timer = System.nanoTime();
		if(this.player.getPingStatus() && System.currentTimeMillis()-this.player.getPingTimer() >= TIMEOUT_TIMER) {
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
			//System.out.println("Read took "+(System.nanoTime()-timer)/1000+" µs");
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
	
	public static HashMap<Integer, Command> getCommandList() {
		return commandList;
	}
	
	private void readPacket() {
		if(this.connection == null) {
			return;
		}
		while(this.connection.hasRemaining()) {
			int packetLength = this.connection.readInt();
			if(this.connection.rBufferRemaining()+4 < packetLength) {
				this.connection.rBufferSetPosition(this.connection.rBufferPosition()-4);
				return;
			}
			short packetId = this.connection.readShort();
			if(commandList.containsKey((int)packetId)) {
				this.lastPacketReaded = packetId;
				commandList.get((int)packetId).read(this.player);
			}
			else {
				System.out.println("Unknown packet: "+(int)packetId+", last packet readed: "+this.lastPacketReaded+" for player "+this.player.getAccountId());
				this.player.close();
				break;
			}
		}
	}
	
	private static void readAuthPacket() {
		if(authConnection == null) {
			return;
		}
		while(authConnection.hasRemaining()) {
			int packetLength = authConnection.readInt();
			if(authConnection.rBufferRemaining()+4 < packetLength) {
				authConnection.rBufferSetPosition(authConnection.rBufferPosition()-4);
				return;
			}
			short packetId = authConnection.readShort();
			if(authCommand.containsKey((int)packetId)) {
				authCommand.get((int)packetId).read(authConnection);
			}
			else {
				System.out.println("Unknown packet: "+(int)packetId+" for authServer");
			}
		}
	}
	
	public static Connection getAuthConnection() {
		return authConnection;
	}
}

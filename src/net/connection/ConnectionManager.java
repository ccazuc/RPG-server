package net.connection;

import static net.connection.PacketID.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

import net.Server;
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
import net.command.player.CommandAura;
import net.command.player.CommandCast;
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
import net.command.player.CommandTrade;
import net.command.player.CommandUpdateStats;
import net.command.player.CommandWho;
import net.game.log.Log;
import net.game.unit.Player;

public class ConnectionManager {
	
	private Player player;
	private Connection connection;
	private static SocketChannel authSocket;
	private static Connection authConnection;
	private static HashMap<Integer, Command> loggedCommandList = new HashMap<Integer, Command>();
	private static HashMap<Integer, Command> nonLoggedCommandList = new HashMap<Integer, Command>();
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
		nonLoggedCommandList.put((int)LOGIN_REALM, new CommandLoginRealmPlayer());
		nonLoggedCommandList.put((int)SELECT_SCREEN_LOAD_CHARACTERS, new CommandSelectScreenLoadCharacters());
		nonLoggedCommandList.put((int)CREATE_CHARACTER, new CommandCreateCharacter());
		nonLoggedCommandList.put((int)DELETE_CHARACTER, new CommandDeleteCharacter());
		nonLoggedCommandList.put((int)LOAD_CHARACTER, new CommandLoadCharacter());
		nonLoggedCommandList.put((int)LOGIN, new CommandLogin());
		nonLoggedCommandList.put((int)PING, new CommandPing());
		nonLoggedCommandList.put((int)PING_CONFIRMED, new CommandPingConfirmed());
		loggedCommandList.put((int)SEND_SINGLE_BAG_ITEM, new CommandSendSingleBagItem());
		loggedCommandList.put((int)PING_CONFIRMED, new CommandPingConfirmed());
		loggedCommandList.put((int)CHAT_LIST_PLAYER, new CommandListPlayer());
		loggedCommandList.put((int)CHAT_PLAYER_INFO, new CommandPlayerInfo());
		loggedCommandList.put((int)REQUEST_ITEM, new CommandRequestItem());
		loggedCommandList.put((int)ADD_ITEM, new CommandAddItem());
		loggedCommandList.put((int)WEAPON, new CommandWeapon());
		loggedCommandList.put((int)POTION, new CommandPotion());
		loggedCommandList.put((int)LOGOUT, new CommandLogout());
		loggedCommandList.put((int)CHAT_SET, new CommandSet());
		loggedCommandList.put((int)CHAT_GET, new CommandGet());
		loggedCommandList.put((int)STUFF, new CommandStuff());
		loggedCommandList.put((int)PING, new CommandPing());
		loggedCommandList.put((int)GEM, new CommandGem());
		loggedCommandList.put((int)CONTAINER, new CommandContainer());
		//loggedCommandList.put((int)SPELL_CAST, new CommandSpellCast());
		loggedCommandList.put((int)UPDATE_STATS, new CommandUpdateStats());
		loggedCommandList.put((int)CHARACTER_LOGOUT, new CommandLogoutCharacter());
		loggedCommandList.put((int)TRADE, new CommandTrade());
		loggedCommandList.put((int)FRIEND, new CommandFriend());
		loggedCommandList.put((int)SEND_MESSAGE, new CommandSendMessage());
		loggedCommandList.put((int)PARTY, new CommandParty());
		loggedCommandList.put((int)GUILD, new CommandGuild());
		loggedCommandList.put((int)IGNORE, new CommandIgnore());
		loggedCommandList.put((int)WHO, new CommandWho());
		loggedCommandList.put((int)DRAG_ITEM, new CommandDragItems());
		loggedCommandList.put((int)DELETE_ITEM, new CommandDeleteItem());
		loggedCommandList.put((int)SPELL_CAST, new CommandCast());
		loggedCommandList.put((int)AURA, new CommandAura());
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
		if(this.player.getPingStatus() && Server.getLoopTickTimer()-this.player.getPingTimer() >= TIMEOUT_TIMER) {
			this.player.close();
			return;
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
			if(this.player.isOnline() && loggedCommandList.containsKey((int)packetId)) {
				this.lastPacketReaded = packetId;
				loggedCommandList.get((int)packetId).read(this.player);
			}
			else if(!this.player.isOnline() && nonLoggedCommandList.containsKey((int)packetId)) {
				this.lastPacketReaded = packetId;
				nonLoggedCommandList.get((int)packetId).read(this.player);
			}
			else {
				System.out.println("Unknown packet: "+(int)packetId+", last packet readed: "+this.lastPacketReaded+" for player "+this.player.getAccountId());
				Log.writePlayerLog(this.player, "Unknown packet: "+(int)packetId+", last packet readed: "+this.lastPacketReaded);
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

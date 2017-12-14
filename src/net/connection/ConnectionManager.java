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
import net.command.chat.CommandChannel;
import net.command.chat.CommandGet;
import net.command.chat.CommandListPlayer;
import net.command.chat.CommandPlayerInfo;
import net.command.chat.CommandSendMessage;
import net.command.item.CommandAuction;
import net.command.item.CommandContainer;
import net.command.item.CommandDeleteItem;
import net.command.item.CommandDragItems;
import net.command.item.CommandGem;
import net.command.item.CommandPotion;
import net.command.item.CommandRequestItem;
import net.command.item.CommandStuff;
import net.command.item.CommandWeapon;
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
import net.command.player.CommandTrade;
import net.command.player.CommandUpdateStats;
import net.command.player.CommandWho;
import net.command.player.spell.CommandAura;
import net.command.player.spell.CommandCast;
import net.config.ConfigMgr;
import net.game.log.Log;
import net.game.manager.DebugMgr;
import net.game.unit.Player;

public class ConnectionManager {
	
	private final Player player;
	private final Connection connection;
	private static SocketChannel authSocket;
	private static Connection authConnection;
	private final static HashMap<Short, Command> loggedCommandList = new HashMap<Short, Command>();
	private final static HashMap<Short, Command> nonLoggedCommandList = new HashMap<Short, Command>();
	private final static HashMap<Short, Command> authCommand = new HashMap<Short, Command>();
	private final static int TIMEOUT_TIMER = 10000;
	private short lastPacketReaded;
	private final static String AUTH_SERVER_IP = "127.0.0.1";
	private final static int AUTH_SERVER_PORT = 5725;
	
	public ConnectionManager(Player player, SocketChannel socket) {
		this.player = player;
		this.connection = new Connection(socket, player);
	}
	
	public static void initPlayerCommand() {
		nonLoggedCommandList.put(LOGOUT, new CommandLogout());
		nonLoggedCommandList.put(LOGIN_REALM, new CommandLoginRealmPlayer());
		nonLoggedCommandList.put(SELECT_SCREEN_LOAD_CHARACTERS, new CommandSelectScreenLoadCharacters());
		nonLoggedCommandList.put(CREATE_CHARACTER, new CommandCreateCharacter());
		nonLoggedCommandList.put(DELETE_CHARACTER, new CommandDeleteCharacter());
		nonLoggedCommandList.put(LOAD_CHARACTER, new CommandLoadCharacter());
		nonLoggedCommandList.put(LOGIN, new CommandLogin());
		nonLoggedCommandList.put(PING, new CommandPing());
		nonLoggedCommandList.put(PING_CONFIRMED, new CommandPingConfirmed());
		loggedCommandList.put(PING_CONFIRMED, new CommandPingConfirmed());
		loggedCommandList.put(CHAT_LIST_PLAYER, new CommandListPlayer());
		loggedCommandList.put(CHAT_PLAYER_INFO, new CommandPlayerInfo());
		loggedCommandList.put(REQUEST_ITEM, new CommandRequestItem());
		loggedCommandList.put(WEAPON, new CommandWeapon());
		loggedCommandList.put(POTION, new CommandPotion());
		loggedCommandList.put(LOGOUT, new CommandLogout());
		//loggedCommandList.put(CHAT_SET, new CommandSet());
		loggedCommandList.put(CHAT_GET, new CommandGet());
		loggedCommandList.put(STUFF, new CommandStuff());
		loggedCommandList.put(PING, new CommandPing());
		loggedCommandList.put(GEM, new CommandGem());
		loggedCommandList.put(CONTAINER, new CommandContainer());
		//loggedCommandList.put((int)SPELL_CAST, new CommandSpellCast());
		loggedCommandList.put(UPDATE_STATS, new CommandUpdateStats());
		loggedCommandList.put(CHARACTER_LOGOUT, new CommandLogoutCharacter());
		loggedCommandList.put(TRADE, new CommandTrade());
		loggedCommandList.put(FRIEND, new CommandFriend());
		loggedCommandList.put(SEND_MESSAGE, new CommandSendMessage());
		loggedCommandList.put(PARTY, new CommandParty());
		loggedCommandList.put(GUILD, new CommandGuild());
		loggedCommandList.put(IGNORE, new CommandIgnore());
		loggedCommandList.put(WHO, new CommandWho());
		loggedCommandList.put(DRAG_ITEM, new CommandDragItems());
		loggedCommandList.put(DELETE_ITEM, new CommandDeleteItem());
		loggedCommandList.put(SPELL_CAST, new CommandCast());
		loggedCommandList.put(AURA, new CommandAura());
		loggedCommandList.put(CHANNEL, new CommandChannel());
		loggedCommandList.put(AUCTION, new CommandAuction());
	}
	
	public static void initAuthCommand() {
		authCommand.put(LOGIN_REALM, new CommandLoginRealmAuth());
	}
	public static final boolean connectAuthServer() {
		try {
			authSocket = SocketChannel.open();
			authSocket.socket().connect(new InetSocketAddress(AUTH_SERVER_IP, AUTH_SERVER_PORT), 5000);
			if(authSocket.isConnected()) {
				authSocket.socket().setTcpNoDelay(ConfigMgr.TCP_NO_DELAY_ENABLED);
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
			Server.close();
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
		while(this.connection.hasRemaining() && this.connection.rBufferRemaining() > 4) {
			int packetLength = this.connection.readInt();
			if(this.connection.rBufferRemaining()+4 < packetLength) {
				this.connection.rBufferSetPosition(this.connection.rBufferPosition()-4);
				return;
			}
			short packetId = this.connection.readShort();
			if(DebugMgr.getPacketReceived())
				System.out.println("Received packet, ID: "+packetId+", length: "+packetLength);
			if(this.player.isOnline() && loggedCommandList.containsKey(packetId)) {
				this.lastPacketReaded = packetId;
				loggedCommandList.get(packetId).read(this.player);
			}
			else if(!this.player.isOnline() && nonLoggedCommandList.containsKey(packetId)) {
				this.lastPacketReaded = packetId;
				nonLoggedCommandList.get(packetId).read(this.player);
			}
			else {
				System.out.println("Unknown packet: "+packetId+", last packet readed: "+this.lastPacketReaded+" for player "+this.player.getAccountId());
				Log.writePlayerLog(this.player, "Unknown packet: "+packetId+", last packet readed: "+this.lastPacketReaded);
				this.player.close();
				return;
			}
		}
	}
	
	private static void readAuthPacket() {
		if(authConnection == null) {
			return;
		}
		while(authConnection.hasRemaining() && authConnection.rBufferRemaining() > 4) {
			int packetLength = authConnection.readInt();
			if(authConnection.rBufferRemaining()+4 < packetLength) {
				authConnection.rBufferSetPosition(authConnection.rBufferPosition()-4);
				return;
			}
			short packetId = authConnection.readShort();
			if(authCommand.containsKey(packetId)) {
				authCommand.get(packetId).read(authConnection);
			}
			else {
				System.out.println("Unknown packet: "+packetId+" for authServer");
			}
		}
	}
	
	public static Connection getAuthConnection() {
		return authConnection;
	}
}

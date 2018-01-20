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
import net.command.chat.CommandListPlayer;
import net.command.chat.CommandPlayed;
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
import net.command.player.CommandLoginQueue;
import net.command.player.CommandLoginRealmPlayer;
import net.command.player.CommandLogout;
import net.command.player.CommandLogoutCharacter;
import net.command.player.CommandMail;
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
	private final static HashMap<Short, Command> loginQueueCommandList = new HashMap<Short, Command>();
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
		nonLoggedCommandList.put(LOGOUT, new CommandLogout("LOGOUT", true));
		nonLoggedCommandList.put(LOGIN_REALM, new CommandLoginRealmPlayer("LOGIN_REALM", true));
		nonLoggedCommandList.put(SELECT_SCREEN_LOAD_CHARACTERS, new CommandSelectScreenLoadCharacters("SELECT_SCREEN_LOAD_CHARACTERS", true));
		nonLoggedCommandList.put(CREATE_CHARACTER, new CommandCreateCharacter("CREATE_CHARACTER", true));
		nonLoggedCommandList.put(DELETE_CHARACTER, new CommandDeleteCharacter("DELETE_CHARACTER", true));
		nonLoggedCommandList.put(CHARACTER_LOGIN, new CommandLoadCharacter("CHARACTER_LOGIN", true));
		nonLoggedCommandList.put(LOGIN, new CommandLogin("LOGIN", true));
		nonLoggedCommandList.put(PING, new CommandPing("PING", false));
		nonLoggedCommandList.put(PING_CONFIRMED, new CommandPingConfirmed("PING_CONFIRMED", false));
		nonLoggedCommandList.put(LOGIN_QUEUE, new CommandLoginQueue("LOGIN_QUEUE", true));
		
		loginQueueCommandList.put(LOGIN_QUEUE, new CommandLoginQueue("LOGIN_QUEUE", true));
		loginQueueCommandList.put(PING, new CommandPing("PING", false));
		loginQueueCommandList.put(PING_CONFIRMED, new CommandPingConfirmed("PING_CONFIRMED", false));
		
		loggedCommandList.put(PING_CONFIRMED, new CommandPingConfirmed("PING_CONFIRMED", false));
		loggedCommandList.put(CHAT_LIST_PLAYER, new CommandListPlayer("CHAT_LIST_PLAYER", true));
		loggedCommandList.put(CHAT_PLAYER_INFO, new CommandPlayerInfo("CHAT_PLAYER_INFO", true));
		loggedCommandList.put(REQUEST_ITEM, new CommandRequestItem("REQUEST_ITEM", true));
		loggedCommandList.put(WEAPON, new CommandWeapon("WEAPON", true));
		loggedCommandList.put(POTION, new CommandPotion("POTION", true));
		loggedCommandList.put(LOGOUT, new CommandLogout("LOGOUT", true));
		//loggedCommandList.put(CHAT_SET, new CommandSet());
		//loggedCommandList.put(CHAT_GET, new CommandGet("CHAT_GET", true));
		loggedCommandList.put(STUFF, new CommandStuff("STUFF", true));
		loggedCommandList.put(PING, new CommandPing("PING", false));
		loggedCommandList.put(GEM, new CommandGem("GEM", true));
		loggedCommandList.put(CONTAINER, new CommandContainer("CONTAINER", true));
		//loggedCommandList.put((int)SPELL_CAST, new CommandSpellCast());
		loggedCommandList.put(UPDATE_STATS, new CommandUpdateStats("UPDATE_STATS", true));
		loggedCommandList.put(CHARACTER_LOGOUT, new CommandLogoutCharacter("CHARACTER_LOGOUT", true));
		loggedCommandList.put(TRADE, new CommandTrade("TRADE", true));
		loggedCommandList.put(FRIEND, new CommandFriend("FRIEND", true));
		loggedCommandList.put(SEND_MESSAGE, new CommandSendMessage("SEND_MESSAGE", true));
		loggedCommandList.put(PARTY, new CommandParty("PARTY", true));
		loggedCommandList.put(GUILD, new CommandGuild("GUILD", true));
		loggedCommandList.put(IGNORE, new CommandIgnore("IGNORE", true));
		loggedCommandList.put(WHO, new CommandWho("WHO", true));
		loggedCommandList.put(DRAG_ITEM, new CommandDragItems("DRAG_ITEM", true));
		loggedCommandList.put(DELETE_ITEM, new CommandDeleteItem("DELETE_ITEM", true));
		loggedCommandList.put(SPELL_CAST, new CommandCast("SPELL_CAST", true));
		loggedCommandList.put(AURA, new CommandAura("AURA", true));
		loggedCommandList.put(CHANNEL, new CommandChannel("CHANNEL", true));
		loggedCommandList.put(AUCTION, new CommandAuction("AUCTION", true));
		loggedCommandList.put(MAIL, new CommandMail("MAIL", true));
		loggedCommandList.put(PLAYED, new CommandPlayed("PLAYED", true));
	}
	
	public static void initAuthCommand() {
		authCommand.put(LOGIN_REALM, new CommandLoginRealmAuth("LOGIN_REALM_AUTH", true));
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
			//System.out.println("IOException on read.");
			this.player.close();
			//System.out.println("Read took "+(System.nanoTime()-timer)/1000+" 탎");
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
	
	private void handlePacket(Command command, short packetId, int packetLength)
	{
		long timer = 0;
		this.lastPacketReaded = packetId;
		if(DebugMgr.getPacketReceived() && command.debug())
			System.out.println("Received packet, ID: "+packetId+", name: " + command.getName() + ", length: "+packetLength);
		if (DebugMgr.getPacketExecuteTimer() && command.debug())
			timer = System.nanoTime();
		command.read(this.player);
		if (DebugMgr.getPacketExecuteTimer() && command.debug())
		{
			long result = System.nanoTime();
			System.out.println("Packet: " + packetId + " took: " + (result - timer) + "ns, " + (result - timer) / 1000 + "탎 to execute.");
		}
	}
	
	private void readPacket() {
		if(this.connection == null) {
			return;
		}
		Command command = null;
		while(this.connection.hasRemaining() && this.connection.rBufferRemaining() > 4) {
			int packetLength = this.connection.readInt();
			if(this.connection.rBufferRemaining()+4 < packetLength) {
				this.connection.rBufferSetPosition(this.connection.rBufferPosition()-4);
				return;
			}
			short packetId = this.connection.readShort();
			/*if(this.player.isOnline() && (command = loggedCommandList.get(packetId)) != null) {
				this.lastPacketReaded = packetId;
				if(DebugMgr.getPacketReceived() && command.debug())
					System.out.println("Received packet, ID: "+packetId+", name: " + command.getName() + ", length: "+packetLength);
				if (DebugMgr.getPacketExecuteTimer() && command.debug())
					timer = System.nanoTime();
				command.read(this.player);
				if (DebugMgr.getPacketExecuteTimer() && command.debug())
				{
					long result = System.nanoTime();
					System.out.println("Packet: " + packetId + " took: " + (result - timer) + "ns, " + (result - timer) / 1000 + "탎 to execute.");
				}
			}
			else if((!this.player.isOnline() || this.player.isInLoginQueue()) && (command = nonLoggedCommandList.get(packetId)) != null) {
				this.lastPacketReaded = packetId;
				if(DebugMgr.getPacketReceived() && command.debug())
					System.out.println("Received packet, ID: "+packetId+", name: " + command.getName() + ", length: "+packetLength);
				if (DebugMgr.getPacketExecuteTimer() && command.debug())
					timer = System.nanoTime();
				command.read(this.player);
				if (DebugMgr.getPacketExecuteTimer() && command.debug())
				{
					long result = System.nanoTime();
					System.out.println("Packet: " + packetId + " took: " + (result - timer) + "ns, " + (result - timer) / 1000 + "탎 to execute.");
				}
			}
			else if (this.player.isInLoginQueue() && (command = loginQueueCommandList.get(packetId)) != null)
			{
				this.lastPacketReaded = packetId;
				if(DebugMgr.getPacketReceived() && command.debug())
					System.out.println("Received packet, ID: "+packetId+", name: " + command.getName() + ", length: "+packetLength);
				if (DebugMgr.getPacketExecuteTimer() && command.debug())
					timer = System.nanoTime();
				command.read(this.player);
				if (DebugMgr.getPacketExecuteTimer() && command.debug())
				{
					long result = System.nanoTime();
					System.out.println("Packet: " + packetId + " took: " + (result - timer) + "ns, " + (result - timer) / 1000 + "탎 to execute.");
				}
			}*/
			if(this.player.isOnline() && (command = loggedCommandList.get(packetId)) != null) {
				handlePacket(command, packetId, packetLength);				
			}
			else if((!this.player.isOnline() || this.player.isInLoginQueue()) && (command = nonLoggedCommandList.get(packetId)) != null) {
				handlePacket(command, packetId, packetLength);
			}
			else if (this.player.isInLoginQueue() && (command = loginQueueCommandList.get(packetId)) != null)
			{
				handlePacket(command, packetId, packetLength);
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

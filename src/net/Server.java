package net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import jdo.JDO;
import jdo.wrapper.MariaDB;
import net.connection.ConnectionManager;
import net.connection.Key;
import net.game.Player;
import net.game.chat.StoreChatCommand;
import net.game.guild.GuildMgr;
import net.game.item.bag.ContainerManager;
import net.game.item.gem.GemManager;
import net.game.item.potion.PotionManager;
import net.game.item.stuff.StuffManager;
import net.game.item.weapon.WeaponManager;
import net.game.manager.BanMgr;
import net.game.manager.CharacterMgr;
import net.game.manager.DebugMgr;
import net.game.manager.ItemMgr;
import net.game.spell.SpellMgr;
import net.thread.chatcommand.ChatCommandRequest;
import net.thread.chatcommand.ChatCommandRunnable;
import net.thread.chatcommand.Who;
import net.thread.log.LogRunnable;
import net.thread.socket.SocketRunnable;
import net.thread.sql.SQLRequest;
import net.thread.sql.SQLRequestPriority;
import net.thread.sql.SQLRunnable;
import net.thread.sql.SQLTask;

public class Server {
	
	private static JDO jdo;
	private static JDO asyncLowPriorityJdo;
	private static JDO asyncHighPriorityJdo;
	private static ServerSocketChannel serverSocketChannel;
	//private static SocketChannel clientSocket;
	private static Map<Integer, Player> loggedPlayerList = Collections.synchronizedMap(new HashMap<Integer, Player>());
	private static ArrayList<Integer> loggedPlayerKickList = new ArrayList<Integer>();
	private static List<Player> nonLoggedPlayerList = Collections.synchronizedList(new ArrayList<Player>());
	private static ArrayList<Player> nonLoggedPlayerKickList = new ArrayList<Player>();
	private static HashMap<Integer, Player> inGamePlayerList = new HashMap<Integer, Player>();
	private static ArrayList<Integer> inGamePlayerKickList = new ArrayList<Integer>();
	private static Thread highPrioritySQLRequestThread;
	private static Thread lowPrioritySQLRequestThread;
	private static SQLRunnable highPrioritySQLRunnable;
	private static SQLRunnable lowPrioritySQLRunnable;
	private static SocketRunnable socketRunnable;
	private static Thread socketThread;
	private static ChatCommandRunnable chatCommandRunnable;
	private static Thread chatCommandThread;
	private static Thread logThread;
	private static LogRunnable logRunnable;
	private static HashMap<Double, Key> keyMap = new HashMap<Double, Key>();
	private static ArrayList<Double> removeKeyList = new ArrayList<Double>();
	private final static int KEY_TIMEOUT_TIMER = 15000;
	private static long SERVER_START_TIMER;
	private static long LOOP_TICK_TIMER;
	
	private final static String REALM_NAME = "World Server";
	private final static int REALM_ID = 15;
	private final static int PORT = 5721;
	private final static int LOOP_TIMER = 15;
	private static String SERVER_MESSAGE_OF_THE_DAY = "Welcome on blabla";
	private static boolean serverRunning = true;
	private static boolean isAcceptingConnection = true;

	private final static Pattern isInteger = Pattern.compile("-?[0-9]+");
	
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, InterruptedException {
		SERVER_START_TIMER = System.currentTimeMillis();
		System.out.println(REALM_NAME);
		long time = System.currentTimeMillis();
		float delta;
		jdo = new MariaDB("127.0.0.1", 3306, "rpg", "root", "mideas");
		asyncLowPriorityJdo = new MariaDB("127.0.0.1", 3306, "rpg", "root", "mideas");
		asyncHighPriorityJdo = new MariaDB("127.0.0.1", 3306, "rpg", "root", "mideas");
		BanMgr.removeExpiredBanAccount();
		BanMgr.removeExpiredBanCharacter();
		CharacterMgr.checkOnlinePlayers();
		GuildMgr.removeOrphanedGuildRank();
		GuildMgr.removeOrphanedMember();
		StoreChatCommand.initChatCommandMap();
		nonLoggedPlayerList = Collections.synchronizedList(nonLoggedPlayerList);
		final InetSocketAddress iNetSocketAdress = new InetSocketAddress(PORT);
		serverSocketChannel = ServerSocketChannel.open();
		//serverSocketChannel.configureBlocking(false);
		serverSocketChannel.bind(iNetSocketAdress);
		ItemMgr.initSQLRequest();
		StuffManager.loadStuffs();
		PotionManager.loadPotions();
		WeaponManager.loadWeapons();
		GemManager.loadGems();
		ContainerManager.loadContainer();
		SpellMgr.loadSpells();
		highPrioritySQLRunnable = new SQLRunnable(3);
		highPrioritySQLRequestThread = new Thread(highPrioritySQLRunnable);
		highPrioritySQLRequestThread.start();
		lowPrioritySQLRunnable = new SQLRunnable(15);
		lowPrioritySQLRequestThread = new Thread(lowPrioritySQLRunnable);
		lowPrioritySQLRequestThread.start();
		socketRunnable = new SocketRunnable(serverSocketChannel);
		socketThread = new Thread(socketRunnable);
		socketThread.start();
		chatCommandRunnable = new ChatCommandRunnable();
		chatCommandThread = new Thread(chatCommandRunnable);
		chatCommandThread.start();
		logRunnable = new LogRunnable();
		logThread = new Thread(logRunnable);
		logThread.start();
		System.out.println("Init took "+(System.currentTimeMillis()-time)+" ms.");
		ConnectionManager.connectAuthServer();
		ConnectionManager.registerToAuthServer();
		ConnectionManager.initAuthCommand();
		ConnectionManager.initPlayerCommand();
		System.gc();
		try {
			while(serverRunning) {
				LOOP_TICK_TIMER = System.currentTimeMillis();
				kickPlayers();
				removeKey();
				readAuthServer();
				readOnlinePlayers();
				read();
				checkKeyTimer();
				delta = System.currentTimeMillis()-LOOP_TICK_TIMER;
				if(delta < LOOP_TIMER) {
					Thread.sleep((LOOP_TIMER-(long)delta));
				}
				if(delta >= DebugMgr.getLoopTooLongValue()) {
					System.out.print("Loop too long: ");
					System.out.print(delta);
					System.out.println("ms.");
				}
			}
		}
		catch(RuntimeException e) {
			LogRunnable.writeServerLog(e);
			System.out.println("[RUNTIME EXCEPTION OCCURED]");
		}
		//Save eveything of every player to the DB
		lowPrioritySQLRunnable.close();
		highPrioritySQLRunnable.close();
		socketRunnable.close();
		chatCommandRunnable.close();
		logRunnable.close();
		
	}
	
	private static void removeKey() {
		while(removeKeyList.size() > 0) {
			keyMap.remove(removeKeyList.get(0));
			removeKeyList.remove(0);
		}
	}
	
	private static void checkKeyTimer() {
		if(keyMap.size() == 0) {
			return;
		}
		for(Key key : keyMap.values()) {
			if(LOOP_TICK_TIMER-key.getTimer() >= KEY_TIMEOUT_TIMER) {
				removeKeyList.add(key.getValue());
			}
		}
	}
	
	private static void readOnlinePlayers() {
		for(Player player : inGamePlayerList.values()) {
			player.tick();
		}
	}
	
	private static void kickPlayers() {
		while(nonLoggedPlayerKickList.size() > 0) {
			nonLoggedPlayerList.remove(nonLoggedPlayerKickList.get(0)); 
			nonLoggedPlayerKickList.remove(0);
		}
		while(loggedPlayerKickList.size() > 0) {
			loggedPlayerList.remove(loggedPlayerKickList.get(0));
			loggedPlayerKickList.remove(0);
		}
		while(inGamePlayerKickList.size() > 0) {
			inGamePlayerList.remove(inGamePlayerKickList.get(0));
			inGamePlayerKickList.remove(0);
		}
	}
	
	private static void read() {
		int i = 0;
		synchronized(nonLoggedPlayerList) {
			while(i < nonLoggedPlayerList.size()) {
				nonLoggedPlayerList.get(i).getConnectionManager().read();
				i++;
			}
		}
		synchronized(loggedPlayerList) {
			for(Player player : loggedPlayerList.values()) {
				player.getConnectionManager().read();
			}
		}
	}
	
	public static Player getNonLoggedPlayer(int id) {
		synchronized(nonLoggedPlayerList) {
			int i = 0;
			while(i < nonLoggedPlayerList.size()) {
				if(nonLoggedPlayerList.get(i).getAccountId() == id) {
					return nonLoggedPlayerList.get(i);
				}
				i++;
			}
		}
		return null;
	}
	
	public static void addNonLoggedPlayer(Player player) {
		synchronized(nonLoggedPlayerList) {
			nonLoggedPlayerList.add(player);
		}
	}
	
	public static void removeNonLoggedPlayer(Player player) {
		if(player != null) {
			nonLoggedPlayerKickList.add(player);
		}
	}
	
	public static Map<Integer, Player> getLoggedPlayerList() {
		synchronized(loggedPlayerList) {
			return loggedPlayerList;
		}
	}
	
	public static void addLoggedPlayer(Player player) {
		if(player != null) {
			synchronized(loggedPlayerList) {
				loggedPlayerList.put(player.getAccountId(), player);
			}
		}
	}
	
	public static void addInGamePlayer(Player player) {
		synchronized(inGamePlayerList) {
			inGamePlayerList.put(player.getCharacterId(), player);
		}
	}
	
	public static void removeInGamePlayer(Player player) {
		inGamePlayerKickList.add(player.getCharacterId());
	}
	
	public static void removeLoggedPlayer(Player player) {
		if(player != null) {
			synchronized(loggedPlayerKickList) {
				loggedPlayerKickList.add(player.getAccountId());
			}
		}
	}
	
	public static Player getInGameCharacter(int id) {
		return inGamePlayerList.get(id);
	}
	
	public static Player getInGameCharacterByName(String name) {
		for(Player player : inGamePlayerList.values()) {
			if(player.getName().equals(name)) {
				return player;
			}
		}
		return null;
	}
	
	public static ArrayList<Player> getAllInGameCharacterByIP(String ip) {
		boolean init = false;
		ArrayList<Player> list = null;
		for(Player player : inGamePlayerList.values()) {
			if(player.getIpAdress().equals(ip)) {
				if(!init) {
					list = new ArrayList<Player>();
					init = true;
				}
				list.add(player);
			}
		}
		return list;
	}
	
	public static Player getInGameCharacterByAccount(int accountId) {
		for(Player player : inGamePlayerList.values()) {
			if(player.getAccountId() == accountId) {
				return player;
			}
		}
		return null;
	}
	
	public static Player getCharacter(String name) {
		synchronized(loggedPlayerList) {
			for(Player player : loggedPlayerList.values()) {
				if(player.getName().equals(name)) {
					return player;
				}
			}
		}
		return null;
	}
	
	public static boolean hasKey(double key, int account_id) {
		if(!keyMap.containsKey(key)) {
			return false;
		}
		if(keyMap.get(key).getAccountId() == account_id) {
			return true;
		}
		return false;
	}
	
	public static boolean isAcceptingConnection() {
		return isAcceptingConnection;
	}
	
	public static void setIsAcceptingConnection(boolean we) {
		isAcceptingConnection = we;
	}
	
	public static void close() {
		serverRunning = false;
	}
	
	public static Key getKey(double key) {
		return keyMap.get(key);
	}
	
	public static void addKey(Key key) {
		keyMap.put(key.getValue(), key);
	}
	
	private static void readAuthServer() {
		ConnectionManager.readAuthServer();
	}
	
	/*public static void executeHighPrioritySQL(SQLRequest request) {
		highPrioritySQLRunnable.addRequest(request);
	}*/
	
	public static void executeSQLRequest(SQLRequest request) {
		if(request.getPriority() == SQLRequestPriority.HIGH) {
			highPrioritySQLRunnable.addRequest(request);
		}
		else {
			lowPrioritySQLRunnable.addRequest(request);
		}
	}
	
	public static void executeHighPrioritySQLTask(SQLTask task) {
		highPrioritySQLRunnable.addTask(task);
	}
	
	/*public static void executeLowPrioritySQL(SQLRequest request) {
		lowPrioritySQLRunnable.addRequest(request);
	}*/
	
	public static void addNewWhoRequest(Who who) {
		chatCommandRunnable.addWhoRequest(who);
	}
	
	public static void addNewChatCommandRequest(ChatCommandRequest request) {
		chatCommandRunnable.addChatCommandRequest(request);
	}
	
	public static HashMap<Integer, Player> getInGamePlayerList() {
		return inGamePlayerList;
	}
	
	public static long getLoopTickTimer() {
		return LOOP_TICK_TIMER;
	}
	
	public static void removeKey(double key) {
		keyMap.remove(key);
	}
	
	public static String getRealmName() {
		return REALM_NAME;
	}
	
	public static JDO getJDO() {
		return jdo;
	}
	
	public static JDO getAsyncLowPriorityJDO() {
		return asyncLowPriorityJdo;
	}
	
	public static JDO getAsyncHighPriorityJDO() {
		return asyncHighPriorityJdo;
	}
	
	public static int getRealmId() {
		return REALM_ID;
	}
	
	public static int getPort() {
		return PORT;
	}
	
	public static String getServerMessageOfTheDay() {
		return SERVER_MESSAGE_OF_THE_DAY;
	}
	
	public static void setServerMessageOfTheDay(String message) {
		SERVER_MESSAGE_OF_THE_DAY = message;
	}
	
	public static boolean isInteger(String string) {
		return isInteger.matcher(string).matches();
	}
	
	public static boolean isInteger(char c) {
		return c >= '0' && c <= '9';
	}
	
	public static long getServerStartTimer() {
		return SERVER_START_TIMER;
	}
	
	public static void mTime(long time, String text) {
		System.out.println(System.currentTimeMillis()-time+"ms "+text);
	}
	
	public static void nTime(long time, String text) {
		long timer = System.nanoTime();
		System.out.println((timer-time)+"ns "+((timer-time)/1000)+"µs "+((timer-time)/1000000)+"ms "+text);
	}
}

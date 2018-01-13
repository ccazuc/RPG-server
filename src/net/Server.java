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
import java.util.concurrent.ConcurrentHashMap;

import jdo.JDO;
import jdo.wrapper.MariaDB;
import net.config.ConfigMgr;
import net.connection.ConnectionManager;
import net.connection.Key;
import net.game.auction.AuctionHouseDBMgr;
import net.game.auction.AuctionHouseMgr;
import net.game.aura.AuraMgr;
import net.game.callback.CallbackMgr;
import net.game.chat.StoreChatCommand;
import net.game.guild.GuildMgr;
import net.game.item.bag.ContainerManager;
import net.game.item.gem.GemManager;
import net.game.item.potion.PotionManager;
import net.game.item.stuff.StuffManager;
import net.game.item.weapon.WeaponManager;
import net.game.mail.MailMgr;
import net.game.manager.BanMgr;
import net.game.manager.ChannelMgr;
import net.game.manager.CharacterMgr;
import net.game.manager.DatabaseMgr;
import net.game.manager.DebugMgr;
import net.game.manager.LoginQueueMgr;
import net.game.quest.QuestMgr;
import net.game.spell.SpellMgr;
import net.game.unit.Player;
import net.thread.ThreadMgr;
import net.thread.chatcommand.ChatCommandRequest;
import net.thread.chatcommand.Who;
import net.thread.log.LogRunnable;
import net.thread.sql.SQLRequest;
import net.thread.sql.SQLTask;

public class Server {
	
	private static JDO jdo;
	private static JDO asyncLowPriorityJdo;
	private static JDO asyncHighPriorityJdo;
	private static ServerSocketChannel serverSocketChannel;
	//private static SocketChannel clientSocket;
	private static Map<Integer, Player> loggedPlayerList = new ConcurrentHashMap<Integer, Player>();
	private static ArrayList<Integer> loggedPlayerKickList = new ArrayList<Integer>();
	private static List<Player> nonLoggedPlayerList = Collections.synchronizedList(new ArrayList<Player>());
	private static ArrayList<Player> nonLoggedPlayerKickList = new ArrayList<Player>();
	private static Map<Integer, Player> inGamePlayerList = new ConcurrentHashMap<Integer, Player>();
	private static ArrayList<Integer> inGamePlayerKickList = new ArrayList<Integer>();
	private static HashMap<Double, Key> keyMap = new HashMap<Double, Key>();
	private static ArrayList<Double> removeKeyList = new ArrayList<Double>();
	private static long SERVER_START_TIMER;
	private static long LOOP_TICK_TIMER;
	
	private final static int LOOP_TIMER = 15;
	private static boolean serverRunning = true;
	private static boolean isAcceptingConnection = true;
	
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, InterruptedException {
		SERVER_START_TIMER = System.currentTimeMillis();
		System.out.println(ConfigMgr.REALM_NAME);
		long time = System.currentTimeMillis();
		float delta;
		jdo = new MariaDB("127.0.0.1", DatabaseMgr.PORT, DatabaseMgr.TABLE_NAME, DatabaseMgr.USER_NAME, DatabaseMgr.PASSWORD);
		asyncLowPriorityJdo = new MariaDB("127.0.0.1", DatabaseMgr.PORT, DatabaseMgr.TABLE_NAME, DatabaseMgr.USER_NAME, DatabaseMgr.PASSWORD);
		asyncHighPriorityJdo = new MariaDB("127.0.0.1", DatabaseMgr.PORT, DatabaseMgr.TABLE_NAME, DatabaseMgr.USER_NAME, DatabaseMgr.PASSWORD);
		BanMgr.removeExpiredBanAccount();
		BanMgr.removeExpiredBanCharacter();
		BanMgr.removeExpiredBanIP();
		CharacterMgr.checkOnlinePlayers();
		GuildMgr.removeOrphanedGuildRank();
		GuildMgr.removeOrphanedMember();
		StoreChatCommand.initChatCommandMap();
		MailMgr.loadAllMail();
		CallbackMgr.initCallbackList();
		CallbackMgr.registerAllCallback();
		nonLoggedPlayerList = Collections.synchronizedList(nonLoggedPlayerList);
		final InetSocketAddress iNetSocketAdress = new InetSocketAddress(ConfigMgr.PORT);
		serverSocketChannel = ServerSocketChannel.open();
		//serverSocketChannel.configureBlocking(false);
		serverSocketChannel.bind(iNetSocketAdress);
		//ItemMgr.initSQLRequest();
		StuffManager.loadStuffs();
		PotionManager.loadPotions();
		WeaponManager.loadWeapons();
		GemManager.loadGems();
		ContainerManager.loadContainer();
		AuctionHouseMgr.initAuctionHouseMgr();
		AuctionHouseDBMgr.loadAllAuction();
		AuraMgr.loadAuras();
		SpellMgr.loadSpells();
		ChannelMgr.initChannelMgr();
		QuestMgr.loadQuest();
		ThreadMgr.initThread();
		System.out.println("Init took "+(System.currentTimeMillis()-time)+" ms.");
		ConnectionManager.connectAuthServer();
		ConnectionManager.registerToAuthServer();
		ConnectionManager.initAuthCommand();
		ConnectionManager.initPlayerCommand();
		System.gc();
		while(serverRunning) {
			try {
				LOOP_TICK_TIMER = System.currentTimeMillis();
				kickPlayers();
				removeKey();
				readAuthServer();
				readOnlinePlayers();
				read();
				checkKeyTimer();
				LoginQueueMgr.tick();
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
			catch(RuntimeException e) {
				LogRunnable.writeServerLog(e);
				System.out.println("[RUNTIME EXCEPTION OCCURED] ("+e.getClass()+')');
			}
		}
		CharacterMgr.saveEveryPlayer();
		ThreadMgr.closeThreads();
	}
	
	private static void removeKey() {
		while(removeKeyList.size() > 0) {
			keyMap.remove(removeKeyList.get(0));
			removeKeyList.remove(0);
		}
	}
	
	private static void checkKeyTimer() {
		for(Key key : keyMap.values())
			if(LOOP_TICK_TIMER-key.getTimer() >= ConfigMgr.KEY_TIMEOUT_TIMER)
				removeKeyList.add(key.getValue());
	}
	
	private static void readOnlinePlayers() {
		synchronized (inGamePlayerList)
		{
			for(Player player : inGamePlayerList.values())
				player.tick();
		}
	}
	
	private static void kickPlayers() {
		while(nonLoggedPlayerKickList.size() > 0) {
			nonLoggedPlayerList.remove(nonLoggedPlayerKickList.get(0)); 
			nonLoggedPlayerKickList.remove(0);
		}
		synchronized (loggedPlayerList)
		{
			while(loggedPlayerKickList.size() > 0) {
				loggedPlayerList.remove(loggedPlayerKickList.get(0));
				loggedPlayerKickList.remove(0);
			}
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
			for(Player player : loggedPlayerList.values())
				player.getConnectionManager().read();
		}
	}
	
	public static Player getNonLoggedPlayer(int id) {
		synchronized(nonLoggedPlayerList) {
			int i = -1;
			while(++i < nonLoggedPlayerList.size())
				if(nonLoggedPlayerList.get(i).getAccountId() == id)
					return nonLoggedPlayerList.get(i);
		}
		return null;
	}
	
	public static void addNonLoggedPlayer(Player player) {
		synchronized(nonLoggedPlayerList) {
			nonLoggedPlayerList.add(player);
		}
	}
	
	public static void removeNonLoggedPlayer(Player player) {
		if(player != null)
			nonLoggedPlayerKickList.add(player);
	}
	
	public static Map<Integer, Player> getLoggedPlayerList() {
		synchronized(loggedPlayerList) {
			return loggedPlayerList;
		}
	}
	
	public static ArrayList<Integer> getLoggedPlayerKickList() {
		return (loggedPlayerKickList);
	}
	
	public static void addLoggedPlayer(Player player) {
		synchronized(loggedPlayerList) {
			loggedPlayerList.put(player.getAccountId(), player);
		}
	}
	
	public static void addInGamePlayer(Player player) {
		synchronized(inGamePlayerList) {
			inGamePlayerList.put(player.getUnitID(), player);
		}
	}
	
	public static void removeInGamePlayer(Player player) {
		inGamePlayerKickList.add(player.getUnitID());
	}
	
	public static int getNumberInGameCharacter()
	{
		return (inGamePlayerList.size() - inGamePlayerKickList.size());
	}
	
	public static int getNumberLoggedAccount()
	{
		return (loggedPlayerList.size() - loggedPlayerKickList.size());
	}
	
	public static void removeLoggedPlayer(Player player) {
		if(player != null)
			synchronized(loggedPlayerKickList) {
				loggedPlayerKickList.add(player.getAccountId());
			}
	}
	
	public static Player getInGameCharacter(int id) {
		return inGamePlayerList.get(id);
	}
	
	public static Player getInGameCharacterByName(String name) {
		synchronized(inGamePlayerList) {
			for(Player player : inGamePlayerList.values())
				if(player.getName().equals(name))
					return player;
		}
		return null;
	}
	
	public static ArrayList<Player> getAllInGameCharacterByIP(String ip) {
		boolean init = false;
		ArrayList<Player> list = null;
		synchronized(inGamePlayerList) {
			for(Player player : inGamePlayerList.values()) {
				if(player.getIpAdress().equals(ip)) {
					if(!init) {
						list = new ArrayList<Player>();
						init = true;
					}
					list.add(player);
				}
			}
		}
		return list;
	}
	
	public static Player getInGameCharacterByAccount(int accountId) {
		synchronized(inGamePlayerList) {
			for(Player player : inGamePlayerList.values())
				if(player.getAccountId() == accountId)
					return player;
		}
		return null;
	}
	
	public static Player getCharacter(String name) {
		synchronized(loggedPlayerList) {
			for(Player player : loggedPlayerList.values())
				if(player.getName().equals(name))
					return player;
		}
		return null;
	}
	
	public static boolean hasKey(double key, int account_id) {
		if(!keyMap.containsKey(key))
			return false;
		return (keyMap.get(key).getAccountId() == account_id);
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
	
	public static void executeSQLRequest(SQLRequest request) {
		ThreadMgr.executeSQLRequest(request);
	}
	
	public static void executeHighPrioritySQLTask(SQLTask task) {
		ThreadMgr.executeHighPrioritySQLTask(task);
	}
	
	public static void addNewWhoRequest(Who who) {
		ThreadMgr.executeWhoRequest(who);
	}
	
	public static void addNewChatCommandRequest(ChatCommandRequest request) {
		ThreadMgr.executeChatCommandRequest(request);
	}
	
	public static Map<Integer, Player> getInGamePlayerList() {
		return inGamePlayerList;
	}
	
	public static ArrayList<Integer> getInGamePlayerKickList() {
		return (inGamePlayerKickList);
	}
	
	public static long getLoopTickTimer() {
		return LOOP_TICK_TIMER;
	}
	
	public static void removeKey(double key) {
		keyMap.remove(key);
	}
	
	public static String getRealmName() {
		return ConfigMgr.REALM_NAME;
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
	
	public static long getServerStartTimer() {
		return SERVER_START_TIMER;
	}
	
	public static ServerSocketChannel getServerSocketChannel() {
		return serverSocketChannel;
	}
	
	public static List<Player> getNonLoggedPlayerList()
	{
		return (nonLoggedPlayerList);
	}
	
	public static void mTime(long time, String text) {
		System.out.println(System.currentTimeMillis()-time+"ms "+text);
	}
	
	public static void nTime(long time, String text) {
		long timer = System.nanoTime();
		System.out.println((timer-time)+"ns "+((timer-time)/1000)+"µs "+((timer-time)/1000000)+"ms "+text);
	}
}

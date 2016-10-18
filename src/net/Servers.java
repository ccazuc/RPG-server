package net;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.JDO;
import jdo.wrapper.MariaDB;
import net.connection.ConnectionManager;
import net.game.Player;
import net.game.item.ItemManager;
import net.game.item.bag.ContainerManager;
import net.game.item.gem.GemManager;
import net.game.item.potion.PotionManager;
import net.game.item.stuff.StuffManager;
import net.game.item.weapon.WeaponManager;
import net.game.spell.SpellManager;
import net.sql.MyRunnable;
import net.sql.SQLRequest;
import net.utils.Hash;

public class Servers {
	
	private final static int PORT = 5720;
	private static JDO jdo;
	private static ServerSocketChannel serverSocketChannel;
	private static SocketChannel clientSocket;
	private static Map<Integer, Player> playerList = Collections.synchronizedMap(new HashMap<Integer, Player>());
	private static List<Player> nonLoggedPlayer = Collections.synchronizedList(new ArrayList<Player>());
	private static ArrayList<Integer> playerWaitingForKick = new ArrayList<Integer>();
	private static Thread sqlRequest;
	private static MyRunnable runnable;
	private static HashMap<Double, String> authList = new HashMap<Double, String>();
	
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, NoSuchAlgorithmException {
		jdo = new MariaDB("127.0.0.1", 3306, "rpg", "root", "mideas");
		nonLoggedPlayer = Collections.synchronizedList(nonLoggedPlayer);
		final InetSocketAddress iNetSocketAdress = new InetSocketAddress(PORT);
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.bind(iNetSocketAdress);
		String salt = Hash.generateSalt(8);
		System.out.println(salt);
		System.out.println(Hash.hash("f68fb6411788c923abe31600921829c7db971383", salt));
		ItemManager.initSQLRequest();
		StuffManager.loadStuffs();
		PotionManager.loadPotions();
		WeaponManager.loadWeapons();
		GemManager.loadGems();
		ContainerManager.loadContainer();
		SpellManager.loadSpells();
		runnable = new MyRunnable();
		sqlRequest = new Thread(runnable);
		sqlRequest.start();
		long time;
		ConnectionManager.connectAuthServer();
		while(true) {
			if((clientSocket = serverSocketChannel.accept()) != null) {
				clientSocket.configureBlocking(false);
				nonLoggedPlayer.add(new Player(clientSocket));
			}
			time = System.currentTimeMillis();
			read();
			readAuthServer();
			kickPlayers();
			if((System.currentTimeMillis()-time)/1000d >= 0.05) {
				System.out.println("Loop too long: "+(System.currentTimeMillis()-time)/1000d);
			}
		}
	}
	
	public static void addNewRequest(SQLRequest request) {
		runnable.addRequest(request);
	}
	
	private static void readAuthServer() {
		ConnectionManager.readAuthServer();
	}
	
	private static void kickPlayers() {
		int i = 0;
		while(i < playerWaitingForKick.size()) {
			playerList.remove(playerWaitingForKick.get(i));
			i++;
		}
		playerWaitingForKick.clear();
	}
	
	private static void read() {
		int i = 0;
		synchronized(nonLoggedPlayer) {
			while(i < nonLoggedPlayer.size()) {
				nonLoggedPlayer.get(i).getConnectionManager().read();
				i++;
			}
		}
		synchronized(playerList) {
			for(Player player : playerList.values()) {
				player.getConnectionManager().read();
			}
		}
	}
	
	public static boolean keyMatch(double key, String ip) {
		if(authList.containsKey(key)) {
			return authList.get(key).equals(ip);
		}
		return false;
	}
	
	public static void addKey(double key, String ip) {
		authList.put(key, ip);
	}
	
	public static Map<Integer, Player> getPlayerList() {
		synchronized(playerList) {
			return playerList;
		}
	}
	
	public static JDO getJDO() {
		return jdo;
	}
	
	public static Player getNonLoggedPlayer(int id) {
		int i = 0;
		synchronized(nonLoggedPlayer) {
			while(i < nonLoggedPlayer.size()) {
				if(nonLoggedPlayer.get(i).getAccountId() == id) {
					return nonLoggedPlayer.get(i);
				}
				i++;
			}
		}
		return null;
	}
	
	public static void removeNonLoggedPlayer(Player player) {
		if(player != null) {
			synchronized(nonLoggedPlayer) {
				nonLoggedPlayer.remove(player);
			}
		}
	}
	
	public static void addLoggedPlayer(Player player) {
		if(player != null) {
			synchronized(playerList) {
				playerList.put(player.getAccountId(), player);
			}
		}
	}
	
	public static void removeLoggedPlayer(Player player) {
		if(player != null) {
			playerWaitingForKick.add(player.getAccountId());
		}
	}
	
	public static Player getCharacter(int id) {
		synchronized(playerList) {
			for(Player player : playerList.values()) {
				if(player.getCharacterId() == id) {
					return player;
				}
			}
		}
		return null;
	}
	
	public static Player getCharacter(String name) {
		synchronized(playerList) {
			for(Player player : playerList.values()) {
				if(player.getName().equals(name)) {
					return player;
				}
			}
		}
		return null;
	}
}
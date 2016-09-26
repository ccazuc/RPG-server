package net;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.JDO;
import jdo.wrapper.MariaDB;
import net.game.Player;
import net.game.item.ItemManager;
import net.game.item.bag.BagManager;
import net.game.item.gem.GemManager;
import net.game.item.potion.PotionManager;
import net.game.item.stuff.StuffManager;
import net.game.item.weapon.WeaponManager;
import net.sql.MyRunnable;
import net.sql.SQLRequest;

public class Server {
	
	
	private final static int PORT = 5720;
	private static JDO jdo;
	private static ServerSocketChannel serverSocketChannel;
	private static SocketChannel clientSocket;
	private static Map<Integer, Player> playerList = Collections.synchronizedMap(new HashMap<Integer, Player>());
	private static List<Player> nonLoggedPlayer = new ArrayList<Player>();
	private static Thread sqlRequest;
	private static MyRunnable runnable;
	
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		jdo = new MariaDB("127.0.0.1", 3306, "rpg", "root", "mideas");
		nonLoggedPlayer = Collections.synchronizedList(nonLoggedPlayer);
		final InetSocketAddress iNetSocketAdress = new InetSocketAddress(PORT);
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.bind(iNetSocketAdress);
		ItemManager.initSQLRequest();
		StuffManager.loadStuffs();
		PotionManager.loadPotions();
		WeaponManager.loadWeapons();
		GemManager.loadGems();
		BagManager.loadBags();
		runnable = new MyRunnable();
		sqlRequest = new Thread(runnable);
		sqlRequest.start();
		while(true) {
			if((clientSocket = serverSocketChannel.accept()) != null) {
				clientSocket.configureBlocking(false);
				nonLoggedPlayer.add(new Player(clientSocket));
			}
			read();
		}
	}
	
	public static void addNewRequest(SQLRequest request) {
		runnable.addRequest(request);
	}
	
	private static void read() {
		int i = 0;
		while(i < nonLoggedPlayer.size()) {
			nonLoggedPlayer.get(i).getConnectionManager().read();
			i++;
		}
		for(Player player : playerList.values()) {
			player.getConnectionManager().read();
		}
	}
	
	public static Map<Integer, Player> getPlayerList() {
		return playerList;
	}
	
	public static JDO getJDO() {
		return jdo;
	}
	
	public static Player getNonLoggedPlayer(int id) {
		int i = 0;
		while(i < nonLoggedPlayer.size()) {
			if(nonLoggedPlayer.get(i).getAccountId() == id) {
				return nonLoggedPlayer.get(i);
			}
			i++;
		}
		return null;
	}
	
	public static void removeNonLoggedPlayer(Player player) {
		if(player != null) {
			nonLoggedPlayer.remove(player);
		}
	}
	
	public static void addLoggedPlayer(Player player) {
		if(player != null) {
			playerList.put(player.getAccountId(), player);
		}
	}
	
	public static void removeLoggedPlayer(Player player) {
		if(player != null) {
			playerList.remove(player.getAccountId());
		}
	}
}

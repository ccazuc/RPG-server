package net;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import jdo.JDO;
import jdo.wrapper.MariaDB;

public class Server {
	
	
	private final static int PORT = 5720;
	private static JDO jdo;
	private static ServerSocketChannel serverSocketChannel;
	private static SocketChannel clientSocket;
	private static HashMap<Integer, Player> playerList = new HashMap<Integer, Player>();
	private static ArrayList<Player> nonLoggedPlayer = new ArrayList<Player>();
	
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		jdo = new MariaDB("127.0.0.1", 3306, "rpg", "root", "mideas");
		final InetSocketAddress iNetSocketAdress = new InetSocketAddress(PORT);
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.bind(iNetSocketAdress);
		while(true) {
			if((clientSocket = serverSocketChannel.accept()) != null) {
				clientSocket.configureBlocking(false);
				nonLoggedPlayer.add(new Player(clientSocket));
			}
			read();
			if(System.currentTimeMillis()%5000 < 1)
			System.out.println(nonLoggedPlayer.size()+" "+playerList.size());
		}
	}
	
	private static void read() {
		int i = 0;
		while(i < nonLoggedPlayer.size()) {
			try {
				nonLoggedPlayer.get(i).getConnectionManager().read();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
			i++;
		}
		for(Player player : playerList.values()) {
			try {
				player.getConnectionManager().read();
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static HashMap<Integer, Player> getPlayerList() {
		return playerList;
	}
	
	public static JDO getJDO() {
		return jdo;
	}
	
	public static void removeNonLoggedPlayer(Player player) {
		nonLoggedPlayer.remove(player);
	}
	
	public static void addLoggedPlayer(Player player) {
		playerList.put(player.getId(), player);
	}
	
	public static void removeLoggedPlayer(Player player) {
		playerList.remove(player.getId());
	}
	
}

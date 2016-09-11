package net;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
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
	private static ByteBuffer rBuffer = ByteBuffer.allocateDirect(16000);
	private static ByteBuffer wBuffer = ByteBuffer.allocateDirect(16000);
	private static ServerSocketChannel serverSocketChannel;
	private static SocketChannel clientSocket;
	private static int readedByte;
	private static HashMap<Integer, Player> playerList = new HashMap<Integer, Player>();
	private static ArrayList<Player> nonLoggedPlayer = new ArrayList<Player>();
	
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		jdo = new MariaDB("127.0.0.1", 3306, "server_test", "root", "mideas");
		final InetSocketAddress iNetSocketAdress = new InetSocketAddress(PORT);
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.bind(iNetSocketAdress);
		while(true) {
			if((clientSocket = serverSocketChannel.accept()) != null) {
				clientSocket.configureBlocking(false);
				nonLoggedPlayer.add(new Player(clientSocket));
			}
			/*if(clientSocket != null && clientSocket.isConnected()) {
				//serverSocketChannel.accept();
				try {
					readedByte = clientSocket.read(rBuffer);
				}
				catch(IOException  e) {
					e.printStackTrace();
					clientSocket.close();
				}
				rBuffer.flip();
				try {
					BufferManager();
				}
				catch (IOException e) {
					e.printStackTrace();
					clientSocket.close();
				}
				catch(BufferUnderflowException e) {
					e.printStackTrace();
				}
				rBuffer.clear();
			}
			else {
			}*/
			checkLogginPlayer();
		}
	}
	
	private static void checkLogginPlayer() {
		int i = 0;
		while(i < nonLoggedPlayer.size()) {
			//System.out.println(nonLoggedPlayer.get(i).getConnectionManager().getConnection().hasRemaining());
			try {
				nonLoggedPlayer.get(i).getConnectionManager().read();
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
	
	public static void removePlayer(Player player) {
		nonLoggedPlayer.remove(player);
	}
	
	private static void BufferManager() throws IOException {
		while(readedByte > 0) {
			byte id = rBuffer.get();
			System.out.println(id);
			wBuffer.put((byte)id);
			wBuffer.flip();
			try {
				clientSocket.write(wBuffer);
			}
			catch(IOException e) {
				e.printStackTrace();
			}
			wBuffer.clear();
			readedByte--;
		}
	}
}

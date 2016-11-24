package net.thread.socket;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import net.Server;
import net.game.Player;

public class SocketRunnable implements Runnable {

	private ServerSocketChannel serverSocket;
	private SocketChannel clientSocket;
	
	public SocketRunnable(ServerSocketChannel serverSocket) {
		this.serverSocket = serverSocket;
	}
	
	@Override
	public void run() {
		System.out.println("SocketRunnable run");
		while(true) {
			try {
				this.clientSocket = this.serverSocket.accept();
				this.clientSocket.configureBlocking(false);
				Server.addNonLoggedPlayer(new Player(this.clientSocket));
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

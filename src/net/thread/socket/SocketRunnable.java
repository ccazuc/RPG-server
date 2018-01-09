package net.thread.socket;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import net.Server;
import net.config.ConfigMgr;
import net.game.unit.Player;

public class SocketRunnable implements Runnable {

	private ServerSocketChannel serverSocket;
	private SocketChannel clientSocket;
	private boolean isAcceptingConnection = true;
	private final static int LOOP_TIMER = 15;
	private volatile static boolean running = true;
	
	public SocketRunnable(ServerSocketChannel serverSocket) {
		this.serverSocket = serverSocket;
	}
	
	@Override
	public void run() {
		long timer = System.currentTimeMillis();
		long delta;
		System.out.println("SocketRunnable run");
		while(running) {
			try {	
				timer = System.currentTimeMillis();
				this.clientSocket = this.serverSocket.accept();
				if(!this.isAcceptingConnection && !this.clientSocket.getLocalAddress().toString().equals("/127.0.0.1")) {
					continue;
				}
				this.clientSocket.socket().setTcpNoDelay(ConfigMgr.TCP_NO_DELAY_ENABLED);
				this.clientSocket.configureBlocking(false);
				Server.addNonLoggedPlayer(new Player(this.clientSocket));
				System.out.println("Add player in socket thread");
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			delta = System.currentTimeMillis()-timer;
			/*if(delta < LOOP_TIMER) {
				try {
					Thread.sleep((LOOP_TIMER-delta));
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}*/
		}
		System.out.println("SocketRunnable stopped");
	}
	
	public void close() {
		try {
			this.serverSocket.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		running = false;
	}
}

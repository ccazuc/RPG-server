package net.thread;

import net.Server;
import net.thread.auctionhouse.AuctionHouseRunnable;
import net.thread.chatcommand.ChatCommandRequest;
import net.thread.chatcommand.ChatCommandRunnable;
import net.thread.chatcommand.Who;
import net.thread.log.LogRunnable;
import net.thread.socket.SocketRunnable;
import net.thread.sql.SQLRequest;
import net.thread.sql.SQLRequestPriority;
import net.thread.sql.SQLRunnable;
import net.thread.sql.SQLTask;

public class ThreadMgr {

	private static Thread highPrioritySQLRequestThread;
	private static SQLRunnable highPrioritySQLRunnable;
	
	private static Thread lowPrioritySQLRequestThread;
	private static SQLRunnable lowPrioritySQLRunnable;
	
	private static Thread socketThread;
	private static SocketRunnable socketRunnable;
	
	private static Thread chatCommandThread;
	private static ChatCommandRunnable chatCommandRunnable;
	
	private static Thread logThread;
	private static LogRunnable logRunnable;
	
	private static Thread auctionHouseThread;
	private static AuctionHouseRunnable auctionHouseRunnable;
	
	public static void initThread() {
		highPrioritySQLRunnable = new SQLRunnable(3);
		highPrioritySQLRequestThread = new Thread(highPrioritySQLRunnable);
		highPrioritySQLRequestThread.start();
		highPrioritySQLRequestThread.setPriority(Thread.MAX_PRIORITY);
		
		lowPrioritySQLRunnable = new SQLRunnable(15);
		lowPrioritySQLRequestThread = new Thread(lowPrioritySQLRunnable);
		lowPrioritySQLRequestThread.start();
		
		socketRunnable = new SocketRunnable(Server.getServerSocketChannel());
		socketThread = new Thread(socketRunnable);
		socketThread.start();
		socketThread.setPriority(1);
		
		chatCommandRunnable = new ChatCommandRunnable();
		chatCommandThread = new Thread(chatCommandRunnable);
		chatCommandThread.start();
		
		logRunnable = new LogRunnable();
		logThread = new Thread(logRunnable);
		logThread.start();
		
		auctionHouseRunnable = new AuctionHouseRunnable();
		auctionHouseThread = new Thread(auctionHouseRunnable);
		auctionHouseThread.start();
	}
	
	public static void closeThreads() {
		lowPrioritySQLRunnable.close();
		highPrioritySQLRunnable.close();
		socketRunnable.close();
		chatCommandRunnable.close();
		logRunnable.close();
		auctionHouseRunnable.close();
	}
	
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
	
	public static void executeWhoRequest(Who who) {
		chatCommandRunnable.addWhoRequest(who);
	}
	
	public static void executeChatCommandRequest(ChatCommandRequest request) {
		chatCommandRunnable.addChatCommandRequest(request);
	}
}

package net.thread.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import net.game.Player;

public class LogRunnable implements Runnable {

	private static List<String> playerPrintList = new ArrayList<String>();
	private static Calendar calendar = Calendar.getInstance();
	private static FileWriter fileWriterPlayerLog;
	private static BufferedWriter bwPlayerLog;
	private static PrintWriter outPlayerLog;
	private final static String FILE_PATH = "Log";
	private final static String FILE_NAME_PLAYER_LOG = "Log/player_log.txt";
	private static FileWriter fileWriterServerLog;
	private static BufferedWriter bwServerLog;
	private static PrintWriter outServerLog;
	private final static String FILE_NAME_SERVER_LOG = "Log/server_log.txt";
	private final static int LOOP_TIMER = 1000;
	private static boolean running = true;
	private static boolean shouldClose;
	private final static boolean DEBUG = true;
	private static long debugTimer;
	
	public LogRunnable() {
		playerPrintList = Collections.synchronizedList(playerPrintList);
	}
	
	@Override
	public void run() {
		File folder = new File(FILE_PATH);
		if(!folder.exists()) {
			folder.mkdirs();
		}
		folder = new File(FILE_NAME_PLAYER_LOG);
		try {
			if(!folder.exists()) {
				folder.createNewFile();
			}
			fileWriterPlayerLog = new FileWriter(folder.getAbsolutePath(), true);
			folder = new File(FILE_NAME_SERVER_LOG);
			if(!folder.exists()) {
				folder.createNewFile();
			}
			fileWriterServerLog = new FileWriter(folder.getAbsolutePath(), true);
		}
		catch(IOException e) {
			e.printStackTrace();
			return;
		}
		bwPlayerLog = new BufferedWriter(fileWriterPlayerLog);
		outPlayerLog = new PrintWriter(bwPlayerLog);
		bwServerLog = new BufferedWriter(fileWriterServerLog);
		outServerLog = new PrintWriter(bwServerLog);
		long time;
		long delta;
		while(running) {
			time = System.currentTimeMillis();
			while(playerPrintList.size() > 0) {
				if(DEBUG) {
					debugTimer = System.currentTimeMillis();
				}
				outPlayerLog.println(playerPrintList.get(0));
				outPlayerLog.flush();
				if(DEBUG) {
					System.out.println("Write \""+playerPrintList.get(0)+"\" in "+FILE_PATH+" took "+(System.currentTimeMillis()-debugTimer)/1000+" µs");
				}
				playerPrintList.remove(0);
			}
			delta = System.currentTimeMillis()-time;
			if(shouldClose && playerPrintList.size() == 0) {
				outPlayerLog.close();
				outServerLog.close();
				running = false;
				try {
					fileWriterPlayerLog.close();
					bwPlayerLog.close();
					fileWriterServerLog.close();
					bwServerLog.close();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
				return;
			}
			if(delta < LOOP_TIMER) {
				try {
					Thread.sleep((LOOP_TIMER-delta));
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void writePlayerLog(Player player, String text) {
		synchronized(playerPrintList) {
			if(player.isInGame()) {
				playerPrintList.add("[ERROR "+calendar.getTime()+"] PlayerName: "+player.getName()+" PlayerID: "+player.getCharacterId()+" AccountID: "+player.getAccountId()+" "+text);
			}
			else {
				playerPrintList.add("[ERROR "+calendar.getTime()+"] AccountID: "+player.getAccountId()+" "+text);
			}
		}
	}
	
	public void writeServerLog(Exception e) {
		synchronized(outServerLog) {
			outServerLog.println("--------------------------------------------------------------");
			outServerLog.println(calendar.getTime());
			e.printStackTrace(outServerLog);
			outServerLog.println("--------------------------------------------------------------");
			outServerLog.print(System.lineSeparator()+System.lineSeparator());
			outServerLog.flush();
		}
	}
	
	public void close() {
		shouldClose = true;
	}
}

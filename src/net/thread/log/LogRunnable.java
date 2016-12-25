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
import net.game.manager.DebugMgr;

public class LogRunnable implements Runnable {

	private static List<Exception> exceptionList = new ArrayList<Exception>();
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
	private static long debugTimer;
	
	public LogRunnable() {
		playerPrintList = Collections.synchronizedList(playerPrintList);
		exceptionList = Collections.synchronizedList(exceptionList);
	}
	
	@Override
	public void run() {
		File folder = new File(FILE_PATH);
		if(!folder.exists()) {
			folder.mkdirs();
		}
		try {
			folder = new File(FILE_NAME_PLAYER_LOG);
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
			synchronized(playerPrintList) {
				while(playerPrintList.size() > 0) {
					if(DebugMgr.getWriteLogFileTimer()) {
						debugTimer = System.currentTimeMillis();
					}
					outPlayerLog.println(playerPrintList.get(0));
					outPlayerLog.flush();
					if(DebugMgr.getWriteLogFileTimer()) {
						System.out.println("Write \""+playerPrintList.get(0)+"\" in "+FILE_NAME_PLAYER_LOG+" took "+(System.currentTimeMillis()-debugTimer)/1000+" µs");
					}
					playerPrintList.remove(0);
				}
			}
			while(exceptionList.size() > 0) {
				if(DebugMgr.getWriteLogFileTimer()) {
					debugTimer = System.currentTimeMillis();
				}
				outServerLog.println("--------------------------------------------------------------");
				outServerLog.println(calendar.getTime());
				exceptionList.get(0).printStackTrace(outServerLog);
				outServerLog.println("--------------------------------------------------------------");
				outServerLog.print(System.lineSeparator()+System.lineSeparator());
				outServerLog.flush();
				if(DebugMgr.getWriteLogFileTimer()) {
					System.out.println("Write \""+exceptionList.get(0).getClass()+"\" in "+FILE_NAME_SERVER_LOG+" took "+(System.currentTimeMillis()-debugTimer)/1000+" µs");
				}
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
			if(player.isOnline()) {
				playerPrintList.add("[ERROR "+calendar.getTime()+"] PlayerName: "+player.getName()+" PlayerID: "+player.getCharacterId()+" AccountID: "+player.getAccountId()+" "+text);
			}
			else {
				playerPrintList.add("[ERROR "+calendar.getTime()+"] AccountID: "+player.getAccountId()+" "+text);
			}
		}
	}
	
	public void writeServerLog(Exception e) {
		synchronized(exceptionList) {
			exceptionList.add(e);
		}
	}
	
	public void close() {
		shouldClose = true;
	}
}

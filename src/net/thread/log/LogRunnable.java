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

import net.config.ConfigMgr;
import net.game.manager.DebugMgr;
import net.game.unit.Player;

public class LogRunnable implements Runnable {

	private static List<ServerException> exceptionList = new ArrayList<ServerException>();
	private static List<ServerException> exceptionQueue = new ArrayList<ServerException>();
	private static List<ErrorLog> errorList = new ArrayList<ErrorLog>();
	private static List<ErrorLog> errorQueue = new ArrayList<ErrorLog>();
	private static List<String> playerPrintList = new ArrayList<String>();
	private static List<String> playerPrintQueue = new ArrayList<String>();
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
		playerPrintQueue = Collections.synchronizedList(playerPrintQueue);
		exceptionList = Collections.synchronizedList(exceptionList);
		exceptionQueue = Collections.synchronizedList(exceptionQueue);
		errorList = Collections.synchronizedList(errorList);
		errorQueue = Collections.synchronizedList(errorQueue);
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
			synchronized (playerPrintQueue)
			{
				while (playerPrintQueue.size() > 0)
				{
					playerPrintList.add(playerPrintQueue.get(0));
					playerPrintQueue.remove(0);
				}
			}
			synchronized (errorQueue)
			{
				while (errorQueue.size() > 0)
				{
					errorList.add(errorQueue.get(0));
					errorQueue.remove(0);
				}
			}
			synchronized (exceptionQueue)
			{
				while (exceptionQueue.size() > 0)
				{
					exceptionList.add(exceptionQueue.get(0));
					exceptionQueue.remove(0);
				}
			}
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
			while(exceptionList.size() > 0) {
				if(DebugMgr.getWriteLogFileTimer()) {
					debugTimer = System.currentTimeMillis();
				}
				outServerLog.println("--------------------------------------------------------------");
				outServerLog.println(calendar.getTime());
				if(exceptionList.get(0).getPlayer() != null) {
					if(exceptionList.get(0).getPlayer().isOnline()) {
						outServerLog.println("AccountID : "+exceptionList.get(0).getPlayer().getAccountId()+", CharacterID : "+exceptionList.get(0).getPlayer().getUnitID()+", CharacterName : "+exceptionList.get(0).getPlayer().getName());
					}
					else {
						outServerLog.println("AccountID : "+exceptionList.get(0).getPlayer().getAccountId());
					}
				}
				exceptionList.get(0).getException().printStackTrace(outServerLog);
				outServerLog.println("--------------------------------------------------------------");
				outServerLog.println(System.lineSeparator());
				outServerLog.flush();
				if(DebugMgr.getWriteLogFileTimer()) {
					System.out.println("Write \""+exceptionList.get(0).getException().getClass()+"\" in "+FILE_NAME_SERVER_LOG+" took "+(System.currentTimeMillis()-debugTimer)/1000+" µs");
				}
				exceptionList.remove(0);
			}
			while (errorList.size() > 0)
			{
				if(DebugMgr.getWriteLogFileTimer()) {
					debugTimer = System.currentTimeMillis();
				}
				outServerLog.println("--------------------------------------------------------------");
				outServerLog.println(calendar.getTime());
				outServerLog.println(errorList.get(0));
				System.out.println(errorList.get(0));
				outServerLog.println("--------------------------------------------------------------");
				outServerLog.println(System.lineSeparator());
				outServerLog.flush();
				if(DebugMgr.getWriteLogFileTimer()) {
					System.out.println("Write error log in "+FILE_NAME_SERVER_LOG+" took "+(System.currentTimeMillis()-debugTimer)/1000+" µs");
				}
				errorList.remove(0);
			}
			delta = System.currentTimeMillis()-time;
			if(shouldClose && playerPrintList.size() == 0 && exceptionList.size() == 0 && errorList.size() == 0) {
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
				playerPrintList.add("[ERROR "+calendar.getTime()+"] AccountID : "+player.getAccountId()+" PlayerName: "+player.getName()+" PlayerID: "+player.getUnitID()+", "+text);
			}
			else {
				playerPrintList.add("[ERROR "+calendar.getTime()+"] AccountID: "+player.getAccountId()+", "+text);
			}
		}
	}
	
	public static void addErrorLog(String log)
	{
		synchronized(errorQueue)
		{
			System.out.println("Add error");
			if (ConfigMgr.ENABLE_FUNCTION_STACK_TRACE)
				errorQueue.add(new ErrorLog(log, new Throwable().getStackTrace()));
			else
				errorQueue.add(new ErrorLog(log, null));
		}
	}
	
	public static void writeServerLog(Exception e) {
		synchronized(exceptionQueue) {
			exceptionQueue.add(new ServerException(e));
		}
	}
	
	public static void writeServerLog(Exception e, Player player) {
		synchronized(exceptionQueue) {
			exceptionQueue.add(new ServerException(e, player));
		}
	}
	
	public void close() {
		shouldClose = true;
	}
}

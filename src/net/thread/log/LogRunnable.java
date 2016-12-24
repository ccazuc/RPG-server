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

	private static List<String> printList = new ArrayList<String>();
	private static Calendar calendar;
	private static FileWriter fileWritter;
	private static BufferedWriter bw;
	private static PrintWriter out;
	private final static String FILE_PATH = "Log";
	private final static String FILE_NAME = "Log/player_log";
	private final static int LOOP_TIMER = 1000;
	private static boolean running = true;
	private static boolean shouldClose;
	
	public LogRunnable() {
		printList = Collections.synchronizedList(printList);
	}
	
	@Override
	public void run() {
		File folder = new File(FILE_PATH);
		if(!folder.exists()) {
			folder.mkdirs();
		}
		folder = new File(FILE_NAME);
		try {
			if(!folder.exists()) {
				folder.createNewFile();
			}
			fileWritter = new FileWriter(FILE_NAME, true);
		}
		catch(IOException e) {
			e.printStackTrace();
			return;
		}
		bw = new BufferedWriter(fileWritter);
		out = new PrintWriter(bw);
		long time;
		long delta;
		while(running) {
			time = System.currentTimeMillis();
			while(printList.size() > 0) {
				out.println(printList.get(0));
				printList.remove(0);
			}
			delta = System.currentTimeMillis()-time;
			if(delta < LOOP_TIMER) {
				try {
					Thread.sleep((LOOP_TIMER-delta));
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(shouldClose && printList.size() == 0) {
				out.close();
				running = false;
			}
		}
	}
	
	public static void write(Player player, String text) {
		synchronized(printList) {
			if(player.isInGame()) {
				printList.add("[ERROR "+calendar.getTime()+"] PNAME: "+player.getName()+" PID: "+player.getCharacterId()+" AID: "+player.getAccountId()+" "+text);
			}
			else {
				printList.add("[ERROR "+calendar.getTime()+"] AID: "+player.getAccountId()+" "+text);
			}
		}
	}
}

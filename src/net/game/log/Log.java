package net.game.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import net.game.Player;

public class Log {

	private static Calendar calendar;
	private static FileWriter fileWritter;
	private static BufferedWriter bw;
	private static PrintWriter out;
	private final static String FILE_PATH = "Log";
	private final static String FILE_NAME = "Log/player_log";
	
	public static void createLog() {
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
	}
	
	public static void write(Player player, String text) {
		if(player.isInGame()) {
			out.println("[ERROR "+calendar.getTime()+"] "+player.getName()+" "+player.getCharacterId()+" "+text);
		}
	}
}

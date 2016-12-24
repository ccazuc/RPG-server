package net.thread.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.Server;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.ClassType;
import net.game.Player;

public class SQLRunnable implements Runnable {
	
	private List<SQLRequest> SQLList = new ArrayList<SQLRequest>();
	private List<Who> whoList = new ArrayList<Who>();
	private boolean running = true;
	private boolean shouldClose;
	
	private final int LOOP_TIMER;
	
	public SQLRunnable(int loop_timer) {
		this.SQLList = Collections.synchronizedList(this.SQLList);
		this.whoList = Collections.synchronizedList(this.whoList);
		this.LOOP_TIMER = loop_timer;
	}
	
	@Override
	public void run() {
		System.out.println("SQLRunnable run");
		long time;
		long delta;
		while(this.running) {
			time = System.currentTimeMillis();
			while(this.SQLList.size() > 0) {
				if(this.SQLList.get(0).debugActive) {
					long timer = System.nanoTime();
					this.SQLList.get(0).execute();
					System.out.println("[SQL REQUEST] "+this.SQLList.get(0).getName()+" took: "+(System.nanoTime()-timer)/1000+" 탎 to execute.");
				}
				else {
					this.SQLList.get(0).execute();
				}
				this.SQLList.remove(0);
			}
			while(this.whoList.size() > 0) {
				Who who = this.whoList.get(0);
				long timer = System.nanoTime();
				executeWhoRequest(who);
				System.out.println("[WHO] took "+(System.nanoTime()-timer)/1000+" 탎 to execute.");
				this.whoList.remove(0);
			}
			delta = System.currentTimeMillis()-time;
			if(delta < this.LOOP_TIMER) {
				try {
					Thread.sleep((this.LOOP_TIMER-delta));
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(this.shouldClose && this.SQLList.size() == 0) {
				this.running = false;
			}
		}
		System.out.println("SQLRunnable stopped");
	}
	
	private static void endListMethod(Who who) {
		long timer = System.nanoTime();
		String word = parseWho(who.getWord().toLowerCase().trim());
		int wordValue = 0;
		if(word.length() == 1) {
			if(Server.isInteger(word.charAt(0))) {
				wordValue = Integer.parseInt(word);
			}
		}
		else {
			if(Server.isInteger(word)) {
				wordValue = Integer.parseInt(word);
			}
		}
		Connection connection = who.getConnection();
		connection.writeShort(PacketID.WHO);
		for(Player player : Server.getInGamePlayerList().values()) {
			if(word.length() == 0 || player.getLevel() == wordValue && player.getName().contains(word) || (player.getGuild() != null && player.getGuild().getName().contains(word))) {
				connection.writeInt(player.getCharacterId());
				connection.writeString(player.getName());
				if(player.getGuild() == null) {
					connection.writeString("");
				}
				else {
					connection.writeString(player.getGuild().getName());
				}
				connection.writeByte(player.getRace().getValue());
				connection.writeInt(player.getLevel());
				connection.writeByte(ClassType.GUERRIER.getValue());
				
			}
		}
		connection.writeInt(-1);
		System.out.println("[WHO ENDLIST REGEXP] took "+(System.nanoTime()-timer)/1000+" 탎 to execute.");
		connection.send();
	}
	
	/*private static void endListMethoda(Who who) {
		long timer = System.nanoTime();
		String word = parseWho(who.getWord().toLowerCase().trim());
		int wordValue = 0;
		try {
			wordValue = Integer.parseInt(word);
		}
		catch(NumberFormatException e) {
		}
		Connection connection = who.getConnection();
		connection.writeShort(PacketID.WHO);
		for(Player player : Server.getInGamePlayerList().values()) {
			if(word.length() == 0 || player.getLevel() == wordValue && player.getName().contains(word) || (player.getGuild() != null && player.getGuild().getName().contains(word))) {
				connection.writeInt(player.getCharacterId());
				connection.writeString(player.getName());
				if(player.getGuild() == null) {
					connection.writeString("");
				}
				else {
					connection.writeString(player.getGuild().getName());
				}
				connection.writeChar(player.getRace().getValue());
				connection.writeInt(player.getLevel());
				connection.writeChar(ClassType.GUERRIER.getValue());
				
			}
		}
		connection.writeInt(-1);
		System.out.println("[WHO ENDLIST CATCH] took "+(System.nanoTime()-timer)/1000+" 탎 to execute.");
		connection.send();
	}*/
	
	private static void executeWhoRequest(Who who) {
		/*long timer = System.nanoTime();
		bufferMethod(who);
		System.out.println("[WHO BUFFER] took "+(System.nanoTime()-timer)/1000+" 탎 to execute.");
		timer = System.nanoTime();
		listMethod(who);
		System.out.println("[WHO LIST] took "+(System.nanoTime()-timer)/1000+" 탎 to execute.");*/
		//bufferMethod(who);
		endListMethod(who);
		//endListMethoda(who);
		//bufferMethod(who);
	}
	
	private static String parseWho(String text) {
		int i = 0;
		while(i < text.length()) {
			if(text.charAt(i) != ' ') {
				return text;
			}
			i++;
		}
		return "";
	}
	
	public void close() {
		this.shouldClose = true;
	}
	 
	public void addRequest(SQLRequest request) {
		this.SQLList.add(request);
	}
	
	public void addWhoRequest(Who who) {
		this.whoList.add(who);
	}
}

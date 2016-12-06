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
	
	public SQLRunnable() {
		this.SQLList = Collections.synchronizedList(this.SQLList);
		this.whoList = Collections.synchronizedList(this.whoList);
	}
	
	@Override
	public void run() {
		System.out.println("SQLRunnable run");
		while(true) {
			if(this.SQLList.size() > 0) {
				if(this.SQLList.get(0).debugActive) {
					long timer = System.nanoTime();
					this.SQLList.get(0).execute();
					System.out.println("[SQL REQUEST] "+this.SQLList.get(0).getName()+" took: "+(System.nanoTime()-timer)/1000+" µs to execute.");
				}
				else {
					this.SQLList.get(0).execute();
				}
				this.SQLList.remove(0);
			}
			if(this.whoList.size() > 0) {
				long timer = System.nanoTime();
				executeWhoRequest(this.whoList.get(0));
				System.out.println("[WHO] took "+(System.nanoTime()-timer)/1000+" µs to execute.");
				this.whoList.remove(0);
			}
		}
	}
	
	private static void executeWhoRequest(Who who) {
		String word = parseWho(who.getWord().toLowerCase().trim());
		Connection connection = who.getConnection();
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(Player player : Server.getInGamePlayerList().values()) {
			if(word.equals("") || player.getName().contains(word) || (player.getGuild() != null && player.getGuild().getName().contains(word)) || Integer.toString(player.getLevel()).contains(word)) {
				list.add(player.getCharacterId());
			}
		}
		if(list.size() > 0) {
			int i = 0;
			connection.writeShort(PacketID.WHO);
			connection.writeInt(list.size());
			while(i < list.size()) {
				Player player = Server.getInGameCharacter(list.get(i));
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
				i++;
			}
			connection.send();
		}
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
	 
	public void addRequest(SQLRequest request) {
		this.SQLList.add(request);
	}
	
	public void addWhoRequest(Who who) {
		this.whoList.add(who);
	}
}

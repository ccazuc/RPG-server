package net.thread.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.Server;
import net.connection.Buffer;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.ClassType;
import net.game.Player;

public class SQLRunnable implements Runnable {
	
	private List<SQLRequest> SQLList = new ArrayList<SQLRequest>();
	private List<Who> whoList = new ArrayList<Who>();
	private static Buffer buffer = new Buffer();
	
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
					System.out.println("[SQL REQUEST] "+this.SQLList.get(0).getName()+" took: "+(System.nanoTime()-timer)/1000+" 탎 to execute.");
				}
				else {
					this.SQLList.get(0).execute();
				}
				this.SQLList.remove(0);
			}
			if(this.whoList.size() > 0) {
				Who who = this.whoList.get(0);
				long timer = System.nanoTime();
				executeWhoRequest(who);
				System.out.println("[WHO] took "+(System.nanoTime()-timer)/1000+" 탎 to execute.");
				this.whoList.remove(0);
			}
		}
	}
	
	private static void bufferMethod(Who who) {
		String word = parseWho(who.getWord().toLowerCase().trim());
		Connection connection = who.getConnection();
		connection.writeShort(PacketID.WHO);
		int i = 0;
		buffer.clear();
		buffer.clear();
		for(Player player : Server.getInGamePlayerList().values()) {
			if(word.equals("") || player.getName().contains(word) || (player.getGuild() != null && player.getGuild().getName().contains(word)) || player.getLevelString().contains(word)) {
				buffer.writeInt(player.getCharacterId());
				buffer.writeString(player.getName());
				if(player.getGuild() == null) {
					buffer.writeString("");
				}
				else {
					buffer.writeString(player.getGuild().getName());
				}
				buffer.writeChar(player.getRace().getValue());
				buffer.writeInt(player.getLevel());
				buffer.writeChar(ClassType.GUERRIER.getValue());
				i++;
				
			}
		}
		connection.writeInt(i);
		buffer.flip();
		while(buffer.hasRemaining()) {
			connection.writeInt(buffer.readInt());
			connection.writeString(buffer.readString());
			connection.writeString(buffer.readString());
			connection.writeChar(buffer.readChar());
			connection.writeInt(buffer.readInt());
			connection.writeChar(buffer.readChar());
		}
		connection.send();
	}
	
	/*private static void listMethod(Who who) {
		String word = parseWho(who.getWord().toLowerCase().trim());
		Connection connection = who.getConnection();
		ArrayList<Integer> list = new ArrayList<Integer>();
		int i = 0;
		for(Player player : Server.getInGamePlayerList().values()) {
			if(word.equals("") || player.getName().contains(word) || (player.getGuild() != null && player.getGuild().getName().contains(word)) || Integer.toString(player.getLevel()).contains(word)) {
				list.add(player.getCharacterId());
				i++;
				
			}
		}
		if(list.size() > 0) {
			i = 0;
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
	}*/
	
	private static void executeWhoRequest(Who who) {
		/*long timer = System.nanoTime();
		bufferMethod(who);
		System.out.println("[WHO BUFFER] took "+(System.nanoTime()-timer)/1000+" 탎 to execute.");
		timer = System.nanoTime();
		listMethod(who);
		System.out.println("[WHO LIST] took "+(System.nanoTime()-timer)/1000+" 탎 to execute.");*/
		bufferMethod(who);
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

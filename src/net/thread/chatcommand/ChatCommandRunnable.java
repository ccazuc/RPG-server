package net.thread.chatcommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.Server;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.manager.DebugMgr;
import net.game.unit.ClassType;
import net.game.unit.Player;
import net.thread.log.LogRunnable;
import net.utils.StringUtils;

public class ChatCommandRunnable implements Runnable {

	private List<ChatCommandRequest> commandList = new ArrayList<ChatCommandRequest>();
	private List<Who> whoList = new ArrayList<Who>();
	private static boolean running = true;
	
	private final static int LOOP_TIMER = 10;
	
	public ChatCommandRunnable() {
		this.commandList = Collections.synchronizedList(this.commandList);
		this.whoList = Collections.synchronizedList(this.whoList);	
	}
	
	@Override
	public void run() {
		System.out.println("ChatCommand run");
		long timer;
		long time = 0;
		long delta;
		while(running) {
			timer = System.currentTimeMillis();
			synchronized(this.commandList) {
				while(this.commandList.size() > 0) {
					if(DebugMgr.getChatCommandTimer())   {
						time = System.nanoTime();
					}
					try {
						this.commandList.get(0).execute();
					}
					catch(RuntimeException e) {
						LogRunnable.writeServerLog(e, this.commandList.get(0).getPlayer());
					}
					this.commandList.remove(0);
					if(DebugMgr.getChatCommandTimer()) {
						System.out.println("ChatCommand took "+(System.nanoTime()-time)/1000+" µs to execute.");
					}
				}
			}
			synchronized(this.whoList) {
				while(this.whoList.size() > 0) {
					Who who = this.whoList.get(0);
					if(DebugMgr.getExecuteWhoTimer()) {
						time = System.nanoTime();
					}
					try {
						executeWhoRequest(who);
					}
					catch(RuntimeException e) {
						LogRunnable.writeServerLog(e, who.getPlayer());
					}
					if((DebugMgr.getExecuteWhoTimer())) {
						System.out.println("[WHO] took "+(System.nanoTime()-time)/1000+" µs to execute.");
					}
					this.whoList.remove(0);
				}
			}
			delta = System.currentTimeMillis()-timer;
			if(delta < LOOP_TIMER) {
				try {
					Thread.sleep((LOOP_TIMER-delta));
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("ChatCommandRunnable stopped");
	}
	
	private static void executeWhoRequest(Who who) {
		endListMethod(who);
	}
	
	private static void endListMethod(Who who) {
		long timer = 0;
		if(DebugMgr.getExecuteWhoTimer()) {
			timer = System.nanoTime();
		}
		String word = parseWho(who.getWord().toLowerCase().trim());
		int wordValue = 0;
		if(word.length() == 1) {
			if(StringUtils.isInteger(word.charAt(0))) {
				wordValue = Integer.parseInt(word);
			}
		}
		else {
			if(StringUtils.isInteger(word)) {
				wordValue = Integer.parseInt(word);
			}
		}
		Connection connection = who.getPlayer().getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.WHO);
		for(Player player : Server.getInGamePlayerList().values()) {
			if(!player.isGMOn() && word.length() == 0 || player.getLevel() == wordValue && player.getName().contains(word) || (player.getGuild() != null && player.getGuild().getName().contains(word))) {
				connection.writeInt(player.getUnitID());
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
		if(DebugMgr.getExecuteWhoTimer()) {
			System.out.println("[WHO ENDLIST REGEXP] took "+(System.nanoTime()-timer)/1000+" µs to execute.");
		}
		connection.endPacket();
		connection.send();
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
	
	public void addChatCommandRequest(ChatCommandRequest request) {
		synchronized(this.commandList) {
			this.commandList.add(request);
		}
	}
	
	public void addWhoRequest(Who who) {
		synchronized(this.whoList) {
			this.whoList.add(who);
		}
	}
	
	public void close() {
		running = false;
	}
}

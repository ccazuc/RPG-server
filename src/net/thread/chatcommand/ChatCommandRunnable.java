package net.thread.chatcommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.game.manager.DebugMgr;
import net.thread.log.LogRunnable;

public class ChatCommandRunnable implements Runnable {

	private List<ChatCommandRequest> commandList = new ArrayList<ChatCommandRequest>();
	private static boolean running = true;
	
	private final static int LOOP_TIMER = 10;
	
	public ChatCommandRunnable() {
		this.commandList = Collections.synchronizedList(this.commandList);	
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
	
	public void addChatCommandRequest(ChatCommandRequest request) {
		synchronized(this.commandList) {
			this.commandList.add(request);
		}
	}
	
	public void close() {
		running = false;
	}
}

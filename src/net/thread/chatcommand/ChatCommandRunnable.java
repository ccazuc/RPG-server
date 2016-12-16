package net.thread.chatcommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
		long delta;
		while(running) {
			timer = System.currentTimeMillis();
			if(this.commandList.size() > 0) {
				long time = System.nanoTime();
				this.commandList.get(0).execute();
				this.commandList.remove(0);
				System.out.println("ChatCommand took "+(System.nanoTime()-time)/1000+" µs to execute.");
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
		this.commandList.add(request);
	}
	
	public void close() {
		running = false;
	}
}

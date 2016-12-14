package net.thread.chatcommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatCommandRunnable implements Runnable {

	private List<ChatCommandRequest> commandList = new ArrayList<ChatCommandRequest>();
	
	private final static int LOOP_TIMER = 10;
	
	public ChatCommandRunnable() {
		this.commandList = Collections.synchronizedList(this.commandList);	
	}
	
	@Override
	public void run() {
		long timer;
		long delta;
		while(true) {
			timer = System.currentTimeMillis();
			if(this.commandList.size() > 0) {
				this.commandList.get(0).execute();
				this.commandList.remove(0);
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
	}
	
	public void addChatCommandRequest(ChatCommandRequest request) {
		this.commandList.add(request);
	}
}

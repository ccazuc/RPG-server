package net.thread.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.game.manager.DebugMgr;

public class SQLRunnable implements Runnable {
	
	private List<SQLRequest> SQLList = new ArrayList<SQLRequest>();
	private boolean running = true;
	private boolean shouldClose;
	
	private final int LOOP_TIMER;
	
	public SQLRunnable(int loop_timer) {
		this.SQLList = Collections.synchronizedList(this.SQLList);
		this.LOOP_TIMER = loop_timer;
	}
	
	@Override
	public void run() {
		System.out.println("SQLRunnable run");
		long time;
		long delta;
		while(this.running) {
			time = System.currentTimeMillis();
			synchronized(this.SQLList) {
				while(this.SQLList.size() > 0) {
					if(this.SQLList.get(0).debugActive || DebugMgr.getSQLRequestTimer()) {
						long timer = System.nanoTime();
						this.SQLList.get(0).execute();
						System.out.println("[SQL REQUEST] "+this.SQLList.get(0).getName()+" took: "+(System.nanoTime()-timer)/1000+" µs to execute.");
					}
					else {
						this.SQLList.get(0).execute();
					}
					this.SQLList.remove(0);
				}
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
	
	public void close() {
		this.shouldClose = true;
	}
	 
	public void addRequest(SQLRequest request) {
		synchronized(this.SQLList) {
			this.SQLList.add(request);
		}
	}
}

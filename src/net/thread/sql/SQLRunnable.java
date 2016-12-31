package net.thread.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.game.manager.DebugMgr;

public class SQLRunnable implements Runnable {

	private List<SQLTask> SQLTaskQueue = new ArrayList<SQLTask>();
	private List<SQLTask> SQLTaskList = new ArrayList<SQLTask>();
	private List<SQLRequest> SQLRequestList = new ArrayList<SQLRequest>();
	private List<SQLRequest> SQLRequestQueue = new ArrayList<SQLRequest>();
	private boolean running = true;
	private boolean shouldClose;
	
	private final int LOOP_TIMER;
	
	public SQLRunnable(int loop_timer) {
		this.SQLTaskQueue = Collections.synchronizedList(this.SQLTaskQueue);
		this.SQLRequestQueue = Collections.synchronizedList(this.SQLRequestQueue);
		this.SQLRequestList = Collections.synchronizedList(this.SQLRequestList);
		this.SQLTaskList = Collections.synchronizedList(this.SQLTaskList);
		this.LOOP_TIMER = loop_timer;
	}
	
	@Override
	public void run() {
		System.out.println("SQLRunnable run");
		long time;
		long delta;
		while(this.running) {
			time = System.currentTimeMillis();
			synchronized(this.SQLTaskQueue) {
				while(this.SQLTaskQueue.size() > 0) {
					this.SQLTaskList.add(this.SQLTaskQueue.get(0));
					this.SQLTaskQueue.remove(0);
				}
			}
			synchronized(this.SQLRequestQueue) {
				while(this.SQLRequestQueue.size() > 0) {
					this.SQLRequestList.add(this.SQLRequestQueue.get(0));
					this.SQLRequestQueue.remove(0);
				}
			}
			synchronized(this.SQLTaskList) {
				while(this.SQLTaskList.size() > 0) {
					if(DebugMgr.getSQLRequestTimer()) {
						long timer = System.nanoTime();
						this.SQLTaskList.get(0).execute();
						System.out.println("[SQL TASK] "+this.SQLTaskList.get(0).getName()+" took: "+(System.nanoTime()-timer)/1000+" µs to execute.");
					}
					else {
						this.SQLTaskList.get(0).execute();
					}
					this.SQLTaskList.remove(0);
				}
			}
			synchronized(this.SQLRequestList) {
				while(this.SQLRequestList.size() > 0) {
					if(this.SQLRequestList.get(0).debugActive || DebugMgr.getSQLRequestTimer()) {
						long timer = System.nanoTime();
						this.SQLRequestList.get(0).execute();
						System.out.println("[SQL REQUEST] "+this.SQLRequestList.get(0).getName()+" took: "+(System.nanoTime()-timer)/1000+" µs to execute.");
					}
					else {
						this.SQLRequestList.get(0).execute();
					}
					this.SQLRequestList.remove(0);
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
			if(this.shouldClose && this.SQLRequestQueue.size() == 0 && this.SQLTaskQueue.size() == 0) {
				this.running = false;
			}
		}
		System.out.println("SQLRunnable stopped");
	}
	
	public void close() {
		this.shouldClose = true;
	}
	 
	public void addRequest(SQLRequest request) {
		synchronized(this.SQLRequestQueue) {
			this.SQLRequestQueue.add(request);
		}
	}
	
	public void addTask(SQLTask task) {
		synchronized(this.SQLTaskQueue) {
			this.SQLTaskQueue.add(task);
		}
	}
}

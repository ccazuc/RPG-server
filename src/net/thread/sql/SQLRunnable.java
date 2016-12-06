package net.thread.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SQLRunnable implements Runnable {
	
	private List<SQLRequest> list = new ArrayList<SQLRequest>();
	
	public SQLRunnable() {
		this.list = Collections.synchronizedList(this.list);
	}
	
	@Override
	public void run() {
		System.out.println("SQLRunnable run");
		while(true) {
			if(this.list.size() > 0) {
				if(this.list.get(0).debugActive) {
					long timer = System.nanoTime();
					this.list.get(0).execute();
					System.out.println("[SQL REQUEST] "+this.list.get(0).getName()+" took: "+(System.nanoTime()-timer)/1000+" µs to execute.");
				}
				else {
					this.list.get(0).execute();
				}
				this.list.remove(0);
			}
		}
	}
	
	public void addRequest(SQLRequest request) {
		this.list.add(request);
	}
}

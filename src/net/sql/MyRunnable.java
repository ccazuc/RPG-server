package net.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyRunnable implements Runnable {
	
	private List<SQLRequest> list = new ArrayList<SQLRequest>();
	
	public MyRunnable() {
		this.list = Collections.synchronizedList(this.list);
	}
	
	@Override
	public void run() {
		System.out.println("run");
		while(true) {
			if(this.list.size() > 0) {
				this.list.get(0).execute();
				this.list.remove(0);
			}
			if(this.list.size() > 0) {
			}
		}
	}
	
	public void addRequest(SQLRequest request) {
		this.list.add(request);
	}
}

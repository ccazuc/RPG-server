package net.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyRunnable implements Runnable {
	
	private ArrayList<SQLRequest> list = new ArrayList<SQLRequest>();
	
	public MyRunnable() {
	}
	
	@Override
	public void run() {
		System.out.println("run");
		while(true) {
			if(this.list.size() > 0) {
				this.list.get(0).execute();
				this.list.remove(0);
			}
		}
	}
	
	public void addRequest(SQLRequest request) {
		this.list.add(request);
	}
}

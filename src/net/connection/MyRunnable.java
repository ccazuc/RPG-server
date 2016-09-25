package net.connection;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyRunnable implements Runnable {
	
	private List<SQLRequest> list = new ArrayList<SQLRequest>();
	
	public MyRunnable() {
		this.list = Collections.synchronizedList(this.list);
	}
	
	public void run() {
		while(true) {
			if(this.list.size() > 0) {
				try {
					this.list.get(0).execute();
				} 
				catch (SQLTimeoutException e) {
					e.printStackTrace();
				} 
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

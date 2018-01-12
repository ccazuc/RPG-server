package net.utils;

import net.Server;

public class Task {

	private int frequence;
	private long lastTick;
	
	public Task(int frequence) {
		this.frequence = frequence;
	}
	
	public final void event() {
		if(Server.getLoopTickTimer()-this.lastTick >= this.frequence) {
			action();
			this.lastTick = Server.getLoopTickTimer();
		}
	}
	
	public void action() {}
}

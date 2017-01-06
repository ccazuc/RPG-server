package net.game.aura;

import net.Server;

public class AppliedAura {

	private final Aura aura;
	private long endTimer;
	private int numberStack;
	private long lastTick;
	
	public AppliedAura(Aura aura) {
		this.aura = aura;
		this.endTimer = Server.getLoopTickTimer()+aura.getDuration();
		this.numberStack = aura.getDefaultNumberStack();
	}
	
	public Aura getAura() {
		return this.aura;
	}
	
	public long getEndTimer() {
		return this.endTimer;
	}
	
	public int getNumberStack() {
		return this.numberStack;
	}
}

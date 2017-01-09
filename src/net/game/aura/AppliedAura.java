package net.game.aura;

import net.Server;
import net.game.unit.Unit;

public class AppliedAura {

	private final Aura aura;
	private long endTimer;
	private byte numberStack;
	private long lastTick;
	private AuraRemoveList removed;
	
	public AppliedAura(Aura aura) {
		this.aura = aura;
		this.endTimer = Server.getLoopTickTimer()+aura.getDuration();
		this.numberStack = aura.getDefaultNumberStack();
	}
	
	public void tick(Unit unit) {
		if(this.endTimer <= Server.getLoopTickTimer()) {
			unit.removeAura(this);
			this.removed = AuraRemoveList.TIMEOUT;
			return;
		}
		this.aura.onTick(unit, this);
		this.lastTick = Server.getLoopTickTimer();
	}
	
	public void resetState() {
		this.endTimer = Server.getLoopTickTimer()+this.aura.getDuration();
		this.numberStack = this.aura.getDefaultNumberStack();
	}
	
	public void onApply(Unit unit) {
		this.aura.onApply(unit);
	}
	
	public void remove(Unit unit) {
		this.aura.onRemove(unit, this.removed);
	}
	 
	public long getLastTickTimer() {
		return this.lastTick;
	}
	
	public Aura getAura() {
		return this.aura;
	}
	
	public long getEndTimer() {
		return this.endTimer;
	}
	
	public byte getNumberStack() {
		return this.numberStack;
	}
}

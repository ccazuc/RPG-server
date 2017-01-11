package net.game.aura;

import net.Server;
import net.game.unit.Unit;

public class AppliedAura {

	private final Aura aura;
	private long endTimer;
	private long applyTimer;
	private byte numberStack;
	private long lastTick;
	private AuraRemoveList removed;
	
	public AppliedAura(Aura aura) {
		this.aura = aura;
		this.applyTimer = Server.getLoopTickTimer();
		this.endTimer = this.applyTimer+this.aura.getDuration();
		this.numberStack = aura.getDefaultNumberStack();
	}
	
	public AppliedAura(Aura aura, long timeLeft, byte numberStack) {
		this.aura = aura;
		this.endTimer = Server.getLoopTickTimer()+timeLeft;
		this.numberStack = numberStack;
	}
	
	public void tick(Unit unit) {
		if(this.lastTick+this.aura.getTickRate() <= Server.getLoopTickTimer() && this.applyTimer+this.aura.getTickRate() <= Server.getLoopTickTimer()) {
			this.aura.onTick(unit, this);
			this.lastTick = Server.getLoopTickTimer();
		}
		if(this.endTimer <= Server.getLoopTickTimer()) {
			unit.removeAura(this, AuraRemoveList.TIMEOUT);
			return;
		}
	}
	
	public void resetState() {
		this.applyTimer = Server.getLoopTickTimer();
		this.endTimer = this.applyTimer+this.aura.getDuration();
		if(this.numberStack < this.aura.getMaximumStack()) {
			this.numberStack++;
		}
	}
	
	public void onApply(Unit unit) {
		this.aura.onApply(unit);
	}
	
	public void remove(Unit unit, AuraRemoveList removed) {
		this.aura.onRemove(unit, removed);
	}
	
	public void remove(Unit unit) {
		this.aura.onRemove(unit, this.removed);
	}
	
	public void setRemoved(AuraRemoveList removed) {
		this.removed = removed;
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

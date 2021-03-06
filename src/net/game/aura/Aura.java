package net.game.aura;

import net.game.unit.Unit;

public class Aura {

	private final int id;
	private final int duration;
	private final byte defaultNumberStack;
	private final byte maximumStack;
	private final AuraEffect effect1;
	private final int effectValue1;
	private final AuraEffect effect2;
	private final int effectValue2;
	private final AuraEffect effect3;
	private final int effectValue3;
	private final boolean lowDispellable;
	private final boolean highDispellable;
	private final boolean isVisible;
	private final String name;
	protected final int spellTriggeredOnFade;
	private final boolean isBuff;
	private final int tickRate;
	private final boolean isStackable;
	private final boolean isMagical;
	private final boolean dupliFromDifferentSource;
	
	public Aura(int id, String name, String sprite_id, int spellTriggeredOnFade, int duration, boolean isStackable, byte defaultNumberStack, byte maximumStack, int tickRate, boolean lowDispellable, boolean highDispellable, AuraEffect effect1, int effectValue1, AuraEffect effect2, int effectValue2, AuraEffect effect3, int effectValue3, boolean isBuff, boolean isVisible, boolean isMagical, boolean dupliFromDifferentSource) {
		this.id = id;
		this.name = name;
		this.duration = duration;
		this.defaultNumberStack = defaultNumberStack;
		this.maximumStack = maximumStack;
		this.effect1 = effect1;
		this.effectValue1 = effectValue1;
		this.effect2 = effect2;
		this.effectValue2 = effectValue2;
		this.effect3 = effect3;
		this.effectValue3 = effectValue3;
		this.highDispellable = highDispellable;
		this.lowDispellable = lowDispellable;
		this.isVisible = isVisible;
		this.spellTriggeredOnFade = spellTriggeredOnFade;
		this.isBuff = isBuff;
		this.tickRate = tickRate;
		this.isStackable = isStackable;
		this.isMagical = isMagical;
		this.dupliFromDifferentSource = dupliFromDifferentSource;
	}
	
	@SuppressWarnings("unused")
	public void onApply(Unit unit) {}
	
	@SuppressWarnings("unused")
	public void onRemove(Unit unit, AuraRemoveList removed) {}

	@SuppressWarnings("unused")
	public void onTick(Unit unit, AppliedAura appliedAura) {}
	
	public boolean hasEffect(AuraEffect effect) {
		return this.effect1 == effect || this.effect2 == effect || this.effect3 == effect;
	}
	
	public int getEffectValue(AuraEffect effect) {
		if(this.effect1 == effect) {
			return this.effectValue1;
		}
		if(this.effect2 == effect) {
			return this.effectValue2;
		}
		if(this.effect3 == effect) {
			return this.effectValue3;
		}
		return 0;
	}
	public int getId() {
		return this.id;
	}
	
	public boolean isStackable() {
		return this.isStackable;
	}
	
 	public String getName() {
		return this.name;
	}
	
	public boolean isBuff() {
		return this.isBuff;
	}
	
	public int getDuration() {
		return this.duration;
	}
	
	public byte getMaximumStack() {
		return this.maximumStack;
	}
	
	public int getTickRate() {
		return this.tickRate;
	}
	
	public int getEffectValue1() {
		return this.effectValue1;
	}
	
	public boolean dupliFromDifferentSource() {
		return this.dupliFromDifferentSource;
	}
	
	public byte getDefaultNumberStack() {
		return this.defaultNumberStack;
	}
	
	public boolean isHighDispellable() {
		return this.highDispellable;
	}
	
	public boolean isLowDispellable() {
		return this.lowDispellable;
	}
	
	public boolean isVisible() {
		return this.isVisible;
	}
} 

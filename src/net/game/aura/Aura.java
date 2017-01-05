package net.game.aura;

import net.game.Unit;

public class Aura {

	private final int id;
	private final int duration;
	private final int defaultNumberStack;
	private final AuraEffect effect1;
	private final int effectValue1;
	private final boolean lowDispellable;
	private final boolean highDispellable;
	private final boolean isVisible;
	private final String name;
	private final String sprite_id;
	protected final int spellTriggeredOnFade;
	private final boolean isBuff;
	
	public Aura(int id, String name, String sprite_id, boolean isBuff, int spellTriggeredOnFade, int duration, int defaultNumberStack, boolean lowDispellable, boolean highDispellable, boolean isVisible, AuraEffect effect1, int effectValue1) {
		this.id = id;
		this.name = name;
		this.sprite_id = sprite_id;
		this.duration = duration;
		this.defaultNumberStack = defaultNumberStack;
		this.effect1 = effect1;
		this.effectValue1 = effectValue1;
		this.highDispellable = highDispellable;
		this.lowDispellable = lowDispellable;
		this.isVisible = isVisible;
		this.spellTriggeredOnFade = spellTriggeredOnFade;
		this.isBuff = isBuff;
	}
	
	@SuppressWarnings("unused")
	public void onApply(Unit unit) {}
	
	@SuppressWarnings("unused")
	public void onRemove(Unit unit, AuraRemoveList removed) {}
	
	public int getId() {
		return this.id;
	}
	
	public int getDuration() {
		return this.duration;
	}
	
	public int getDefaultNumberStack() {
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

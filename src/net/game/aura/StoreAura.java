package net.game.aura;

import net.game.unit.Unit;

public class StoreAura {

	public static void createAura(int id, String name, String sprite_id, int spellTriggeredOnFade, int duration, boolean isStackable, int defaultNumberStack, int tickRate, boolean lowDispellable, boolean highDispellable, AuraEffect effect1, int effectValue1, AuraEffect effect2, int effectValue2, AuraEffect effect3, int effectValue3, boolean isBuff, boolean isVisible, boolean isMagical) {
		if(id == 1) {
			AuraMgr.storeAura(new Aura(id, name, sprite_id, spellTriggeredOnFade, duration, isStackable, defaultNumberStack, tickRate, lowDispellable, highDispellable, effect1, effectValue1, effect2, effectValue2, effect3, effectValue3, isBuff, isVisible, isMagical) {
				
				@Override
				public void onApply(Unit unit) {
					System.out.println("Aura applied");
				}
				
				@Override
				public void onRemove(Unit unit, AuraRemoveList list) {
					System.out.println("Aura removed : "+list);
				}
				
				@Override
				public void onTick(Unit unit, AppliedAura appliedAura) {
					System.out.println("Aura tick !");
				}
			});
		}
	}
}

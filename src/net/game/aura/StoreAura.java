package net.game.aura;

import net.command.player.spell.CommandAura;
import net.game.unit.Player;
import net.game.unit.Unit;
import net.game.unit.UnitType;

public class StoreAura {

	public static void createAura(int id, String name, String sprite_id, int spellTriggeredOnFade, int duration, boolean isStackable, byte defaultNumberStack, byte maximumStack, int tickRate, boolean lowDispellable, boolean highDispellable, AuraEffect effect1, int effectValue1, AuraEffect effect2, int effectValue2, AuraEffect effect3, int effectValue3, boolean isBuff, boolean isVisible, boolean isMagical, boolean dupliFromDifferentSource) {
		if(id == 1) {
			AuraMgr.storeAura(new Aura(id, name, sprite_id, spellTriggeredOnFade, duration, isStackable, defaultNumberStack, maximumStack, tickRate, lowDispellable, highDispellable, effect1, effectValue1, effect2, effectValue2, effect3, effectValue3, isBuff, isVisible, isMagical, dupliFromDifferentSource) {
				
				@Override
				public void onApply(Unit unit) {
				}
				
				@Override
				public void onRemove(Unit unit, AuraRemoveList list) {
				}
				
				@Override
				public void onTick(Unit unit, AppliedAura appliedAura) {
				}
			});
		}
		else if(id == 2) {
			AuraMgr.storeAura(new Aura(id, name, sprite_id, spellTriggeredOnFade, duration, isStackable, defaultNumberStack, maximumStack, tickRate, lowDispellable, highDispellable, effect1, effectValue1, effect2, effectValue2, effect3, effectValue3, isBuff, isVisible, isMagical, dupliFromDifferentSource) {
				
				@Override
				public void onApply(Unit unit) {
					
				}
				
				@Override
				public void onRemove(Unit unit, AuraRemoveList list) {
					System.out.println("Removed");
					if(unit.getUnitType() == UnitType.PLAYER) {
						CommandAura.removeAura((Player)unit, unit.getUnitID(), this.getId());
					}
				}
				
				@Override
				public void onTick(Unit unit, AppliedAura appliedAura) {
					unit.doHeal(appliedAura.getAura().getEffectValue1());
				}
			});
		}
		else if(id == 3) {
			AuraMgr.storeAura(new Aura(id, name, sprite_id, spellTriggeredOnFade, duration, isStackable, defaultNumberStack, maximumStack, tickRate, lowDispellable, highDispellable, effect1, effectValue1, effect2, effectValue2, effect3, effectValue3, isBuff, isVisible, isMagical, dupliFromDifferentSource) {
				
				@Override
				public void onApply(Unit unit) {
					unit.calcEffectiveMaxStamina();
				}
				
				@Override
				public void onRemove(Unit unit, AuraRemoveList list) {
					if(unit.getUnitType() == UnitType.PLAYER) {
						CommandAura.removeAura((Player)unit, unit.getUnitID(), this.getId());
					}
					unit.calcEffectiveMaxStamina();
				}
				
				@Override
				public void onTick(Unit unit, AppliedAura appliedAura) {
					
				}
			});
		}
		else {
			System.out.println("[LOAD AURA] Aura not found, name : "+name+", id : "+id);
		}
	}
}

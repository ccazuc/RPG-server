package net.game.aura;

import java.sql.SQLException;
import java.util.HashMap;

import jdo.JDOStatement;
import net.Server;

public class AuraMgr {

	private final static HashMap<Integer, Aura> auraMap = new HashMap<Integer, Aura>();
	public final static String LOAD_AURA_REQUEST = "SELECT id, name, sprite_id, spell_triggered_on_fade, duration, is_stackable, default_number_stack, max_stack, tick_rate, low_dispellable, high_dispellable, aura_effect1, aura_effect_value1, aura_effect2, aura_effect_value2, aura_effect3, aura_effect_value3, visible, is_buff, is_magical, dupli_from_diff_source FROM aura";
	private static JDOStatement loadAuras;
	
	public static void loadAuras() {
		try {
			if(loadAuras == null) {
				loadAuras = Server.getJDO().prepare(LOAD_AURA_REQUEST);
			}
			loadAuras.clear();
			loadAuras.execute();
			while(loadAuras.fetch()) {
				int id = loadAuras.getInt();
				String name = loadAuras.getString();
				String sprite_id = loadAuras.getString();
				int spellTriggeredOnFase = loadAuras.getInt();
				int duration = loadAuras.getInt();
				boolean isStackable = loadAuras.getBoolean();
				byte defaultNumberStack = loadAuras.getByte();
				byte maximumStack = loadAuras.getByte();
				int tickRate = loadAuras.getInt();
				boolean lowDispellable = loadAuras.getBoolean();
				boolean highDispellable = loadAuras.getBoolean();
				AuraEffect auraEffect1 = convStringToAuraEffect(loadAuras.getString());
				int auraEffectValue1 = loadAuras.getInt();
				AuraEffect auraEffect2 = convStringToAuraEffect(loadAuras.getString());
				int auraEffectValue2 = loadAuras.getInt();
				AuraEffect auraEffect3 = convStringToAuraEffect(loadAuras.getString());
				int auraEffectValue3 = loadAuras.getInt();
				boolean visible = loadAuras.getBoolean();
				boolean buff = loadAuras.getBoolean();
				boolean magical = loadAuras.getBoolean();
				boolean dupli_from_diff_source = loadAuras.getBoolean();
				StoreAura.createAura(id, name, sprite_id, spellTriggeredOnFase, duration, isStackable, defaultNumberStack, maximumStack, tickRate, lowDispellable, highDispellable, auraEffect1, auraEffectValue1, auraEffect2, auraEffectValue2, auraEffect3, auraEffectValue3, visible, buff, magical, dupli_from_diff_source);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void storeAura(Aura aura) {
		auraMap.put(aura.getId(), aura);
	}
	
	public static Aura getAura(int auraID) {
		return auraMap.get(auraID);
	}
	
	public static AuraEffect convStringToAuraEffect(String aura) {
		if(aura.equals("PERIODIC_DAMAGE")) {
			return AuraEffect.PERIODIC_DAMAGE;
		}
		if(aura.equals("REDUCE_MAX_STAMINA")) {
			return AuraEffect.REDUCE_MAX_STAMINA;
		}
		if(aura.equals("REDUCE_MANA")) {
			return AuraEffect.REDUCE_MANA;
		}
		if(aura.equals("REDUCE_MAX_MANA")) {
			return AuraEffect.REDUCE_MAX_MANA;
		}
		if(aura.equals("REDUCE_ARMOR")) {
			return AuraEffect.REDUCE_ARMOR;
		}
		if(aura.equals("REDUCE_CARAC")) {
			return AuraEffect.REDUCE_CARAC;
		}
		if(aura.equals("REDUCE_STRENGTH")) {
			return AuraEffect.REDUCE_STRENGTH;
		}
		if(aura.equals("REDUCE_AGILITY")) {
			return AuraEffect.REDUCE_AGILITY;
		}
		if(aura.equals("REDUCE_ATTACK_POWER")) {
			return AuraEffect.REDUCE_ATTACK_POWER;
		}
		if(aura.equals("REDUCE_INTELLIGENCE")) {
			return AuraEffect.REDUCE_INTELLIGENCE;
		}
		if(aura.equals("REDUCE_SPELL_POWER")) {
			return AuraEffect.REDUCE_SPELL_POWER;
		}
		if(aura.equals("REDUCE_ATTACK_SPEED")) {
			return AuraEffect.REDUCE_ATTACK_SPEED;
		}
		if(aura.equals("REDUCE_HASTE")) {
			return AuraEffect.REDUCE_HASTE;
		}
		if(aura.equals("REDUCE_SPELL_HASTE")) {
			return AuraEffect.REDUCE_SPELL_HASTE;
		}
		if(aura.equals("REDUCE_CRITICAL")) {
			return AuraEffect.REDUCE_CRITICAL;
		}
		if(aura.equals("REDUCE_SPELL_CRITICAL")) {
			return AuraEffect.REDUCE_SPELL_CRITICAL;
		}
		if(aura.equals("REDUCE_HEALING_POWER")) {
			return AuraEffect.REDUCE_HEALING_POWER;
		}
		if(aura.equals("REDUCE_HEALING_TAKEN")) {
			return AuraEffect.REDUCE_HEALING_TAKEN;
		}
		if(aura.equals("PERIODIC_HEAL")) {
			return AuraEffect.PERIODIC_HEAL;
		}
		if(aura.equals("INCREASE_MAX_STAMINA")) {
			return AuraEffect.INCREASE_MAX_STAMINA;
		}
		if(aura.equals("INCREASE_MANA")) {
			return AuraEffect.INCREASE_MANA;
		}
		if(aura.equals("INCREASE_MAX_MANA")) {
			return AuraEffect.INCREASE_MAX_MANA;
		}
		if(aura.equals("INCREASE_CARAC")) {
			return AuraEffect.INCREASE_CARAC;
		}
		if(aura.equals("INCREASE_STRENGTH")) {
			return AuraEffect.INCREASE_STRENGTH;
		}
		if(aura.equals("INCREASE_AGILITY")) {
			return AuraEffect.INCREASE_AGILITY;
		}
		if(aura.equals("INCREASE_ATTACK_POWER")) {
			return AuraEffect.INCREASE_ATTACK_POWER;
		}
		if(aura.equals("INCREASE_INTELLIGENCE")) {
			return AuraEffect.INCREASE_INTELLIGENCE;
		}
		if(aura.equals("INCREASE_SPELL_POWER")) {
			return AuraEffect.INCREASE_SPELL_POWER;
		}
		if(aura.equals("INCREASE_ATTACK_SPEED")) {
			return AuraEffect.INCREASE_ATTACK_SPEED;
		}
		if(aura.equals("INCREASE_HASTE")) {
			return AuraEffect.INCREASE_HASTE;
		}
		if(aura.equals("INCREASE_SPELL_HASTE")) {
			return AuraEffect.INCREASE_SPELL_HASTE;
		}
		if(aura.equals("INCREASE_CRITICAL")) {
			return AuraEffect.INCREASE_CRITICAL;
		}
		if(aura.equals("INCREASE_SPELL_CRITICAL")) {
			return AuraEffect.INCREASE_SPELL_CRITICAL;
		}
		if(aura.equals("INCREASE_HEALING_POWER")) {
			return AuraEffect.INCREASE_HEALING_POWER;
		}
		if(aura.equals("INCREASE_HEALING_TAKEN")) {
			return AuraEffect.INCREASE_HEALING_TAKEN;
		}
		if(aura.equals("MOUNT")) {
			return AuraEffect.MOUNT;
		}
		if(aura.equals("NONE")) {
			return AuraEffect.NONE;
		}
		if(aura.equals("SPELL_MODIFIER")) {
			return AuraEffect.SPELL_MODIFIER;
		}
		if(aura.equals("STUN")) {
			return AuraEffect.STUN;
		}
		if(aura.equals("FEAR")) {
			return AuraEffect.FEAR;
		}
		if(aura.equals("SILENCE")) {
			return AuraEffect.SILENCE;
		}
		if(aura.equals("IMMUNE_PHYSICAL")) {
			return AuraEffect.IMMUNE_PHYSICAL;
		}
		if(aura.equals("IMMUNE_MAGICAL")) {
			return AuraEffect.IMMUNE_MAGICAL;
		}
		if(aura.equals("IMMUNE_ALL")) {
			return AuraEffect.IMMUNE_ALL;
		}
		System.out.println("Error : Unknown AuraEffect AuraMgr:convStringToAuraEffect : "+aura);
		return null;
	} 
}

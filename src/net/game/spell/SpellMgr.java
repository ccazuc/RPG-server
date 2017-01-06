package net.game.spell;

import java.sql.SQLException;
import java.util.HashMap;

import jdo.JDOStatement;
import net.Server;

public class SpellMgr {

	public final static String LOAD_SPELL_REQUEST = "SELECT id, sprite_id, name, tooltip, rank, effectValue, stun_duration, stun_rate, manaCost, trigger_gcd, cd, cast_time, magical_school, is_magical FROM spell";
	private static int numberSpellLoaded;
	private static JDOStatement loadSpells;
	private final static HashMap<Integer, Spell> spellMap = new HashMap<Integer, Spell>();
	
	/*public static void loadSpells() throws SQLException {
		if(loadSpells == null) {
			loadSpells = Server.getJDO().prepare("SELECT type, id FROM spell");
		}
		loadSpells.clear();
		loadSpells.execute();
		while(loadSpells.fetch()) {
			String type = loadSpells.getString();
			int id = loadSpells.getInt();
			if(type.equals("DAMAGE")) {
				if(loadDamageSpells == null) {
					loadDamageSpells = Server.getJDO().prepare("SELECT sprite_id, name, damage, manaCost, cd, cast_time, stun_duration, stun_rate, trigger_gcd FROM SPELL WHERE id = ?");
				}
				loadDamageSpells.clear();
				loadDamageSpells.putInt(id);
				loadDamageSpells.execute();
				if(loadDamageSpells.fetch()) {
					String sprite_id = loadDamageSpells.getString();
					String name = loadDamageSpells.getString();
					SpellType spellType = getSpellType(type);
					int damage = loadDamageSpells.getInt();
					int manaCost = loadDamageSpells.getInt();
					int cd = loadDamageSpells.getInt();
					int castTime = loadDamageSpells.getInt();
					int stunDuration = loadDamageSpells.getInt();
					float stunRate = loadDamageSpells.getFloat();
					boolean triggerGCD = loadDamageSpells.getBoolean();
					spellMap.put(id, new Spell(id, sprite_id, name, spellType, damage, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD) {
						@Override
						public void action(Unit target, Unit caster) {
							this.doDamage(target, caster);
							if(caster.getUnitType() == UnitType.PLAYER) {
								CommandUpdateStats.updateStamina((Player)caster, target.getid(), target.getStamina());
								//((Player)caster).getConnectionManager().getCommandList().get((int)PacketID.UPDATE_STATS).write(PacketID.UPDATE_STATS_STAMINA, target.getid(), target.getStamina());
							}
						}
					});
				}
			}
			else if(type.equals("HEAL")) {
				if(loadHealSpells == null) {
					loadHealSpells = Server.getJDO().prepare("SELECT sprite_id, name, heal, manaCost, cd, cast_time, trigger_gcd FROM SPELL WHERE id = ?");
				}
				loadHealSpells.clear();
				loadHealSpells.putInt(id);
				loadHealSpells.execute();
				if(loadHealSpells.fetch()) {
					String sprite_id = loadHealSpells.getString();
					String name = loadHealSpells.getString();
					int heal = loadHealSpells.getInt();
					SpellType spellType = getSpellType(type);
					int manaCost = loadHealSpells.getInt();
					int cd = loadHealSpells.getInt();
					int castTime = loadHealSpells.getInt();
					boolean triggerGCD = loadHealSpells.getBoolean();
					spellMap.put(id, new Spell(id, sprite_id, name, spellType, manaCost, heal, cd, castTime, triggerGCD) {
						@Override
						public void action(Unit target, Unit caster) {
							this.doHeal(target, caster);
						}
					});
				}
			}
			else if(type.equals("HEALANDDAMAGE")) {
				
			}
			else if(type.equals("OTHER")) {
				
			}
		}
	}*/
	
	public static void loadSpells() throws SQLException {
		long timer = System.nanoTime();
		int amount = 0;
		if(loadSpells == null) {
			loadSpells = Server.getJDO().prepare(LOAD_SPELL_REQUEST);
		}
		loadSpells.clear();
		loadSpells.execute();
		while(loadSpells.fetch()) {
			int id = loadSpells.getInt();
			String sprite_id = loadSpells.getString();
			String name = loadSpells.getString();
			String tooltip = loadSpells.getString();
			byte rank = loadSpells.getByte();
			int effectValue = loadSpells.getInt();
			int stunDuration = loadSpells.getInt();
			float stunRate = loadSpells.getFloat();
			int manaCost = loadSpells.getInt();
			boolean triggerGCD = loadSpells.getBoolean();
			int cd = loadSpells.getInt();
			int castTime = loadSpells.getInt();
			SpellMagicalSchool magicalSchool = convStringToMagicalSchool(loadSpells.getString());
			boolean isMagical = loadSpells.getBoolean();
			StoreSpell.createSpell(id, sprite_id, name, rank, effectValue, manaCost, stunRate, stunDuration, cd, castTime, triggerGCD, magicalSchool, isMagical);
			amount++;
		}
		System.out.println("Loaded "+amount+" spells in "+(System.nanoTime()-timer)/1000+" µs.");
	}
	
	public static boolean exists(int id) {
		return spellMap.containsKey(id);
	}
	
	public static SpellMagicalSchool convStringToMagicalSchool(String type) {
		if(type.equals("SHADOW")) {
			return SpellMagicalSchool.SHADOW;
		}
		if(type.equals("HOLY")) {
			return SpellMagicalSchool.HOLY;
		}
		if(type.equals("FROST")) {
			return SpellMagicalSchool.FROST;
		}
		if(type.equals("ARCANE")) {
			return SpellMagicalSchool.ARCANE;
		}
		if(type.equals("NATURE")) {
			return SpellMagicalSchool.NATURE;
		}
		if(type.equals("FIRE")) {
			return SpellMagicalSchool.FIRE;
		}
		System.out.println("Unknown spell's magical school SpellMgr:convStringToMagicalSchool : "+type);
		return null;
	}
	
	public static SpellType getSpellType(String type) {
		if(type.equals("DAMAGE")) {
			return SpellType.DAMAGE;
		}
		if(type.equals("HEAL")) {
			return SpellType.HEAL;
		}
		if(type.equals("HEALANDDAMAGE")) {
			return SpellType.HEALANDDAMAGE;
		}
		if(type.equals("OTHER")) {
			return SpellType.OTHER;
		}
		return null;
	}
	
	public static void store(Spell spell) {
		spellMap.put(spell.getSpellId(), spell);
	}
	
	public static Spell getSpell(int id) {
		return spellMap.get(id);
	}
	
	public static int getNumberSpellLoaded() {
		return numberSpellLoaded;
	}
}

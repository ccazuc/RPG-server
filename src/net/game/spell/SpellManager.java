package net.game.spell;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import jdo.JDOStatement;
import net.Servers;
import net.command.CommandUpdateStats;
import net.connection.PacketID;
import net.game.Unit;
import net.game.UnitType;
import net.game.shortcut.SpellShortcut;
import net.game.Player;

public class SpellManager {

	private static HashMap<Integer, Integer> spellCdList = new HashMap<Integer, Integer>();
	private static ArrayList<Spell> spellList = new ArrayList<Spell>();
	private static ArrayList<SpellShortcut> spellShortcutList = new ArrayList<SpellShortcut>();
	private static int numberSpellLoaded;
	private static JDOStatement loadSpells;
	private static JDOStatement loadDamageSpells;
	private static JDOStatement loadHealSpells;
	
	public static void loadSpells() throws SQLException {
		if(loadSpells == null) {
			loadSpells = Servers.getJDO().prepare("SELECT type, id FROM spell");
		}
		loadSpells.clear();
		loadSpells.execute();
		while(loadSpells.fetch()) {
			String type = loadSpells.getString();
			int id = loadSpells.getInt();
			if(type.equals("DAMAGE")) {
				if(loadDamageSpells == null) {
					loadDamageSpells = Servers.getJDO().prepare("SELECT sprite_id, name, damage, manaCost, cd, cast_time, stun_duration, stun_rate FROM SPELL WHERE id = ?");
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
					spellCdList.put(id, 0);
					spellList.add(new Spell(id, sprite_id, name, spellType, damage, manaCost, stunRate, stunDuration, cd, castTime) {
						@Override
						public void action(Unit target, Unit caster) {
							this.doDamage(target, caster);
							if(caster.getUnitType() == UnitType.PLAYER) {
								CommandUpdateStats.write((Player)caster, PacketID.UPDATE_STATS_STAMINA, target.getid(), target.getStamina());
								//((Player)caster).getConnectionManager().getCommandList().get((int)PacketID.UPDATE_STATS).write(PacketID.UPDATE_STATS_STAMINA, target.getid(), target.getStamina());
							}
						}
					});
				}
			}
			else if(type.equals("HEAL")) {
				if(loadHealSpells == null) {
					loadHealSpells = Servers.getJDO().prepare("SELECT sprite_id, name, heal, manaCost, cd, cast_time FROM SPELL WHERE id = ?");
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
					spellCdList.put(id, 0);
					spellList.add(new Spell(id, sprite_id, name, spellType, manaCost, heal, cd, castTime) {
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
	}
	
	public static boolean exists(int id) {
		return spellList.contains(getBookSpell(id));
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
	
	public static Spell getBookSpell(int id) {
		int i = 0;
		while(i < spellList.size()) {
			if(spellList.get(i).getSpellId() == id) {
				return spellList.get(i);
			}
			i++;
		}
		return null;
	}

	public static SpellShortcut getShortcutSpell(int id) {
		int i = 0;
		while(i < spellShortcutList.size()) {
			if(spellShortcutList.get(i).getSpell().getSpellId() == id) {
				return spellShortcutList.get(i);
			}
			i++;
		}
		return null;
	}
	
	public static int getCd(int id) {
		return spellCdList.get(id);
	}
	
	public static void setCd(int id, int cd) {
		spellCdList.put(id, cd);
	}
	
	public static ArrayList<SpellShortcut> getSpellShortcutList() {
		return spellShortcutList;
	}
	
	public static int getNumberSpellLoaded() {
		return numberSpellLoaded;
	}
}

package net.game.spell;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Server;
import net.game.Player;
import net.game.item.potion.PotionManager;
import net.game.item.stuff.StuffManager;
import net.game.item.weapon.WeaponManager;
import net.game.shortcut.PotionShortcut;
import net.game.shortcut.Shortcut;
import net.game.shortcut.SpellShortcut;
import net.game.shortcut.StuffShortcut;

public class SpellBarManager {
	
	private static JDOStatement loadSpellBar;
	private static JDOStatement setSpellBar;

	public void loadSpellBar(Player player) {
		try {
			if(loadSpellBar == null) {
				loadSpellBar = Server.getJDO().prepare("SELECT slot1, slot2, slot3, slot4, slot5, slot6, slot7, slot8, slot9, slot10, slot11, slot12, slot13, slot14, slot15, slot16, slot17, slot18, slot19, slot20, slot21, slot22, slot23, slot24, slot25, slot26, slot27, slot28, slot29, slot30, slot31, slot32, slot33, slot34, slot35, slot36 FROM spellbar WHERE character_id = ?");
			}
			int i = 0;
			int id;
			loadSpellBar.clear();
			loadSpellBar.putInt(player.getCharacterId());
			loadSpellBar.execute();
			if(loadSpellBar.fetch()) {
				while(i < 36) {
					id = loadSpellBar.getInt();
					if(StuffManager.exists(id)) {
						player.setSpells(i, new StuffShortcut(StuffManager.getStuff(id)));
					}
					else if(PotionManager.exists(id)) {
						player.setSpells(i, new PotionShortcut(PotionManager.getPotion(id)));
					}
					else if(SpellManager.exists(id)) {
						player.setSpells(i, new SpellShortcut(SpellManager.getBookSpell(id)));
					}
					else if(WeaponManager.exists(id)) {
						player.setSpells(i, new StuffShortcut(WeaponManager.getWeapon(id)));
					}
					i++;
				}
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setSpellBar(Player player) throws SQLException {
		if(setSpellBar == null) {
			setSpellBar = Server.getJDO().prepare("UPDATE spellbar SET slot1 = ?, slot2 = ?, slot3 = ?, slot4 = ?, slot5 = ?, slot6 = ?, slot7 = ?, slot8 = ?, slot9 = ?, slot10 = ?, slot11 = ?, slot12 = ?, slot13 = ?, slot14 = ?, slot15 = ?, slot16 = ?, slot17 = ?, slot18 = ?, slot19 = ?, slot20 = ?, slot21 = ?, slot22 = ?, slot23 = ?, slot24 = ?, slot25 = ?, slot26 = ?, slot27 = ?, slot28 = ?, slot29 = ?, slot30 = ?, slot31 = ?, slot32 = ?, slot33 = ?, slot34 = ?, slot35 = ?, slot36 = ? WHERE character_id = ?");
		}
		setSpellBar.clear();
		int i = 0;
		while(i < 36) {
			Shortcut tempSpell = player.getSpells(i);
			int id = 0;
			if(tempSpell != null) {
				id = tempSpell.getId();
			}
			setSpellBar.putInt(id);
			i++;
		}
		setSpellBar.putInt(player.getCharacterId());
		setSpellBar.execute();
	}
}

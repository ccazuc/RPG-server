package net.game.shortcut;

import java.sql.SQLException;

import net.game.Player;
import net.game.spell.Spell;

public class SpellShortcut implements Shortcut {
	
	private Spell spell;
	private ShortcutType type;
	
	public SpellShortcut(Spell spell) {
		this.spell = spell;
		this.type = ShortcutType.SPELL;
	}
	
	
	@Override
	public boolean use(Player player, Shortcut spell) throws SQLException {
		/*if(player.cast(((SpellShortcut)spell).getSpell())) {
			SpellBarFrame.setIsCastingSpell(false);
			return true;
		}*/
		return false;
	}
	
	@Override
	public void setCd(int id, int cd) {
		//SpellManager.setCd(id, cd);
	}
	
	@Override
	public int getId() {
		return this.spell.getSpellId();
	}
	
	public Spell getSpell() {
		return this.spell;
	}
	
	@Override
	public ShortcutType getShortcutType() {
		return this.type;
	}
}

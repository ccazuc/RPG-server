package net.command;

import net.connection.ConnectionManager;
import net.game.spell.SpellManager;

public class CommandSpellCast extends Command {

	public CommandSpellCast(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	@Override
	public void read() {
		int spellId = this.connection.readInt();
		if(SpellManager.exists(spellId)) {
			/*int i = 0;
			while(i < this.player.getSpellUnlocked().length) {
				if(this.player.getSpellUnlocked(i) != null && this.player.getSpellUnlocked(i).getSpellId() == spellId) {
					this.player.getSpellUnlocked(i).action(this.player, this.player.getTarget());
					break;
				}
				i++;
			}*/
			SpellManager.getBookSpell(spellId).action(this.player.getTarget(), this.player);
		}
	}
}

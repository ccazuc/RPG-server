package net.game.shortcut;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import net.game.item.potion.Potion;
import net.game.unit.Player;

public class PotionShortcut implements Shortcut {
	
	private Potion potion;
	private ShortcutType type;
	
	public PotionShortcut(Potion potion) {
		this.potion = potion;
		this.type = ShortcutType.POTION;
	}
	
	@Override
	public boolean use(Player player, Shortcut potion) throws SQLException, FileNotFoundException {
		int i = 0;
		while(i < player.getBag().getBag().length) {
			if(player.getBag().getBag(i) != null && player.getBag().getBag(i).getId() == ((PotionShortcut)potion).getId()) {
				//SpellBarFrame.doHealingPotion((Potion)Mideas.bag().getBag(i));
				return true;
			}
			i++;
		}
		return true;
	}
	
	@Override
	public void setCd(int id, int cd) {
	}
	
	@Override
	public int getId() {
		return this.potion.getId();
	}
	
	public Potion getPotion() {
		return this.potion;
	}
	
	@Override
	public ShortcutType getShortcutType() {
		return this.type;
	}
}

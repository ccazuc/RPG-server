package net.game.shortcut;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import net.game.Player;
import net.game.item.stuff.Stuff;

public class StuffShortcut implements Shortcut {

	private Stuff stuff;
	private ShortcutType type;

	public StuffShortcut(Stuff stuff) {
		this.stuff = stuff;
		this.type = ShortcutType.STUFF;
	}
	
	@Override
	public boolean use(Player player, Shortcut shortcut) throws FileNotFoundException, SQLException {
		/*int i = 0;
			while(i < player.getStuff().length) {
				if(this.stuff.isStuff()) {
					if(this.stuff != null && this.stuff.getType().getValue() == i && this.stuff.canEquipTo(DragManager.convClassType()) && DragManager.canWear(this.stuff)) {
						if(player.getLevel() >= this.stuff.getLevel()) {
							if(player.getStuff(i) == null) {
								player.setStuff(i, this.stuff);
								DragManager.calcStats(Mideas.joueur1().getStuff(i));
								DragManager.setNullContainer(Mideas.bag().getBag(i));
								CharacterStuff.setBagItems();
								CharacterStuff.setEquippedItems();
								break;
							}
							else {
								Item tempItem = Mideas.joueur1().getStuff(i);
								DragManager.calcStatsLess(tempItem);
								Mideas.joueur1().setStuff(i, this.stuff);
								DragManager.calcStats(Mideas.joueur1().getStuff(i));
								Mideas.bag().setBag(DragManager.checkItemSlot(this.stuff), tempItem);
								CharacterStuff.setBagItems();
								CharacterStuff.setEquippedItems();
								break;
							}
						}
					}
				}
				else if(this.stuff.isWeapon()) {
					if(this.stuff != null && this.stuff.getWeaponSlot() == DragManager.getWeaponSlot(i) && this.stuff.canEquipTo(DragManager.convClassType())) {
						if(Mideas.getLevel() >= this.stuff.getLevel()) {
							if(Mideas.joueur1().getStuff(i) == null) {
								Mideas.joueur1().setStuff(i, this.stuff);
								DragManager.calcStats(Mideas.joueur1().getStuff(i));
								DragManager.setNullContainer(Mideas.bag().getBag(i));
								CharacterStuff.setBagItems();
								CharacterStuff.setEquippedItems();
								break;
							}
							else {
								Item tempItem = Mideas.joueur1().getStuff(i);
								DragManager.calcStatsLess(tempItem);
								Mideas.joueur1().setStuff(i, this.stuff);
								DragManager.calcStats(Mideas.joueur1().getStuff(i));
								Mideas.bag().setBag(DragManager.checkItemSlot(this.stuff), tempItem);
								CharacterStuff.setBagItems();
								CharacterStuff.setEquippedItems();
								break;
							}
						}
					}
				}
				i++;
			}
		}*/
		return false;
	}
	
	@Override
	public int getId() {
		return this.stuff.getId();
	}

	@Override
	public void setCd(int id, int cd) {
	}
	
	public Stuff getStuff() {
		return this.stuff;
	}
	
	@Override
	public ShortcutType getShortcutType() {
		return this.type;
	}
}

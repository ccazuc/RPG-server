package net.game.shortcut;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import net.game.Player;

public interface Shortcut {

	abstract boolean use(Player player, Shortcut shortcut) throws SQLException, FileNotFoundException;
	abstract void setCd(int id, int cd);
	abstract ShortcutType getShortcutType();
	abstract int getId();
}

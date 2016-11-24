package net.command;

import net.connection.Connection;
import net.game.Player;

public class Command {
	
	public Command() {}
	
	public void read(@SuppressWarnings("unused") Player player) {}
	public void read(@SuppressWarnings("unused") Connection connection) {}
	public void write() {}
}

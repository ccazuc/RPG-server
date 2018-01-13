package net.command;

import net.connection.Connection;
import net.game.unit.Player;

public class Command {
	
	private final String name;
	private final boolean debug;

	public Command(String name, boolean debug)
	{
		this.name = name;
		this.debug = debug;
	}
	
	public Command(String name)
	{
		this(name, false);
	}
	
	public String getName()
	{
		return (this.name);
	}
	
	public boolean debug()
	{
		return (this.debug);
	}
	
	public void read(@SuppressWarnings("unused") Player player) {}
	public void read(@SuppressWarnings("unused") Connection connection) {}
}

package net.command.player;

import net.command.Command;
import net.game.unit.Player;

public class CommandPingConfirmed extends Command
{

	public CommandPingConfirmed(String name, boolean debug)
	{
		super(name, debug);
	}
	
	@Override
	public void read(Player player)
	{
		player.setPingStatus(false);
	}
}

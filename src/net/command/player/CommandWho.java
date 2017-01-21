package net.command.player;

import net.Server;
import net.command.Command;
import net.command.chat.CommandSendMessage;
import net.command.chat.MessageType;
import net.game.unit.Player;
import net.thread.chatcommand.Who;

public class CommandWho extends Command {
	
	@Override
	public void read(Player player) {
		String word = player.getConnection().readString();
		if(player.getLastWhoTimer()+Player.WHO_COMMAND_FREQUENCE < Server.getLoopTickTimer()) {
			CommandSendMessage.selfWithoutAuthor(player.getConnection(), "Please wait before doing a new request.", MessageType.SELF);
			return;
		}
		Server.addNewWhoRequest(new Who(word, player));
		player.setLastWhoTimer(Server.getLoopTickTimer());
	}
}

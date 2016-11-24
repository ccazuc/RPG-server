package net.command;

import net.Server;
import net.connection.Connection;
import net.game.Player;

public class CommandLoadCharacter extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		int id = connection.readInt();
		if(Server.getLoggedPlayerList().containsKey(player.getAccountId())) {
			player.setCharacterId(id);
			player.initTable();
			player.loadCharacterInfoSQL();
			player.sendStats();
			player.loadEquippedBagSQL();
			player.loadEquippedItemSQL();
			player.loadBagItemSQL();
			player.loadFriendList();
			player.updateLastLoginTimer();
			CommandFriend.loadFriendList(player);
			player.notifyFriendOnline();
			player.loadGuild();
			if(player.getGuild() != null) {
				CommandGuild.initGuildWhenLogin(connection, player);
				player.getGuild().getMember(player.getCharacterId()).setOnlineStatus(true);
				CommandGuild.notifyOnlinePlayer(player);
			}
			//this.player.loadSpellUnlocked();
			Server.addInGamePlayer(player);
			Server.removeLoggedPlayer(player);
		}
		else {
			//player is using WPE
		}
	}
	
	@Override
	public void write() {}
}

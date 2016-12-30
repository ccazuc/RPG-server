package net.command.player;

import net.Server;
import net.command.Command;
import net.connection.Connection;
import net.game.Player;
import net.game.log.Log;
import net.game.manager.CharacterMgr;
import net.game.manager.IgnoreMgr;

public class CommandLoadCharacter extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		int id = connection.readInt();
		if(!Server.getLoggedPlayerList().containsKey(player.getAccountId())) {
			Log.writePlayerLog(player, "Tried to load character "+id+" whereas he's not connected");
			player.close();
			return;
		}
		if(!CharacterMgr.checkPlayerAccount(player.getAccountId(), id)) {
			player.close();
			Log.writePlayerLog(player, "tried to connect on someone else's character (id = "+id+')');
			return;
		}
		CharacterMgr.fullyLoadCharacter(player, id);
		/*player.setOnline();
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
		IgnoreMgr.loadIgnoreList(player.getCharacterId());
		CommandIgnore.ignoreInit(player.getConnection(), player);
		player.loadGuild();
		if(player.getGuild() != null) {
			CommandGuild.initGuildWhenLogin(player);
			player.getGuild().getMember(player.getCharacterId()).setOnlineStatus(true);
			CommandGuild.notifyOnlinePlayer(player);
		}
		//this.player.loadSpellUnlocked();
		Server.addInGamePlayer(player);
		Server.removeLoggedPlayer(player);*/
	}
	
	@Override
	public void write() {}
}

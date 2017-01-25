package net.command.player;

import net.Server;
import net.command.Command;
import net.connection.Connection;
import net.game.log.Log;
import net.game.manager.CharacterMgr;
import net.game.unit.Player;

public class CommandLoadCharacter extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		int id = connection.readInt();
		if(!Server.getLoggedPlayerList().containsKey(player.getAccountId())) {
			Log.writePlayerLog(player, new StringBuilder().append("Tried to load character ").append(id).append(" whereas he's not connected").toString());
			player.close();
			return;
		}
		if(!CharacterMgr.checkPlayerAccount(player.getAccountId(), id)) {
			player.close();
			Log.writePlayerLog(player, new StringBuilder().append("tried to connect on someone else's character (id = ").append(id).append(')').toString());
			return;
		}
		//System.out.println("CHARACTER LOAD ID : "+id);
		player.resetTarget();
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

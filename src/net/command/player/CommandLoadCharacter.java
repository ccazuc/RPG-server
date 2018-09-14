package net.command.player;

import net.Server;
import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.log.Log;
import net.game.manager.BanMgr;
import net.game.manager.CharacterMgr;
import net.game.unit.Player;

public class CommandLoadCharacter extends Command
{

	public CommandLoadCharacter(String name, boolean debug)
	{
		super(name, debug);
	}
	
	@Override
	public void read(Player player)
	{
		Connection connection = player.getConnection();
		int id = connection.readInt();
		//System.out.println("Character " + id + " tried to connect");
		long duration;
		if (!Server.getLoggedPlayerList().containsKey(player.getAccountId()))
		{
			Log.writePlayerLog(player, new StringBuilder().append("Tried to load character ").append(id).append(" whereas he's not connected").toString());
			player.close();
			return;
		}
		if(!CharacterMgr.checkPlayerAccount(player.getAccountId(), id))
		{
			player.close();
			Log.writePlayerLog(player, new StringBuilder().append("tried to connect on someone else's character (id = ").append(id).append(')').toString());
			return;
		}
		if ((duration = BanMgr.isCharacterBannedHighAsync(id)) != -1)
		{
			characterBanned(player, duration);
			return;
		}
		//System.out.println("CHARACTER LOAD ID : "+id);
		player.setUnitID(id);
		player.resetTarget();
		player.setLoginTimer();
		Server.removeLoggedPlayer(player);
		Server.addInGamePlayer(player);
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
	
	public static void characterBanned(Player player, long duration)
	{
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.CHARACTER_LOGIN);
		connection.writeShort(PacketID.CHARACTER_LOGIN_BANNED);
		if (duration == 0)
		{
			connection.writeBoolean(true);
		}
		else
		{
			connection.writeBoolean(false);
			connection.writeLong(duration);
		}
		connection.endPacket();
		connection.send();
	}
}

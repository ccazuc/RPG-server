package net.command.player;

import net.Server;
import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.log.Log;
import net.game.manager.CharacterMgr;
import net.game.premade_group.PremadeGroup;
import net.game.premade_group.PremadeGroupApplication;
import net.game.premade_group.PremadeGroupMgr;
import net.game.premade_group.PremadeGroupType;
import net.game.unit.Player;

public class CommandPremadeGroup extends Command
{

	public CommandPremadeGroup(String name, boolean debug)
	{
		super(name, debug);
	}
	
	@Override
	public void read(Player player)
	{
		Connection connection = player.getConnection();
		short packetId = connection.readShort();
		if (packetId == PacketID.PREMADE_GROUP_CREATE)
		{
			String title = connection.readString();
			String description = connection.readString();
			int requiredLevel = connection.readInt();
			PremadeGroupType type = PremadeGroupType.getValue(connection.readByte());
			if (type == null)
			{
				Log.writePlayerLog(player, "tried to create a premade group with an invalid type: " + type);
				return;
			}
			if (title.length() == 0 || title.length() > 30)
				return;
			if (description.length() > 200)
				return;
			if (player.getPremadeGroup() != null)
			{
				Log.writePlayerLog(player, "tried to create a premade group whereas he's already in a premade group.");
				return;
			}
			if (player.getParty() != null && player.getParty().getLeaderId() != player.getUnitID())
			{
				Log.writePlayerLog(player, "tried to create a premade group whereas he's not leader.");
				return;
			}
			if (player.getParty() == null)
			{
				if (player.getLevel() < requiredLevel)
					return;
			}
			else
			{
				int i = -1;
				while (++i < player.getParty().getPlayerList().length)
				{
					if (player.getParty().getPlayerList()[i] == 0)
						continue;
					Player tmp = Server.getInGameCharacter(player.getParty().getPlayerList()[i]);
					if (tmp != null)
					{
						if (tmp.getLevel() < requiredLevel)
							return;
					}
					else
					{
						int experience = CharacterMgr.getExperience(player.getParty().getPlayerList()[i]);
						if (experience == -1)
							continue;
						if (Player.getLevel(experience) < requiredLevel)
							return;
					}
				}
			}
				
			PremadeGroupMgr.addPremadeGroup(player, title, description, type, requiredLevel);
		}
		else if (packetId == PacketID.PREMADE_GROUP_DELIST)
		{
			if (player.getPremadeGroup() == null)
				return;
			if (player.getParty() != null && player.getParty().getLeaderId() != player.getUnitID())
				return;
			PremadeGroupMgr.delistPremadeGroup(player);
		}
		else if (packetId == PacketID.PREMADE_GROUP_ACCEPT_APPLICATION)
		{
			long applicationId = connection.readLong();
			if (player.getPremadeGroup() == null)
				return;
			if (player.getParty() != null && player.getParty().getLeaderId() != player.getUnitID())
				return;
			PremadeGroupMgr.acceptApplication(player, applicationId);
		}
		else if (packetId == PacketID.PREMADE_GROUP_REFUSE_APPLICATION)
		{
			long applicationId = connection.readLong();
			if (player.getPremadeGroup() == null)
				return;
			if (player.getParty() != null && player.getParty().getLeaderId() != player.getUnitID())
				return;
			PremadeGroupMgr.refuseApplication(player, applicationId);
		}
		else if (packetId == PacketID.PREMADE_GROUP_CANCEL_APPLICATION)
		{
			long applicationId = connection.readLong();
			if (player.getParty() != null && player.getParty().getLeaderId() != player.getUnitID())
				return;
			PremadeGroupMgr.cancelApplication(player, applicationId, true);
		}
	}
	
	/*
	 * Used when someone canceled his application to your group
	 */
	public static void sendApplicationCanceledFromGroup(PremadeGroupApplication application, Player player)
	{
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.PREMADE_GROUP);
		connection.writeShort(PacketID.PREMADE_GROUP_APPLICATION_CANCELED_FROM_GROUP);
		connection.writeLong(application.getId());
		connection.endPacket();
		connection.send();
	}
	
	/*
	 * Used when you cancel your application
	 */
	public static void sendApplicationCanceled(PremadeGroup group, Player player)
	{
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.PREMADE_GROUP);
		connection.writeShort(PacketID.PREMADE_GROUP_CANCEL_APPLICATION);
		connection.writeLong(group.getId());
		connection.endPacket();
		connection.send();
	}
	
	public static void sendGroupDelisted(PremadeGroup group, PremadeGroupApplication application)
	{
		if (application.getParty() != null)
		{
			int i = -1;
			while (++i < application.getPlayerList().length)
			{
				Player tmp = application.getParty().getPlayer(i);
				if (tmp == null)
					continue;
				Connection connection = tmp.getConnection();
				connection.startPacket();
				connection.writeShort(PacketID.PREMADE_GROUP);
				connection.writeShort(PacketID.PREMADE_GROUP_DELISTED);
				connection.writeLong(group.getId());
				connection.endPacket();
				connection.send();
			}
		}
		else
		{
			Player tmp = Server.getInGameCharacter(application.getPlayerId());
			if (tmp == null)
				return;
			Connection connection = tmp.getConnection();
			connection.startPacket();
			connection.writeShort(PacketID.PREMADE_GROUP);
			connection.writeShort(PacketID.PREMADE_GROUP_DELISTED);
			connection.writeLong(group.getId());
			connection.endPacket();
			connection.send();
		}
			
	}

	public static void sendGroupDelisted(Player player)
	{
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.PREMADE_GROUP);
		connection.writeShort(PacketID.PREMADE_GROUP_DELISTED);
		connection.writeLong(player.getPremadeGroup().getId());
		connection.endPacket();
		connection.send();
	}
	
	public static void sendGroupCreated(Player player)
	{
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.PREMADE_GROUP);
		connection.writeShort(PacketID.PREMADE_GROUP_CREATE);
		connection.writeLong(player.getPremadeGroup().getId());
		connection.writeString(player.getPremadeGroup().getName());
		connection.writeString(player.getPremadeGroup().getDescription());
		connection.writeInt(player.getPremadeGroup().getRequiredLevel());
		connection.endPacket();
		connection.send();
	}
	
	public static void sendEveryonePremadeGroupDelisted(PremadeGroup group)
	{
		
	}
}

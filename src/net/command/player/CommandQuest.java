package net.command.player;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.log.Log;
import net.game.quest.PlayerQuest;
import net.game.quest.PlayerQuestMgr;
import net.game.quest.PlayerQuestObjective;
import net.game.quest.Quest;
import net.game.quest.QuestMgr;
import net.game.unit.Player;

public class CommandQuest extends Command
{

	public CommandQuest(String name, boolean debug)
	{
		super(name, debug);
	}
	
	@Override
	public void read(Player player)
	{
		Connection connection = player.getConnection();
		short packetId = connection.readShort();
		if (packetId == PacketID.QUEST_COMPLETE_REQUEST)
		{
			int questId = connection.readInt();
			Quest quest = QuestMgr.getQuest(questId);
			if (quest == null)
			{
				Log.writePlayerLog(player, "Tried to complete a quest that doesn't exist, questId: "+questId);
				return;
			}
			player.getQuestManager().completeQuest(quest);
		}
		else if (packetId == PacketID.QUEST_ACCEPT)
		{
			int questId = connection.readInt();
			Quest quest = QuestMgr.getQuest(questId);
			if (quest == null)
			{
				Log.writePlayerLog(player, "Tried to accept a quest that doesn't exist, questId: "+questId);
				return;
			}
			player.getQuestManager().acceptQuest(quest);
		}
		else if (packetId == PacketID.QUEST_CANCEL)
		{
			int questId = connection.readInt();
			Quest quest = QuestMgr.getQuest(questId);
			if (quest == null)
			{
				Log.writePlayerLog(player, "Tried to cancel a quest that doesn't exist, questId: "+questId);
				return;
			}
			player.getQuestManager().cancelQuest(quest);
		}
	}
	
	public static void InitQuests(Player player)
	{
		PlayerQuestMgr questManager = player.getQuestManager();
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.QUEST);
		connection.writeShort(PacketID.QUEST_INIT);
		for (PlayerQuest quest : questManager.getQuestMap().values())
		{
			connection.writeInt(quest.getQuest().getId());
			connection.writeByte((byte)quest.getObjectives().size());
			int i = -1;
			while (++i < quest.getObjectives().size())
			{
				connection.writeShort(quest.getObjective(i).getObjective().getAmount());
				connection.writeString(quest.getObjective(i).getObjective().getDescription());
				connection.writeShort(quest.getObjective(i).getProgress());
			}
			connection.writeString(quest.getQuest().getTitle());
			connection.writeString(quest.getQuest().getDescription());
		}
		connection.endPacket();
		connection.send();
	}
	
	public static void ObjectiveUpdate(Player player, PlayerQuestObjective objective)
	{
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.QUEST);
		connection.writeShort(PacketID.QUEST_UPDATE_OBJECTIVE);
		connection.writeInt(objective.getPlayerQuest().getQuest().getId());
		connection.writeByte(objective.getObjective().getIndex());
		connection.writeShort(objective.getProgress());
		connection.endPacket();
		connection.send();
	}
	
	public static void questAccepted(Player player, PlayerQuest quest)
	{
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.QUEST);
		connection.writeShort(PacketID.QUEST_ACCEPT);
		connection.writeInt(quest.getQuest().getId());
		connection.writeByte((byte)quest.getObjectives().size());
		int i = -1;
		while (++i < quest.getObjectives().size())
		{
			connection.writeShort(quest.getObjective(i).getObjective().getAmount());
			connection.writeString(quest.getObjective(i).getObjective().getDescription());
		}
		connection.writeString(quest.getQuest().getTitle());
		connection.writeString(quest.getQuest().getDescription());
		connection.endPacket();
		connection.send();
	}
	
	public static void questCanceled(Player player, PlayerQuest quest)
	{
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.QUEST);
		connection.writeShort(PacketID.QUEST_CANCEL);
		connection.writeInt(quest.getQuest().getId());
		connection.endPacket();
		connection.send();
	}
	
	public static void sendAvailableQuest(Player player)
	{
		for (Quest quest : QuestMgr.getQuestMap().values())
		{
			if (quest.getRequiredLevel() > player.getLevel() || player.getQuestManager().hasCompletedQuest(quest) || !player.getQuestManager().hasUnlockedQuest(quest))
				continue;
		}
	}
}

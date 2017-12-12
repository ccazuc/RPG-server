package net.command.player;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.quest.PlayerQuest;
import net.game.quest.PlayerQuestMgr;
import net.game.quest.PlayerQuestObjective;
import net.game.unit.Player;

public class CommandQuest extends Command {

	@Override
	public void read(Player player) {
		
	}
	
	public static void InitQuests(Player player) {
		PlayerQuestMgr questManager = player.getQuestManager();
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.QUEST);
		connection.writeShort(PacketID.QUEST_INIT);
		for (PlayerQuest quest : questManager.getQuestMap().values()) {
			connection.writeInt(quest.getQuest().getId());
			connection.writeByte((byte)quest.getObjectives().size());
			int i = -1;
			while (++i < quest.getObjectives().size()) {
				connection.writeShort(quest.getObjective(i).getObjective().getAmount());
				connection.writeString(quest.getObjective(i).getObjective().getDescription());
				connection.writeShort(quest.getObjective(i).getProgress());
			}
			connection.writeString(quest.getQuest().getTitle());
			connection.writeString(quest.getQuest().getDescription());
		}
		connection.endPacket();
	}
	
	public static void ObjectiveUpdate(Player player, PlayerQuestObjective objective) {
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.QUEST);
		connection.writeShort(PacketID.QUEST_UPDATE_OBJECTIVE);
		connection.writeInt(objective.getPlayerQuest().getQuest().getId());
		connection.writeByte(objective.getObjective().getIndex());
		connection.writeShort(objective.getProgress());
		connection.endPacket();
	}
}

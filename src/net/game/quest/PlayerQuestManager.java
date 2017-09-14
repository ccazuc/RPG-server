package net.game.quest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import jdo.JDOStatement;
import net.Server;
import net.game.unit.Player;

public class PlayerQuestManager {

	@SuppressWarnings("unchecked")
	private final ArrayList<PlayerQuestObjective>[] callback = (ArrayList<PlayerQuestObjective>[]) new ArrayList<?>[4];
	private static JDOStatement loadQuestsStatement;
	private final HashMap<Integer, PlayerQuest> questList;
	private final Player player;
	
	public PlayerQuestManager(Player player) {
		this.questList = new HashMap<Integer, PlayerQuest>();
		this.player = player;
		int i = -1;
		while (++i < this.callback.length)
			this.callback[i] = new ArrayList<PlayerQuestObjective>();
	}
	
	public static void loadPlayerQuest(Player player) {
		try  {
			if (loadQuestsStatement == null)
				loadQuestsStatement = Server.getJDO().prepare("SELECT `quest_id`, `accepted_timestamp`, `objective1_progress`, `objective2_progress`, `objective3_progress`, `objective4_progress` FROM `character_quests` WHERE `user_id` = ?");
			loadQuestsStatement.clear();
			loadQuestsStatement.putInt(player.getUnitID());
			loadQuestsStatement.execute();
			while (loadQuestsStatement.fetch()) {
				int questId = loadQuestsStatement.getInt();
				long acceptedTimestamp = loadQuestsStatement.getLong();
				short objective1Progress = loadQuestsStatement.getShort();
				short objective2Progress = loadQuestsStatement.getShort();
				short objective3Progress = loadQuestsStatement.getShort();
				short objective4Progress = loadQuestsStatement.getShort();
				Quest quest = QuestManager.getQuest(questId);
				if (quest == null)
					continue;
				PlayerQuest playerQuest = new PlayerQuest(quest, acceptedTimestamp);
				if (!checkObjectiveOnLoad(quest, 0, playerQuest, objective1Progress)) continue;
				if (!checkObjectiveOnLoad(quest, 1, playerQuest, objective2Progress)) continue;
				if (!checkObjectiveOnLoad(quest, 2, playerQuest, objective3Progress)) continue;
				if (!checkObjectiveOnLoad(quest, 3, playerQuest, objective4Progress)) continue;				
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean checkObjectiveOnLoad(Quest quest, int objectiveIndex, PlayerQuest playerQuest, short progress) {
		QuestObjective objective = quest.getObjective(objectiveIndex);
		if (objective == null) {
			System.out.println("Error load quest objective for quest: "+quest.getId()+", objectiveIndex: "+objectiveIndex);
			return false;
		}
		playerQuest.addObjective(new PlayerQuestObjective(objective, progress, playerQuest));
		return true;
	}
	
	public void addObjectiveCallback(PlayerQuestObjective objective) {
		QuestObjectiveType type = objective.getObjectiveType();
		this.callback[type.getValue()].add(objective);
	}
	
	public void removeObjectiveCallback(PlayerQuestObjective objective) {
		int i = -1;
		while (++i < this.callback[objective.getObjectiveType().getValue()].size())
			if (this.callback[objective.getObjectiveType().getValue()].get(i) == objective) {
				this.callback[objective.getObjectiveType().getValue()].remove(i);
				break;
			}
	}
	
	public void completeQuest(Quest quest) {
		PlayerQuest playerQuest = this.questList.get(quest.getId());
		if (playerQuest == null)
			return;
		if (!playerQuest.isQuestCompleted())
			return;
		removeQuest(playerQuest);
		handleQuestReward(playerQuest.getQuest());
	}
	
	public void handleQuestReward(Quest quest) {
		
	}
	
	public void acceptQuest(Quest quest) {
		int i = -1;
		PlayerQuest playerQuest = new PlayerQuest(quest, Server.getLoopTickTimer());
		while (++i < quest.getObjectives().size()) {
			PlayerQuestObjective playerQuestObjective = new PlayerQuestObjective(quest.getObjective(i), (short)0, playerQuest);
			playerQuest.addObjective(playerQuestObjective);
			addObjectiveCallback(playerQuestObjective);
		}
	}
	
	public void removeQuest(PlayerQuest playerQuest) {
		this.questList.remove(playerQuest.getQuest().getId());
		int i = -1;
		while (++i < playerQuest.getObjectives().size())
			removeObjectiveCallback(playerQuest.getObjectives().get(i));
	}
	
	public PlayerQuest getPlayerQuest(Quest quest) {
		return getPlayerQuest(quest.getId());
	}
	
	public void onUnitKilled(int unitId) {
		int i = -1;
		ArrayList<PlayerQuestObjective> list = this.callback[QuestObjectiveType.QUEST_OBJECTIVE_NPC.getValue()];
		while (++i < list.size()) {
			if (list.get(i).getObjective().getObjectiveId() == unitId)
				list.get(i).addProgress((short)1);
		}
	}
	
	public PlayerQuest getPlayerQuest(int id) {
		return this.questList.get(id);
	}
	
	public Player getPlayer() {
		return this.player;
	}
}

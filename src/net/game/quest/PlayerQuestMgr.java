package net.game.quest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import jdo.JDOStatement;
import net.Server;
import net.game.unit.Player;
import net.thread.log.LogRunnable;

public class PlayerQuestMgr {

	@SuppressWarnings("unchecked")
	private final ArrayList<PlayerQuestObjective>[] callback = (ArrayList<PlayerQuestObjective>[]) new ArrayList<?>[4];
	private static JDOStatement loadQuestsStatement;
	private static JDOStatement loadCompletedQuestStatement;
	private final HashMap<Integer, PlayerQuest> questMap;
	private final HashSet<Integer> completedQuestSet;
	private final Player player;
	
	public PlayerQuestMgr(Player player) {
		this.questMap = new HashMap<Integer, PlayerQuest>();
		this.completedQuestSet = new HashSet<Integer>();
		this.player = player;
		int i = -1;
		while (++i < this.callback.length)
			this.callback[i] = new ArrayList<PlayerQuestObjective>();
	}
	
	public void loadPlayerCompletedQuest()
	{
		try
		{
			if (loadCompletedQuestStatement == null)
				loadCompletedQuestStatement = Server.getJDO().prepare("SELECT `quest_id` FROM `character_quest_completed` WHERE `player_id` = ?");
			loadCompletedQuestStatement.clear();
			loadCompletedQuestStatement.putInt(this.player.getUnitID());
			loadCompletedQuestStatement.execute();
			while (loadCompletedQuestStatement.fetch())
			{
				int questId = loadCompletedQuestStatement.getInt();
				this.completedQuestSet.add(questId);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadPlayerQuest() {
		try  {
			if (loadQuestsStatement == null)
				loadQuestsStatement = Server.getJDO().prepare("SELECT `quest_id`, `accepted_timestamp`, `objective1_progress`, `objective2_progress`, `objective3_progress`, `objective4_progress` FROM `character_quests` WHERE `user_id` = ?");
			loadQuestsStatement.clear();
			loadQuestsStatement.putInt(this.player.getUnitID());
			loadQuestsStatement.execute();
			while (loadQuestsStatement.fetch()) {
				int questId = loadQuestsStatement.getInt();
				long acceptedTimestamp = loadQuestsStatement.getLong();
				short objective1Progress = loadQuestsStatement.getShort();
				short objective2Progress = loadQuestsStatement.getShort();
				short objective3Progress = loadQuestsStatement.getShort();
				short objective4Progress = loadQuestsStatement.getShort();
				Quest quest = QuestMgr.getQuest(questId);
				if (quest == null)
				{
					LogRunnable.addErrorLog("Error in PlayerQuestManager.loadPlayerQuest(), quest not found: "+questId+", playerId: "+this.player.getUnitID());
					continue;
				}
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
		if (objective == null)
			return false;
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
		PlayerQuest playerQuest = this.questMap.get(quest.getId());
		if (playerQuest == null || !playerQuest.isQuestCompleted())
			return;
		removeQuest(playerQuest);
		handleQuestReward(playerQuest.getQuest());
	}
	
	public void handleQuestReward(Quest quest) {
		this.player.setGold(this.player.getGold() + quest.getGoldReward());
		this.player.setExperience(this.player.getExperience() + quest.getExperienceReward());
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
		this.questMap.remove(playerQuest.getQuest().getId());
		int i = -1;
		while (++i < playerQuest.getObjectives().size())
			removeObjectiveCallback(playerQuest.getObjective(i));
	}
	
	public PlayerQuest getPlayerQuest(Quest quest) {
		return getPlayerQuest(quest.getId());
	}
	
	public void onUnitKilled(int unitId) {
		int i = -1;
		ArrayList<PlayerQuestObjective> list = this.callback[QuestObjectiveType.QUEST_OBJECTIVE_NPC.getValue()];
		while (++i < list.size())
			if (list.get(i).getObjective().getObjectiveId() == unitId)
				list.get(i).addProgress((short)1);
	}
	
	public void onItemLoot(int itemId, short amount) {
		int i = -1;
		ArrayList<PlayerQuestObjective> list = this.callback[QuestObjectiveType.QUEST_OBJECTIVE_ITEM.getValue()];
		while (++i < list.size())
			if (list.get(i).getObjective().getObjectiveId() == itemId)
				list.get(i).addProgress(amount);
	}
	
	public PlayerQuest getPlayerQuest(int id) {
		return this.questMap.get(id);
	}
	
	public HashMap<Integer, PlayerQuest> getQuestMap() {
		return this.questMap;
	}
	
	public Player getPlayer() {
		return this.player;
	}
}

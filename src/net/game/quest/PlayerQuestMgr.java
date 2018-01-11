package net.game.quest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import jdo.JDOStatement;
import net.Server;
import net.command.player.CommandQuest;
import net.command.player.CommandSendRedAlert;
import net.game.DefaultRedAlert;
import net.game.callback.CallbackMgr;
import net.game.callback.CallbackType;
import net.game.callback.UpdateAvailableQuestOnLevelupCallback;
import net.game.callback.UpdateAvailableQuestOnQuestCompletedCallback;
import net.game.unit.Player;
import net.thread.log.LogRunnable;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;
import net.thread.sql.SQLRequestPriority;

public class PlayerQuestMgr {

	@SuppressWarnings("unchecked")
	private final ArrayList<PlayerQuestObjective>[] callback = (ArrayList<PlayerQuestObjective>[]) new ArrayList<?>[4];
	private static JDOStatement loadQuestsStatement;
	private static JDOStatement loadCompletedQuestStatement;
	private final HashMap<Integer, PlayerQuest> questMap;
	private final HashSet<Integer> completedQuestSet;
	private final HashSet<Integer> availableQuest;
	private final Player player;
	private final static UpdateAvailableQuestOnQuestCompletedCallback updateAvailableQuestOnQuestCompletedCallback = new UpdateAvailableQuestOnQuestCompletedCallback();
	private final static UpdateAvailableQuestOnLevelupCallback updateAvailableQuestOnLevelupCallback = new UpdateAvailableQuestOnLevelupCallback();
	private final static SQLRequest addQuestToDB = new SQLRequest("INSERT INTO `character_quests` (`player_id`, `quest_id`, `accepted_timestamp`, `objective1_progress`, `objective2_progress`, `objective3_progress`, `objective4_progress` VALUES (?, ?, ?, 0, 0, 0, 0)", "Add player quest", SQLRequestPriority.HIGH) {
		
		@Override
		public void gatherData() throws SQLException {
			SQLDatas datas = this.datasList.get(0);
			this.statement.putInt((int)datas.getNextObject());
			this.statement.putInt((int)datas.getNextObject());
			this.statement.putLong((long)datas.getNextObject());
		}
	};
	private final static SQLRequest removeQuestFromDB = new SQLRequest("DELETE FROM `character_quests` WHERE `player_id` = ? AND `quest_id` = ?", "Remove player quest", SQLRequestPriority.HIGH) {
		
		@Override
		public void gatherData() throws SQLException {
			SQLDatas datas = this.datasList.get(0);
			this.statement.putInt((int)datas.getNextObject());
			this.statement.putInt((int)datas.getNextObject());
		}
	};
	private final static SQLRequest addCompletedQuestToDB = new SQLRequest("INSERT INTO `character_quest_completed` (`player_id`, `quest_id`) VALUES (?, ?)", "Add completed player quest", SQLRequestPriority.HIGH) {
		
		@Override
		public void gatherData() throws SQLException {
			SQLDatas datas = this.datasList.get(0);
			this.statement.putInt((int)datas.getNextObject());
			this.statement.putInt((int)datas.getNextObject());
		}
	};
	
	public PlayerQuestMgr(Player player) {
		this.questMap = new HashMap<Integer, PlayerQuest>();
		this.completedQuestSet = new HashSet<Integer>();
		this.availableQuest = new HashSet<Integer>();
		this.player = player;
		int i = -1;
		while (++i < this.callback.length)
			this.callback[i] = new ArrayList<PlayerQuestObjective>();
		loadPlayerQuest();
		loadPlayerCompletedQuest();
	}
	
	public static void initCallback()
	{
		CallbackMgr.registerCallback(CallbackType.QUEST_COMPLETED, updateAvailableQuestOnQuestCompletedCallback);
		CallbackMgr.registerCallback(CallbackType.LEVEL_CHANGED, updateAvailableQuestOnLevelupCallback);
	}
	
	public void loadPlayerCompletedQuest()
	{
		try
		{
			if (loadCompletedQuestStatement == null)
				loadCompletedQuestStatement = Server.getJDO().prepare("SELECT `quest_id` FROM `character_quests_completed` WHERE `character_id` = ?");
			loadCompletedQuestStatement.clear();
			loadCompletedQuestStatement.putInt(this.player.getUnitID());
			loadCompletedQuestStatement.execute();
			while (loadCompletedQuestStatement.fetch())
				this.completedQuestSet.add(loadCompletedQuestStatement.getInt());
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadPlayerQuest() {
		try  {
			if (loadQuestsStatement == null)
				loadQuestsStatement = Server.getJDO().prepare("SELECT `quest_id`, `accepted_timestamp`, `objective1_progress`, `objective2_progress`, `objective3_progress`, `objective4_progress` FROM `character_quests` WHERE `character_id` = ?");
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
				PlayerQuest playerQuest = new PlayerQuest(this.player, quest, acceptedTimestamp);
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
	
	public static void addQuestDBAsyncHigh(Player player, PlayerQuest quest)
	{
		SQLDatas datas = new SQLDatas(player.getUnitID(), quest.getQuest().getId(), quest.getAcceptedTimestamp());
		addQuestToDB.addDatas(datas);
		Server.executeSQLRequest(addQuestToDB);
	}
	
	public static void removeQuestDBAsyncHigh(Player player, PlayerQuest quest)
	{
		SQLDatas datas = new SQLDatas(player.getUnitID(), quest.getQuest().getId());
		removeQuestFromDB.addDatas(datas);
		Server.executeSQLRequest(removeQuestFromDB);
	}
	
	public static void addCompletedQuestDBAsyncHigh(Player player, PlayerQuest quest)
	{
		SQLDatas datas = new SQLDatas(player.getUnitID(), quest.getQuest().getId());
		addCompletedQuestToDB.addDatas(datas);
		Server.executeSQLRequest(addCompletedQuestToDB);
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
		if (quest == null)
			return;
		PlayerQuest playerQuest = this.questMap.get(quest.getId());
		if (playerQuest == null)
			return;
		if (!playerQuest.isQuestCompleted())
		{
			CommandSendRedAlert.write(this.player, DefaultRedAlert.QUEST_NOT_COMPLETED);
			return;
		}
		if (playerQuest.getQuest().getState() == QuestStateType.DISABLED)
		{
			CommandSendRedAlert.write(this.player, DefaultRedAlert.QUEST_DISABLED);
			return;
		}
		this.completedQuestSet.add(playerQuest.getQuest().getId());
		addCompletedQuestDBAsyncHigh(this.player, playerQuest);
		removeQuest(playerQuest);
		handleQuestReward(playerQuest.getQuest());
		CallbackMgr.executeCallback(CallbackType.QUEST_COMPLETED, this.player, quest.getId());
	}
	
	public void cancelQuest(Quest quest)
	{
		if (quest == null)
			return;
		PlayerQuest playerQuest = this.questMap.get(quest.getId());
		if (playerQuest == null)
			return;
		removeQuest(playerQuest);
		CommandQuest.questCanceled(this.player, playerQuest);
	}
	
	public void handleQuestReward(Quest quest) {
		this.player.setGold(this.player.getGold() + quest.getGoldReward(), true);
		this.player.setExperience(this.player.getExperience() + quest.getExperienceReward(), true);
	}
	
	public boolean hasUnlockedQuest(Quest quest)
	{
		int i = -1;
		while (++i < quest.getPreviousQuestList().size())
			if (!this.completedQuestSet.contains(quest.getPreviousQuestList().get(i)))
				return (false);
		return (true);
	}
	
	public boolean hasCompletedQuest(Quest quest)
	{
		return (this.completedQuestSet.contains(quest.getId()));
	}
	
	public void updateAvailableQuest()
	{
		this.availableQuest.clear();
		for (Quest quest : QuestMgr.getQuestMap().values())
		{
			if (quest.getRequiredLevel() > this.player.getLevel() || this.completedQuestSet.contains(quest.getId()) || !hasUnlockedQuest(quest))
				continue;
			this.availableQuest.add(quest.getId());
		}
	}
	
	public void acceptQuest(Quest quest) {
		if (quest == null)
			return;
		if (!this.availableQuest.contains(quest.getId()))
		{
			CommandSendRedAlert.write(this.player, DefaultRedAlert.QUEST_NOT_UNLOCKED);
			return;
		}
		if (this.questMap.containsKey(quest.getId()))
		{
			CommandSendRedAlert.write(this.player, DefaultRedAlert.QUEST_ALREADY_ACCEPTED);
			return;
		}
		int i = -1;
		PlayerQuest playerQuest = new PlayerQuest(this.player, quest, Server.getLoopTickTimer());
		while (++i < quest.getObjectives().size()) {
			PlayerQuestObjective playerQuestObjective = new PlayerQuestObjective(quest.getObjective(i), (short)0, playerQuest);
			playerQuest.addObjective(playerQuestObjective);
			addObjectiveCallback(playerQuestObjective);
		}
		this.questMap.put(quest.getId(), playerQuest);
		CommandQuest.questAccepted(this.player, playerQuest);
		addQuestDBAsyncHigh(this.player, playerQuest);
	}
	
	public void removeQuest(PlayerQuest playerQuest) {
		if (!this.questMap.containsKey(playerQuest.getQuest().getId()))
			return;
		this.questMap.remove(playerQuest.getQuest().getId());
		int i = -1;
		while (++i < playerQuest.getObjectives().size())
			removeObjectiveCallback(playerQuest.getObjective(i));
		removeQuestDBAsyncHigh(this.player, playerQuest);
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
	
	public PlayerQuest getPlayerQuest(Quest quest) {
		return (this.questMap.get(quest.getId()));
	}
	
	public HashMap<Integer, PlayerQuest> getQuestMap() {
		return this.questMap;
	}
	
	public Player getPlayer() {
		return this.player;
	}
}

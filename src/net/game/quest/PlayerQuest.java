package net.game.quest;

import java.util.ArrayList;

import net.game.unit.Player;

public class PlayerQuest {

	private final Quest quest;
	private final long acceptedTimestamp;
	private final ArrayList<PlayerQuestObjective> objectives;
	private boolean questCompleted;
	private final Player player;
	
	public PlayerQuest(Player player, Quest quest, long acceptedTimestamp) {
		this.player = player;
		this.quest = quest;
		this.acceptedTimestamp = acceptedTimestamp;
		this.objectives = new ArrayList<PlayerQuestObjective>();
	}
	
	public void addObjective(PlayerQuestObjective objective) {
		if (this.quest.getState() == QuestStateType.AUTO_COMPLETE)
			objective.addProgress(objective.getObjective().getAmount());
		this.objectives.add(objective);
	}
	
	public PlayerQuestObjective getObjective(int index) {
		if (index < 0 || index >= this.objectives.size())
			return null;
		return (this.objectives.get(index));
	}
	
	public ArrayList<PlayerQuestObjective> getObjectives() {
		return (this.objectives);
	}
	
	public Player getPlayer()
	{
		return (this.player);
	}
	public void onObjectiveCompleted() {
		if (this.questCompleted)
			return;
		int i = 0;
		while (i < this.objectives.size()) {
			if (!this.objectives.get(i).isCompleted())
				return;
		}
		this.questCompleted = true;
	}
	
	public boolean isQuestCompleted() {
		return (this.questCompleted);
	}
	
	public Quest getQuest() {
		return (this.quest);
	}
	
	public long getAcceptedTimestamp() {
		return (this.acceptedTimestamp);
	}
}

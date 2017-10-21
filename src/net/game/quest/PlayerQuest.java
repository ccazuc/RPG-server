package net.game.quest;

import java.util.ArrayList;

public class PlayerQuest {

	private final Quest quest;
	private final long acceptedTimestamp;
	private final ArrayList<PlayerQuestObjective> objectives;
	private boolean questCompleted;
	
	public PlayerQuest(Quest quest, long acceptedTimestamp) {
		this.quest = quest;
		this.acceptedTimestamp = acceptedTimestamp;
		this.objectives = new ArrayList<PlayerQuestObjective>();
	}
	
	public void addObjective(PlayerQuestObjective objective) {
		this.objectives.add(objective);
	}
	
	public PlayerQuestObjective getObjective(int index) {
		if (index < 0 || index >= this.objectives.size()) {
			System.out.println("Error in PlayerQuest::getObjective, quest: "+this.quest.getId()+", objective: "+index);
			return null;
		}
		return this.objectives.get(index);
	}
	
	public ArrayList<PlayerQuestObjective> getObjectives() {
		return this.objectives;
	}

	public void objectiveCompleted() {
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
		return this.questCompleted;
	}
	
	public Quest getQuest() {
		return this.quest;
	}
	
	public long getAcceptedTimestamp() {
		return this.acceptedTimestamp;
	}
}

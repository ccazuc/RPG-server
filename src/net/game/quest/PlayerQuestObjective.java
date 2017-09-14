package net.game.quest;

public class PlayerQuestObjective {

	private final QuestObjective objective;
	private short progress;
	private final PlayerQuest playerQuest;
	
	public PlayerQuestObjective(QuestObjective objective, short progress, PlayerQuest playerQuest) {
		this.objective = objective;
		this.progress = progress;
		this.playerQuest = playerQuest;
	}
	
	public boolean isCompleted() {
		return this.progress >= this.objective.getAmount();
	}
	
	public PlayerQuest getPlayerQuest() {
		return this.playerQuest;
	}
	
	public QuestObjectiveType getObjectiveType() {
		return this.objective.getObjectiveType();
	}
	
	public QuestObjective getObjective() {
		return this.objective;
	}

	public void addProgress(short amount) {
		if (!isCompleted())
		{
			this.progress+= amount;
			if (isCompleted())
				this.playerQuest.objectiveCompleted();
		}
	}
}

package net.game.quest;

import java.util.ArrayList;

public class Quest {

	private final ArrayList<QuestObjective> objectives;
	private final int id;
	private final short requiredLevel;
	private final ArrayList<Integer> previousQuest;
	private final ArrayList<Integer> nextQuest;
	
	public Quest(int id, short requiredLevel) {
		this.id = id;
		this.requiredLevel = requiredLevel;
		this.objectives = new ArrayList<QuestObjective>();
		this.previousQuest = new ArrayList<Integer>();
		this.nextQuest = new ArrayList<Integer>();
	}
	
	public void addObjective(QuestObjective objective) {
		this.objectives.add(objective);
	}
	
	public QuestObjective getObjective(int index) {
		if (index >= 0 && index < this.objectives.size())
			return this.objectives.get(index);
		return null;
	}
	
	public ArrayList<QuestObjective> getObjectives() {
		return this.objectives;
	}
	
	public void addPreviousQuest(int id) {
		this.previousQuest.add(id);
	}
	
	public void addNextQuest(int id) {
		this.nextQuest.add(id);
	}
	
	public int getId() {
		return this.id;
	}
	
	public short getRequiredLevel() {
		return this.requiredLevel;
	}
}

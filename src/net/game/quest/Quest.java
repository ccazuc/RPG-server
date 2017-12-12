package net.game.quest;

import java.util.ArrayList;

public class Quest {

	private final ArrayList<QuestObjective> objectives;
	private final int id;
	private final short requiredLevel;
	private final int experienceReward;
	private final int goldReward;
	private final QuestStateType state;
	private final String title;
	private final String description;
	private final ArrayList<Integer> previousQuest;
	private final ArrayList<Integer> nextQuest;
	
	public Quest(int id, short requiredLevel, QuestStateType state, int experienceReward, int goldReward, String title, String description) {
		this.id = id;
		this.requiredLevel = requiredLevel;
		this.state = state;
		this.experienceReward = experienceReward;
		this.goldReward = goldReward;
		this.title = title;
		this.description = description;
		this.objectives = new ArrayList<QuestObjective>();
		this.previousQuest = new ArrayList<Integer>();
		this.nextQuest = new ArrayList<Integer>();
		QuestMgr.loadQuestObjective(this);
	}
	
	public void addObjective(QuestObjective objective) {
		this.objectives.add(objective);
	}
	
	public QuestObjective getObjective(int index) {
		if (index < 0 || index >= this.objectives.size()) {
			System.out.println("Error, invalid index for quest: "+this.id+", index: "+index);
			return null;
		}
		return this.objectives.get(index);
	}
	
	public ArrayList<QuestObjective> getObjectives() {
		return this.objectives;
	}
	
	public ArrayList<Integer> getPreviousQuestList()
	{
		return (this.previousQuest);
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
	
	public int getExperienceReward()
	{
		return (this.experienceReward);
	}
	
	public int getGoldReward()
	{
		return (this.goldReward);
	}

	public String getTitle() {
		return this.title;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public QuestStateType getState()
	{
		return (this.state);
	}
}

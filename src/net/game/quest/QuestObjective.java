package net.game.quest;

public class QuestObjective {

	private final int id;
	private final int objectiveId;
	private final int amount;
	private final QuestObjectiveType type;
	private final byte index;
	
	public QuestObjective(int id, int objectiveId, int amount, QuestObjectiveType type, byte index) {
		this.id = id;
		this.objectiveId = objectiveId;
		this.amount = amount;
		this.type = type;
		this.index = index;
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getObjectiveId() {
		return this.objectiveId;
	}
	
	public int getAmount() {
		return this.amount;
	}
	
	public QuestObjectiveType getObjectiveType() {
		return this.type;
	}
	
	public byte getIndex() {
		return this.index;
	}
}

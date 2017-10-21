package net.game.quest;

public class QuestObjective {

	private final int id;
	private final int objectiveId;
	private final short amount;
	private final String description;
	private final QuestObjectiveType type;
	private final byte index;
	
	public QuestObjective(int id, int objectiveId, short amount, QuestObjectiveType type, byte index) {
		this.id = id;
		this.objectiveId = objectiveId;
		this.description = null; //TODO: create description based on objective
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
	
	public short getAmount() {
		return this.amount;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public QuestObjectiveType getObjectiveType() {
		return this.type;
	}
	
	public byte getIndex() {
		return this.index;
	}
}

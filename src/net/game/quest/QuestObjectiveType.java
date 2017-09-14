package net.game.quest;

public enum QuestObjectiveType {


	QUEST_OBJECTIVE_NPC((byte)1),
	;
	
	private final byte value;
	
	private QuestObjectiveType(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}
}

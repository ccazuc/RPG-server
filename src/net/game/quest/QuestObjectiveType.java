package net.game.quest;

import net.thread.log.LogRunnable;

public enum QuestObjectiveType {


	QUEST_OBJECTIVE_NPC((byte)1),
	QUEST_OBJECTIVE_ITEM((byte)2),
	;
	
	private final byte value;
	
	private QuestObjectiveType(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}
	
	public static QuestObjectiveType getType(byte index)
	{
		if (index < 0 || index >= QuestObjectiveType.values().length)
		{
			LogRunnable.addErrorLog("Error in QuestObjectiveType.getType(), invalid index: "+index);
			return (null);
		}
		return (QuestObjectiveType.values()[index]);
	}
}

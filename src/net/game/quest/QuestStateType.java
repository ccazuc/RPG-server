package net.game.quest;

import net.thread.log.LogRunnable;

public enum QuestStateType {

	ENABLED((byte)0),
	AUTO_COMPLETE((byte)1),
	DISABLED((byte)2),
	;
	
	private final byte value;
	
	private QuestStateType(byte value)
	{
		this.value = value;
	}
	
	public byte getValue()
	{
		return (this.value);
	}
	
	public static QuestStateType getQuestStateType(byte index)
	{
		if (index < 0 || index >= QuestStateType.values().length)
		{
			LogRunnable.addErrorLog("Error in QuestStateType(), incorrect index: "+index);
			return (null);
		}
		return (QuestStateType.values()[index]);
	}
}

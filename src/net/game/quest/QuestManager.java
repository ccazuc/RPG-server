package net.game.quest;

import java.sql.SQLException;
import java.util.HashMap;

import jdo.JDOStatement;
import net.Server;
import net.thread.log.LogRunnable;

public class QuestManager {

	private final static HashMap<Integer, Quest> questMap = new HashMap<Integer, Quest>();
	private static JDOStatement loadQuestStatement;
	private static JDOStatement loadQuestObjectiveStatement;
	
	public static void loadQuest() {
		try {
			if (loadQuestStatement == null)
				loadQuestStatement = Server.getJDO().prepare("SELECT `id`, `required_level`, `title`, `description` FROM `quest`");
			loadQuestStatement.clear();
			loadQuestStatement.execute();
			while (loadQuestStatement.fetch()) {
				int id = loadQuestStatement.getInt();
				short requiredLevel = loadQuestStatement.getShort();
				String title = loadQuestStatement.getString();
				String description = loadQuestStatement.getString();
				addQuest(id, requiredLevel, title, description);
			}
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadQuestObjective(Quest quest) {
		try {
			if (loadQuestObjectiveStatement == null)
				loadQuestObjectiveStatement = Server.getJDO().prepare("SELECT `index`, `type`, `amount`, `objective_id`, `id` FROM `quest_objective` WHERE `quest_id` = ? ORDER BY `index` ASC");
			loadQuestObjectiveStatement.clear();
			loadQuestObjectiveStatement.putInt(quest.getId());
			loadQuestObjectiveStatement.execute();
			while (loadQuestObjectiveStatement.fetch()) {
				byte index = loadQuestObjectiveStatement.getByte();
				byte type = loadQuestObjectiveStatement.getByte();
				short amount = loadQuestObjectiveStatement.getShort();
				int data = loadQuestObjectiveStatement.getInt();
				int id = loadQuestObjectiveStatement.getInt();
				addObjective(quest, index, type, amount, data, id);
			}
				
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void addQuest(int id, short requiredLevel, String title, String description) {
		questMap.put(id, new Quest(id, requiredLevel, title, description));
		loadQuestObjective(questMap.get(id));
	}
	
	private static void addObjective(Quest quest, byte index, byte type, short amount, int objectiveId, int id)
	{
		QuestObjectiveType objectiveType = QuestObjectiveType.getType(type);
		if (objectiveType == null)
		{
			LogRunnable.addErrorLog("Error in QuestManager.addObjective(), invalid objective index for questId: "+quest.getId()+", index: "+type);
			return;
		}
		quest.addObjective(new QuestObjective(id, objectiveId, amount, objectiveType, index));
	}
	
	public static Quest getQuest(int id) {
		return questMap.get(id);
	}
}

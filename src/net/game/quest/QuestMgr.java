package net.game.quest;

import java.sql.SQLException;
import java.util.HashMap;

import jdo.JDOStatement;
import net.Server;

public class QuestMgr {

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
				loadQuestObjectiveStatement = Server.getJDO().prepare("SELECT `index`, `type`, `amount`, `object_id`FROM `quest_objective` WHERE `quest_id` = ? ORDER BY `index` ASC");
			loadQuestObjectiveStatement.clear();
			loadQuestObjectiveStatement.putInt(quest.getId());
			loadQuestObjectiveStatement.execute();
			while (loadQuestObjectiveStatement.fetch()) {
				byte index = loadQuestObjectiveStatement.getByte();
				byte tmpType = loadQuestObjectiveStatement.getByte();
				short amount = loadQuestObjectiveStatement.getShort();
				int objectId = loadQuestObjectiveStatement.getInt();
				if (tmpType < 0 || tmpType >= QuestObjectiveType.values().length) {
					System.out.println("Invalid type for quest: "+quest.getId()+", objective: "+index+", type: "+tmpType);
					continue;
				}
				QuestObjectiveType type = QuestObjectiveType.values()[tmpType];
				quest.addObjective(new QuestObjective(index, objectId, amount, type, index));
			}
				
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void addQuest(int id, short requiredLevel, String title, String description) {
		Quest quest = new Quest(id, requiredLevel, title, description);
		questMap.put(id, quest);
		loadQuestObjective(quest);
	}
	
	public static Quest getQuest(int id) {
		return questMap.get(id);
	}
}

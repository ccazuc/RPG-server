package net.game.quest;

import java.sql.SQLException;
import java.util.HashMap;

import jdo.JDOStatement;
import net.Server;

public class QuestManager {

	private final static HashMap<Integer, Quest> questMap = new HashMap<Integer, Quest>();
	private static JDOStatement loadQuestStatement;
	private static JDOStatement loadQuestObjectiveStatement;
	
	public static void loadQuest() {
		try {
			if (loadQuestStatement == null)
				loadQuestStatement = Server.getJDO().prepare("SELECT `id`, `required_level` FROM `quest`");
			loadQuestStatement.clear();
			loadQuestStatement.execute();
			while (loadQuestStatement.fetch()) {
				int id = loadQuestStatement.getInt();
				short requiredLevel = loadQuestStatement.getShort();
				addQuest(id, requiredLevel);
			}
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadQuestObjective(Quest quest) {
		try {
			if (loadQuestObjectiveStatement == null)
				loadQuestObjectiveStatement = Server.getJDO().prepare("SELECT `index`, `type`, `amount`, `data1`, `id` FROM `quest_objective` WHERE `quest_id` = ? ORDER BY `index` ASC");
			loadQuestObjectiveStatement.clear();
			loadQuestObjectiveStatement.putInt(quest.getId());
			loadQuestObjectiveStatement.execute();
			while (loadQuestObjectiveStatement.fetch()) {
				byte index = loadQuestObjectiveStatement.getByte();
				byte type = loadQuestObjectiveStatement.getByte();
				short amount = loadQuestObjectiveStatement.getShort();
				int data = loadQuestObjectiveStatement.getInt();
				int id = loadQuestObjectiveStatement.getInt();
				
			}
				
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void addQuest(int id, short requiredLevel) {
		questMap.put(id, new Quest(id, requiredLevel));
		loadQuestObjective(questMap.get(id));
	}
	
	public static Quest getQuest(int id) {
		return questMap.get(id);
	}
}

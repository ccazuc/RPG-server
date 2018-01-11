package net.game.manager;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;

import jdo.JDOStatement;
import net.Server;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;
import net.thread.sql.SQLRequestPriority;

public class IgnoreMgr {

	public final static String ignoreMessage = " ignores your messages.";
	private static JDOStatement loadIgnoreList;
	private final static HashMap<Integer, HashSet<Integer>> ignoreMap = new HashMap<Integer, HashSet<Integer>>();
	private final static SQLRequest addIgnoreInDB = new SQLRequest("INSERT INTO social_ignore (character_id, ignore_id) VALUES (?, ?)", "Add ignore", SQLRequestPriority.LOW) {
		
		@Override
		public void gatherData() throws SQLException {
			SQLDatas datas = this.datasList.get(0);
			this.statement.putInt((int)datas.getNextObject());
			this.statement.putInt((int)datas.getNextObject());
		}
	};
	private final static SQLRequest removeIgnoreFromDB = new SQLRequest("DELETE FROM social_ignore WHERE character_id = ? AND ignore_id = ?", "Remove ignore", SQLRequestPriority.LOW) {
		
		@Override
		public void gatherData() throws SQLException {
			SQLDatas datas = this.datasList.get(0);
			this.statement.putInt((int)datas.getNextObject());
			this.statement.putInt((int)datas.getNextObject());
		}
	};
	
	public static void loadIgnoreList(int id) {
		try {
			if(loadIgnoreList == null) {
				loadIgnoreList = Server.getAsyncHighPriorityJDO().prepare("SELECT ignore_id FROM social_ignore WHERE character_id = ?");
			}
			loadIgnoreList.clear();
			loadIgnoreList.putInt(id);
			loadIgnoreList.execute();
			HashSet<Integer> list = null;
			boolean hasData = false;
			while(loadIgnoreList.fetch()) {
				list = new HashSet<Integer>();
				list.add(loadIgnoreList.getInt());
				hasData = true;
			}
			if(hasData) {
				ignoreMap.put(id, list);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();;
		}
	}

	public static boolean isIgnored(int player_id, int ignore_id) {
		HashSet<Integer> list = ignoreMap.get(player_id);
		if (list == null)
			return (false);
		return (list.contains(ignore_id));
	}
	
	public static void addIgnore(int player_id, int ignore_id) {
		if(ignoreMap.containsKey(player_id)) {
			ignoreMap.get(player_id).add(ignore_id);
		}
		else {
			ignoreMap.put(player_id, new HashSet<Integer>());
			ignoreMap.get(player_id).add(ignore_id);
		}
		addIgnoreInDB.addDatas(new SQLDatas(player_id, ignore_id));
		Server.executeSQLRequest(addIgnoreInDB);
	}
	
	public static void removeIgnore(int player_id, int ignore_id) {
		HashSet<Integer> ignoreList = ignoreMap.get(player_id);
		if (ignoreList == null)
			return;
		if (ignoreList.remove(ignore_id))
		{
			removeIgnoreFromDB.addDatas(new SQLDatas(player_id, ignore_id));
			Server.executeSQLRequest(removeIgnoreFromDB);
		}
	}
	
	public static HashSet<Integer> getIgnoreList(int id) {
		return ignoreMap.get(id);
	}
	
	public static boolean containsKey(int id) {
		return ignoreMap.containsKey(id);
	}
}

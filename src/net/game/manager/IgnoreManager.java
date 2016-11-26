package net.game.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import jdo.JDOStatement;
import net.Server;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;

public class IgnoreManager {

	private static JDOStatement loadIgnoreList;
	private final static HashMap<Integer, ArrayList<Integer>> ignoreMap = new HashMap<Integer, ArrayList<Integer>>();
	private final static SQLRequest addIgnoreInDB = new SQLRequest("INSERT INTO social_ignore (character_id, ignore_id) VALUES (?, ?)") {
		
		@Override
		public void gatherData() {
			try {
				SQLDatas datas = this.datasList.get(0);
				this.statement.clear();
				this.statement.putInt(datas.getIValue1());
				this.statement.putInt(datas.getIValue2());
				this.statement.execute();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
	private final static SQLRequest removeIgnoreFromDB = new SQLRequest("DELETE FROM social_ignore WHERE character_id = ? AND ignore_id = ?") {
		
		@Override
		public void gatherData() {
			try {
				SQLDatas datas = this.datasList.get(0);
				this.statement.clear();
				this.statement.putInt(datas.getIValue1());
				this.statement.putInt(datas.getIValue2());
				this.statement.execute();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
	
	public static void loadIgnoreList(int id) {
		try {
			if(loadIgnoreList == null) {
				loadIgnoreList = Server.getJDO().prepare("SELECT ignore_id FROM social_ignore WHERE character_id = ?");
			}
			loadIgnoreList.clear();
			loadIgnoreList.putInt(id);
			loadIgnoreList.execute();
			ArrayList<Integer> list = null;
			boolean hasData = false;
			while(loadIgnoreList.fetch()) {
				list = new ArrayList<Integer>();
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
		if(ignoreMap.containsKey(player_id)) {
			int i = 0;
			int length = ignoreMap.get(player_id).size();
			ArrayList<Integer> list = ignoreMap.get(player_id);
			while(i < length) {
				if(list.get(i) == ignore_id) {
					return true;
				}
				i++;
			}
		}
		return false;
	}
	
	public static void addIgnore(int player_id, int ignore_id) {
		if(ignoreMap.containsKey(player_id)) {
			ignoreMap.get(player_id).add(ignore_id);
		}
		else {
			ignoreMap.put(player_id, new ArrayList<Integer>());
			ignoreMap.get(player_id).add(ignore_id);
		}
		addIgnoreInDB.addDatas(new SQLDatas(player_id, ignore_id));
		Server.addNewRequest(addIgnoreInDB);
	}
	
	public static void removeIgnore(int player_id, int ignore_id) {
		if(containsKey(player_id)) {
			int i = 0;
			ArrayList<Integer> ignoreList = ignoreMap.get(player_id);
			int length = ignoreList.size();
			while(i < length) {
				if(ignoreList.get(i) == ignore_id) {
					ignoreList.remove(i);
					removeIgnoreFromDB.addDatas(new SQLDatas(player_id, ignore_id));
					Server.addNewRequest(removeIgnoreFromDB);
					return;
				}
				i++;
			}
		}
	}
	
	public static ArrayList<Integer> getIgnoreList(int id) {
		return ignoreMap.get(id);
	}
	
	public static boolean containsKey(int id) {
		return ignoreMap.containsKey(id);
	}
}

package net.game.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import net.Server;
import net.game.unit.Player;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;
import net.thread.sql.SQLRequestPriority;

public class FriendMgr {

	private static HashMap<Integer, ArrayList<Integer>> friendMap = new HashMap<Integer, ArrayList<Integer>>();
	private final static SQLRequest addFriendInDB = new SQLRequest("INSERT INTO social_friend (character_id, friend_id) VALUES (?, ?)", "Add friend", SQLRequestPriority.LOW) {
		
		@Override
		public void gatherData() throws SQLException {
			SQLDatas datas = this.datasList.get(0);
			this.statement.putInt(datas.getIValue1());
			this.statement.putInt(datas.getIValue2());
		}
	};
	private final static SQLRequest removeFriendFromDB = new SQLRequest("DELETE FROM social_friend WHERE character_id = ? AND friend_id = ?", "Remove friend", SQLRequestPriority.LOW) {
		
		@Override
		public void gatherData() throws SQLException {
			SQLDatas datas = this.datasList.get(0);
			this.statement.putInt(datas.getIValue1());
			this.statement.putInt(datas.getIValue2());
		}
	};
	
	public static void removeValueToFriendMapList(int id, Player player) {
		if(friendMap.containsKey(id)) {
			friendMap.get(id).remove(player);
		}
	}
	
	public static boolean containsKey(int id) {
		return friendMap.containsKey(id);
	}
	
	public static HashMap<Integer, ArrayList<Integer>> getFriendMaps() {
		return friendMap;
	}
	
	public static ArrayList<Integer> getFriendList(int id) {
		return friendMap.get(id);
	}
	
	public static void removeList(int id) {
		friendMap.remove(id);
	}
	
	public static void addList(int id, ArrayList<Integer> list) {
		friendMap.put(id, list);
	}
	
	public static void addFriend(int character_id, int friend_id) {
		friendMap.get(character_id).add(friend_id);
	}
	
	public static void addFriendInDB(int character_id, int friend_id) {
		addFriendInDB.addDatas(new SQLDatas(character_id, friend_id));
		Server.executeSQLRequest(addFriendInDB);
	}
	
	public static void removeFriendFromDB(int character_id, int friend_id) {
		removeFriendFromDB.addDatas(new SQLDatas(character_id, friend_id));
		Server.executeSQLRequest(removeFriendFromDB);
	}
}

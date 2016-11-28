package net.game.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import net.Server;
import net.game.Player;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;

public class FriendManager {

	private static HashMap<Integer, ArrayList<Integer>> friendMap = new HashMap<Integer, ArrayList<Integer>>();
	private final static SQLRequest addFriendInDB = new SQLRequest("INSERT INTO social_friend (character_id, friend_id) VALUES (?, ?)", "Add friend") {
		
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
	private final static SQLRequest removeFriendFromDB = new SQLRequest("DELETE FROM social_friend WHERE character_id = ? AND friend_id = ?", "Remove friend") {
		
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
	
	public static void removeValueToFriendMapList(int id, Player player) {
		if(friendMap.containsKey(id)) {
			friendMap.get(id).remove(player);
		}
	}
	
	public static boolean containsKey(int id) {
		return friendMap.containsKey(id);
	}
	
	public static HashMap<Integer, ArrayList<Integer>> getFriendMap() {
		return friendMap;
	}
	
	public static void addFriendInDB(int character_id, int friend_id) {
		addFriendInDB.addDatas(new SQLDatas(character_id, friend_id));
		Server.addNewRequest(addFriendInDB);
	}
	
	public static void removeFriendFromDB(int character_id, int friend_id) {
		removeFriendFromDB.addDatas(new SQLDatas(character_id, friend_id));
		Server.addNewRequest(removeFriendFromDB);
	}
}

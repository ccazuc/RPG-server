package net.game.manager;

import java.sql.SQLException;
import java.util.ArrayList;

import jdo.JDOStatement;
import net.Server;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;
import net.thread.sql.SQLRequestPriority;

public class AccountMgr {

	private static JDOStatement loadAccountIDFromName;
	private static JDOStatement loadAccountIDFromCharacterID;
	private static JDOStatement loadAccountIDAndNameFromNamePattern;
	private final static SQLRequest updateAccountRank = new SQLRequest("UPDATE account SET rank = ? WHERE id = ?", "Update account rank", SQLRequestPriority.HIGH) {
		
		@Override
		public void gatherData() throws SQLException {
			this.statement.putInt((int)getNextObject());
			this.statement.putByte((byte)getNextObject());
		}
	};
	
	public static void updateAccountRank(int account_id, int rank) {
		updateAccountRank.addDatas(new SQLDatas(account_id, rank));
		Server.executeSQLRequest(updateAccountRank);
	}
	
	public static int loadAccountIDFromName(String accountName) {
		try {
			if(loadAccountIDFromName == null) {
				loadAccountIDFromName = Server.getJDO().prepare("SELECT id FROM account WHERE name = ?");
			}
			loadAccountIDFromName.clear();
			loadAccountIDFromName.putString(accountName);
			loadAccountIDFromName.execute();
			if(loadAccountIDFromName.fetch()) {
				return loadAccountIDFromName.getInt();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static boolean accountExists(int accountId)
	{
		return (loadAccountNameFromID(accountId) == null);
	}
	
	public static boolean checkAccountName(String name)
	{
		return (true);
	}
	
	public static String loadAccountNameFromID(int accountID) {
		try {
			if(loadAccountIDFromName == null) {
				loadAccountIDFromName = Server.getJDO().prepare("SELECT name FROM account WHERE id = ?");
			}
			loadAccountIDFromName.clear();
			loadAccountIDFromName.putInt(accountID);
			loadAccountIDFromName.execute();
			if(loadAccountIDFromName.fetch()) {
				return loadAccountIDFromName.getString();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<SQLDatas> loadAccountIDAndNameFromNamePattern(String pattern) { //TODO find why CONTAINS doesn't work
		try {
			if(loadAccountIDAndNameFromNamePattern == null) {
				loadAccountIDAndNameFromNamePattern = Server.getJDO().prepare("SELECT id, name FROM account WHERE CONTAINS(name, ?)");
			}
			ArrayList<SQLDatas> list = null;
			loadAccountIDAndNameFromNamePattern.clear();
			loadAccountIDAndNameFromNamePattern.putString(pattern);
			loadAccountIDAndNameFromNamePattern.execute();
			boolean init = false;
			while(loadAccountIDAndNameFromNamePattern.fetch()) {
				if(!init) {
					 list = new ArrayList<SQLDatas>();
					 init = true;
				}
				System.out.println("FETCH");
				list.add(new SQLDatas(loadAccountIDAndNameFromNamePattern.getInt(), loadAccountIDAndNameFromNamePattern.getString()));
			}
			return list;
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static int loadAccountIDFromCharacterID(int characterID) {
		try {
			if(loadAccountIDFromCharacterID == null) {
				loadAccountIDFromCharacterID = Server.getJDO().prepare("SELECT account_id FROM `character` WHERE character_id = ?");
			}
			loadAccountIDFromCharacterID.clear();
			loadAccountIDFromCharacterID.putInt(characterID);
			loadAccountIDFromCharacterID.execute();
			if(loadAccountIDFromCharacterID.fetch()) {
				return loadAccountIDFromCharacterID.getInt();
			}
		}
		catch(SQLException e)  {
			e.printStackTrace();
		}
		return -1;
	}
}

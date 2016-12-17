package net.game.manager;

import java.sql.SQLException;
import java.util.ArrayList;

import jdo.JDOStatement;
import net.Server;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;

public class AccountMgr {

	private static JDOStatement loadAccountIDFromName;
	private static JDOStatement loadAccountIDAndNameFromNamePattern;
	private final static SQLRequest updateAccountRank = new SQLRequest("UPDATE account SET rank = ? WHERE id = ?", "Update account rank") {
		
		@Override
		public void gatherData() {
			try {
				this.statement.clear();
				SQLDatas datas = this.datasList.get(0);
				this.statement.putInt(datas.getIValue1());
				this.statement.putByte(datas.getBValue1());
				this.statement.execute();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
	
	public static void updateAccountRank(int account_id, int rank) {
		updateAccountRank.addDatas(new SQLDatas(account_id, rank));
		Server.addNewSQLRequest(updateAccountRank);
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
}

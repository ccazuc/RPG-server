package net.game.manager;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Server;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;

public class AccountManager {

	private static JDOStatement loadAccountIDFromName;
	private final static SQLRequest banAccount = new SQLRequest("INSERT INTO `ban_account` (id, ban_date, unban_date, banned_by, ban_reason) VALUES (?, ?, ?, ?, ?)", "Ban account") {
		
		@Override
		public void gatherData() {
			try {
				this.statement.clear();
				SQLDatas datas = this.datasList.get(0);
				this.statement.putInt(datas.getIValue1());
				this.statement.putLong(datas.getLValue1());
				this.statement.putLong(datas.getLValue2());
				this.statement.putString(datas.getStringValue1());
				this.statement.putString(datas.getStringValue2());
				this.statement.execute();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	};
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
	
	public static void banAccount(int ban_id, long ban_date, long unban_date, String banned_by, String ban_reason) {
		banAccount.addDatas(new SQLDatas(ban_id, ban_date, unban_date, banned_by, ban_reason));
		Server.addNewSQLRequest(banAccount);
	}
	
	public static void updateAccountRank(int account_id, int rank) {
		updateAccountRank.addDatas(new SQLDatas(account_id, rank));
		Server.addNewSQLRequest(updateAccountRank);
	}
	
	public static int loadAccountIDFromName(String accountName) {
		try {
			if(loadAccountIDFromName == null) {
				loadAccountIDFromName = Server.getJDO().prepare("SELECT name FROM account WHERE id = ?");
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
}

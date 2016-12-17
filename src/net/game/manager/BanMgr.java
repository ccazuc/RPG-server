package net.game.manager;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Server;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;

public class BanMgr {

	private static JDOStatement selectAllBanAccount;
	private static JDOStatement removeExpiredBanAccount;
	private static JDOStatement selectAllBanCharacter;
	private static JDOStatement removeExpiredBanCharacter;
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
	private final static SQLRequest banCharacter = new SQLRequest("INSERT INTO `character_banned (character_id, ban_date, unban_date, banned_by, ban_reason) VALUES (?, ?, ?, ?, ?)", "Ban character") {
		
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
	
	public static void removeExpiredBanAccount() {
		try {
			long timer = System.currentTimeMillis();
			if(removeExpiredBanAccount == null) {
				removeExpiredBanAccount = Server.getJDO().prepare("DELETE FROM account_banned WHERE account_id = ?");
				selectAllBanAccount = Server.getJDO().prepare("SELECT account_id FROM account_banned WHERE unban_date >= 0 AND unban_date <= ?");
			}
			selectAllBanAccount.clear();
			selectAllBanAccount.putLong(timer);
			selectAllBanAccount.execute();
			while(selectAllBanAccount.fetch()) {
				int accountId = selectAllBanAccount.getInt();
				removeExpiredBanAccount.clear();
				removeExpiredBanAccount.putInt(accountId);
				removeExpiredBanAccount.execute();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();;
		}
	}
	
	public static void removeExpiredBanCharacter() {
		try {
			long timer = System.currentTimeMillis();
			if(removeExpiredBanCharacter == null) {
				removeExpiredBanCharacter = Server.getJDO().prepare("DELETE FROM characted_banned WHERE character_id = ?");
				selectAllBanCharacter = Server.getJDO().prepare("SELECT character_id FROM character_banned WHERE unban_date >= 0 AND unban_date <= ?");
			}
			selectAllBanCharacter.clear();
			selectAllBanCharacter.putLong(timer);
			selectAllBanCharacter.execute();
			while(selectAllBanCharacter.fetch()) {
				int accountId = selectAllBanCharacter.getInt();
				removeExpiredBanCharacter.clear();
				removeExpiredBanCharacter.putInt(accountId);
				removeExpiredBanCharacter.execute();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();;
		}
	}
	
	public static void banAccount(int ban_id, long ban_date, long unban_date, String banned_by, String ban_reason) {
		banAccount.addDatas(new SQLDatas(ban_id, ban_date, unban_date, banned_by, ban_reason));
		Server.addNewSQLRequest(banAccount);
	}
	
	public static void banCharacter(int character_id, long ban_date, long unban_date, String banned_by, String ban_reason) {
		banCharacter.addDatas(new SQLDatas(character_id, ban_date, unban_date, banned_by, ban_reason));
		Server.addNewSQLRequest(banCharacter);
	}
}

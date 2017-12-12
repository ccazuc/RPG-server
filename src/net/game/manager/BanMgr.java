package net.game.manager;

import java.sql.SQLException;
import java.util.ArrayList;

import jdo.JDOStatement;
import net.Server;
import net.thread.log.LogRunnable;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;
import net.thread.sql.SQLRequestPriority;

public class BanMgr {

	private static JDOStatement isCharacterBannedIDHighAsync;
	private static JDOStatement getBanListAccountIDLowAsync;
	private static JDOStatement getBanInfoIPAdressLowAsync;
	private static JDOStatement getBanInfoAccountIDLowAsync;
	private static JDOStatement getBanInfoCharacterIDLowAsync;
	private static JDOStatement removeExpiredBanAccount;
	private static JDOStatement removeExpiredBanCharacter;
	private static JDOStatement removeExpiredBanIP;
	private final static SQLRequest banAccount = new SQLRequest("INSERT INTO ban_account (id, ban_date, unban_date, banned_by, ban_reason) VALUES (?, ?, ?, ?, ?)", "Ban account", SQLRequestPriority.HIGH) {
		
		@Override
		public void gatherData() throws SQLException {
			SQLDatas datas = this.datasList.get(0);
			this.statement.putInt(datas.getIValue1());
			this.statement.putLong(datas.getLValue1());
			this.statement.putLong(datas.getLValue2());
			this.statement.putString(datas.getStringValue1());
			this.statement.putString(datas.getStringValue2());
		}
	};
	private final static SQLRequest banCharacter = new SQLRequest("INSERT INTO character_banned (character_id, ban_date, unban_date, banned_by, ban_reason) VALUES (?, ?, ?, ?, ?)", "Ban character", SQLRequestPriority.HIGH) {
		
		@Override
		public void gatherData() throws SQLException {
			SQLDatas datas = this.datasList.get(0);
			this.statement.putInt(datas.getIValue1());
			this.statement.putLong(datas.getLValue1());
			this.statement.putLong(datas.getLValue2());
			this.statement.putString(datas.getStringValue1());
			this.statement.putString(datas.getStringValue2());
		}
	};
	private final static SQLRequest banIPAdress = new SQLRequest("INSERT INTO ip_banned (ip_adress, ban_date, unban_date, banned_by, ban_reason) VALUES (?, ?, ?, ?, ?)", "Ban IP adress", SQLRequestPriority.HIGH) {
		
		@Override
		public void gatherData() throws SQLException {
			SQLDatas datas = this.datasList.get(0);
			this.statement.putString(datas.getStringValue1());
			this.statement.putLong(datas.getLValue1());
			this.statement.putLong(datas.getLValue2());
			this.statement.putString(datas.getStringValue2());
			this.statement.putString(datas.getStringValue3());
		}
	};
	
	public static void removeExpiredBanAccount() {
		try {
			long timer = System.currentTimeMillis();
			if(removeExpiredBanAccount == null) {
				removeExpiredBanAccount = Server.getJDO().prepare("DELETE FROM account_banned WHERE unban_date > 0 AND unban_date <= ?");
			}
			removeExpiredBanAccount.clear();
			removeExpiredBanAccount.putLong(timer);
			removeExpiredBanAccount.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();;
		}
	}
	
	public static void removeExpiredBanIP() {
		try {
			long timer = System.currentTimeMillis();
			if(removeExpiredBanIP == null) {
				removeExpiredBanIP = Server.getJDO().prepare("DELETE FROM ip_banned WHERE unban_date > 0 AND unban_date <= ?");
			}
			removeExpiredBanIP.clear();
			removeExpiredBanIP.putLong(timer);
			removeExpiredBanIP.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();;
		}
	}
	
	public static SQLDatas getBanInfoAccountIDLowAsync(int accountID) {
		try {
			if(getBanInfoAccountIDLowAsync == null) {
				getBanInfoAccountIDLowAsync = Server.getAsyncLowPriorityJDO().prepare("SELECT ban_date, unban_date, banned_by, ban_reason FROM account_banned WHERE account_id = ?");
			}
			getBanInfoAccountIDLowAsync.clear();
			getBanInfoAccountIDLowAsync.putInt(accountID);
			getBanInfoAccountIDLowAsync.execute();
			if(getBanInfoAccountIDLowAsync.fetch()) {
				long ban_date = getBanInfoAccountIDLowAsync.getLong();
				long unban_date = getBanInfoAccountIDLowAsync.getLong();
				String banned_by = getBanInfoAccountIDLowAsync.getString();
				String ban_reason = getBanInfoAccountIDLowAsync.getString();
				return new SQLDatas(ban_date, unban_date, banned_by, ban_reason);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static SQLDatas getBanInfoCharacterIDLowAsync(int characterID) {
		try {
			if(getBanInfoCharacterIDLowAsync == null) {
				getBanInfoCharacterIDLowAsync = Server.getAsyncLowPriorityJDO().prepare("SELECT ban_date, unban_date, banned_by, ban_reason FROM character_banned WHERE character_id = ?");
			}
			getBanInfoCharacterIDLowAsync.clear();
			getBanInfoCharacterIDLowAsync.putInt(characterID);
			getBanInfoCharacterIDLowAsync.execute();
			if(getBanInfoCharacterIDLowAsync.fetch()) {
				long ban_date = getBanInfoCharacterIDLowAsync.getLong();
				long unban_date = getBanInfoCharacterIDLowAsync.getLong();
				String banned_by = getBanInfoCharacterIDLowAsync.getString();
				String ban_reason = getBanInfoCharacterIDLowAsync.getString();
				return new SQLDatas(ban_date, unban_date, banned_by, ban_reason);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static SQLDatas getBanInfoIPAdressLowAsync(String IPAdress) {
		try {
			if(getBanInfoIPAdressLowAsync == null) {
				getBanInfoIPAdressLowAsync = Server.getAsyncLowPriorityJDO().prepare("SELECT ban_date, unban_date, banned_by, ban_reason FROM ip_banned WHERE ip_adress = ?");
			}
			getBanInfoIPAdressLowAsync.clear();
			getBanInfoIPAdressLowAsync.putString(IPAdress);
			getBanInfoIPAdressLowAsync.execute();
			if(getBanInfoIPAdressLowAsync.fetch()) {
				long ban_date = getBanInfoIPAdressLowAsync.getLong();
				long unban_date = getBanInfoIPAdressLowAsync.getLong();
				String banned_by = getBanInfoIPAdressLowAsync.getString();
				String ban_reason = getBanInfoIPAdressLowAsync.getString();
				return new SQLDatas(ban_date, unban_date, banned_by, ban_reason);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<Integer> getBanListAccountIDLowAsync() {
		try {
			if(getBanListAccountIDLowAsync == null) {
				getBanListAccountIDLowAsync = Server.getAsyncLowPriorityJDO().prepare("SELECT account_id FROM account_banned");
			}
			getBanListAccountIDLowAsync.clear();
			getBanListAccountIDLowAsync.execute();
			ArrayList<Integer> list = null;
			boolean init = false;
			while(getBanListAccountIDLowAsync.fetch()) {
				if(!init) {
					list = new ArrayList<Integer>();
					init = true;
				}
				list.add(getBanListAccountIDLowAsync.getInt());
			}
			return list;
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void removeExpiredBanCharacter() {
		try {
			long timer = System.currentTimeMillis();
			if(removeExpiredBanCharacter == null) {
				removeExpiredBanCharacter = Server.getJDO().prepare("DELETE FROM character_banned WHERE unban_date >= 0 AND unban_date <= ?");
			}
			removeExpiredBanCharacter.clear();
			removeExpiredBanCharacter.putLong(timer);
			removeExpiredBanCharacter.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();;
		}
	}
	
	public static boolean isCharacterBannedHighAsync(int characterID) {
		try {
			if(isCharacterBannedIDHighAsync == null) {
				isCharacterBannedIDHighAsync = Server.getAsyncHighPriorityJDO().prepare("SELECT COUNT(character_id) FROM character_banned WHERE character_id = ?");
			}
			isCharacterBannedIDHighAsync.clear();
			isCharacterBannedIDHighAsync.putInt(characterID);
			isCharacterBannedIDHighAsync.execute();
			if(isCharacterBannedIDHighAsync.fetch()) {
				return isCharacterBannedIDHighAsync.getInt() > 0;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void banAccount(int account_id, long ban_date, long unban_date, String banned_by, String ban_reason) {
		banAccount.addDatas(new SQLDatas(account_id, ban_date, unban_date, banned_by, ban_reason));
		Server.executeSQLRequest(banAccount);
	}
	
	public static void banCharacter(int character_id, long ban_date, long unban_date, String banned_by, String ban_reason) {
		banCharacter.addDatas(new SQLDatas(character_id, ban_date, unban_date, banned_by, ban_reason));
		Server.executeSQLRequest(banCharacter);
	}

	public static void banIPAdress(String ip_adress, long ban_date, long unban_date, String banned_by, String ban_reason) {
		banIPAdress.addDatas(new SQLDatas(ban_date, unban_date, ip_adress, banned_by, ban_reason));
		Server.executeSQLRequest(banIPAdress);
	}
}

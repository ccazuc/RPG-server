package net.game.manager;

import java.sql.SQLException;
import java.util.ArrayList;

import jdo.JDOStatement;
import net.Server;
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
	private static JDOStatement isAccountBannedStatement;
	private static JDOStatement isCharacterBannedStatement;
	private final static SQLRequest banAccount = new SQLRequest("INSERT INTO account_banned (account_id, ban_date, unban_date, banned_by, ban_reason) VALUES (?, ?, ?, ?, ?)", "Ban account", SQLRequestPriority.LOW) {
		
		@Override
		public void gatherData() throws SQLException {
			this.statement.putInt((int)getNextObject());
			this.statement.putLong((long)getNextObject());
			this.statement.putLong((long)getNextObject());
			this.statement.putString((String)getNextObject());
			this.statement.putString((String)getNextObject());
		}
	};
	private final static SQLRequest unbanAccount = new SQLRequest("DELETE FROM `account_banned` WHERE `account_id` = ?", "Unban account", SQLRequestPriority.LOW) {
		
		@Override
		public void gatherData() throws SQLException {
			this.statement.putInt((int)getNextObject());
		}
	};
	private final static SQLRequest unbanCharacter = new SQLRequest("DELETE FROM `character_banned` WHERE `character_id` = ?", "Unban character", SQLRequestPriority.LOW) {
		
		@Override
		public void gatherData() throws SQLException {
			this.statement.putInt((int)getNextObject());
		}
	};
	private final static SQLRequest banCharacter = new SQLRequest("INSERT INTO character_banned (character_id, ban_date, unban_date, banned_by, ban_reason) VALUES (?, ?, ?, ?, ?)", "Ban character", SQLRequestPriority.LOW) {
		
		@Override
		public void gatherData() throws SQLException {
			this.statement.putInt((int)getNextObject());
			this.statement.putLong((long)getNextObject());
			this.statement.putLong((long)getNextObject());
			this.statement.putString((String)getNextObject());
			this.statement.putString((String)getNextObject());
		}
	};
	private final static SQLRequest banIPAdress = new SQLRequest("INSERT INTO ip_banned (ip_adress, ban_date, unban_date, banned_by, ban_reason) VALUES (?, ?, ?, ?, ?)", "Ban IP adress", SQLRequestPriority.LOW) {
		
		@Override
		public void gatherData() throws SQLException {
			this.statement.putString((String)getNextObject());
			this.statement.putLong((long)getNextObject());
			this.statement.putLong((long)getNextObject());
			this.statement.putString((String)getNextObject());
			this.statement.putString((String)getNextObject());
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
	
	public static boolean isAccountBanned(int accountId)
	{
		try
		{
			if (isAccountBannedStatement == null)
				isAccountBannedStatement = Server.getJDO().prepare("SELECT COUNT(*) FROM `account_banned` WHERE `account_id` = ?");
			isAccountBannedStatement.clear();
			isAccountBannedStatement.putInt(accountId);
			isAccountBannedStatement.execute();
			if (isAccountBannedStatement.fetch())
				return (isAccountBannedStatement.getInt() > 0);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return (false);
	}
	
	public static boolean isCharacterBanned(int characterId)
	{
		try
		{
			if (isCharacterBannedStatement == null)
				isCharacterBannedStatement = Server.getJDO().prepare("SELECT COUNT(*) FROM `character_banned` WHERE `character_id` = ?");
			isCharacterBannedStatement.clear();
			isCharacterBannedStatement.putInt(characterId);
			isCharacterBannedStatement.execute();
			if (isCharacterBannedStatement.fetch())
				return (isCharacterBannedStatement.getInt() > 0);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return (false);
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
				removeExpiredBanCharacter = Server.getJDO().prepare("DELETE FROM character_banned WHERE unban_date > 0 AND unban_date <= ?");
			}
			removeExpiredBanCharacter.clear();
			removeExpiredBanCharacter.putLong(timer);
			removeExpiredBanCharacter.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();;
		}
	}
	
	public static long isCharacterBannedHighAsync(int characterID) {
		try {
			if(isCharacterBannedIDHighAsync == null) {
				isCharacterBannedIDHighAsync = Server.getAsyncHighPriorityJDO().prepare("SELECT `unban_date` FROM character_banned WHERE character_id = ?");
			}
			isCharacterBannedIDHighAsync.clear();
			isCharacterBannedIDHighAsync.putInt(characterID);
			isCharacterBannedIDHighAsync.execute();
			if(isCharacterBannedIDHighAsync.fetch()) {
				return (isCharacterBannedIDHighAsync.getLong());
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return (-1);
	}
	
	public static void banAccount(int account_id, long ban_date, long unban_date, String banned_by, String ban_reason) {
		banAccount.addDatas(new SQLDatas(account_id, ban_date, unban_date, banned_by, ban_reason));
		Server.executeSQLRequest(banAccount);
	}
	
	public static void unbanAccount(int accountId) {
		unbanAccount.addDatas(new SQLDatas(accountId));
		Server.executeSQLRequest(unbanAccount);
	}
	
	public static void unbanCharacter(int characterId) {
		unbanCharacter.addDatas(new SQLDatas(characterId));
		Server.executeSQLRequest(unbanCharacter);
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

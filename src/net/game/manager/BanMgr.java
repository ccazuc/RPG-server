package net.game.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import jdo.JDOStatement;
import net.Server;
import net.command.chat.CommandSendMessage;
import net.command.chat.MessageType;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;
import net.thread.sql.SQLRequestPriority;

public class BanMgr {

	private static JDOStatement getBanListAccountIDLowAsync;
	private static JDOStatement getBanInfoIPAdressLowAsync;
	private static JDOStatement getBanInfoAccountIDLowAsync;
	private static JDOStatement getBanInfoCharacterIDLowAsync;
	private static JDOStatement selectAllBanAccount;
	private static JDOStatement removeExpiredBanAccount;
	private static JDOStatement selectAllBanCharacter;
	private static JDOStatement removeExpiredBanCharacter;
	private final static SQLRequest banAccount = new SQLRequest("INSERT INTO ban_account (id, ban_date, unban_date, banned_by, ban_reason) VALUES (?, ?, ?, ?, ?)", "Ban account", SQLRequestPriority.HIGH) {
		
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
	private final static SQLRequest banCharacter = new SQLRequest("INSERT INTO character_banned (character_id, ban_date, unban_date, banned_by, ban_reason) VALUES (?, ?, ?, ?, ?)", "Ban character", SQLRequestPriority.HIGH) {
		
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
	private final static SQLRequest banIPAdress = new SQLRequest("INSERT INTO ip_banned (ip_adress, ban_date, unban_date, banned_by, ban_reason) VALUES (?, ?, ?, ?, ?)", "Ban IP adress", SQLRequestPriority.HIGH) {
		
		@Override
		public void gatherData() {
			try {
				this.statement.clear();
				SQLDatas datas = this.datasList.get(0);
				this.statement.putString(datas.getStringValue1());
				this.statement.putLong(datas.getLValue1());
				this.statement.putLong(datas.getLValue2());
				this.statement.putString(datas.getStringValue2());
				this.statement.putString(datas.getStringValue3());
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

package net.game.mail;

import java.sql.SQLException;
import java.util.HashMap;

import jdo.JDOStatement;
import net.Server;
import net.game.unit.Player;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;
import net.thread.sql.SQLRequestPriority;

public class MailMgr {

	private final static HashMap<Long, Mail> mailMap = new HashMap<Long, Mail>();
	private static JDOStatement loadAllMail;
	private final static long MAIL_DURATION = 1500000;
	private final static long CR_DURATION = 150000;
	private static long currentGUID;
	private final static SQLRequest deleteMail = new SQLRequest("DELETE FROM `mail` WHERE `GUID` = ?", "Delete mail", SQLRequestPriority.LOW) {
		
		@Override
		public void gatherData() throws SQLException {
			this.statement.putLong(this.datasList.get(0).getLValue1());
		}
	};
	private final static SQLRequest addMail = new SQLRequest("INSERT INTO `mail` (GUID, author_id, dest_id, title, content, delete_date, gold, is_cr, template) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", "Add mail", SQLRequestPriority.LOW) {
	
		@Override
		public void gatherData() throws SQLException {
			SQLDatas datas = this.datasList.get(0);
			this.statement.putLong(datas.getLValue1());
			this.statement.putInt(datas.getIValue1());
			this.statement.putInt(datas.getIValue2());
			this.statement.putString(datas.getStringValue1());
			this.statement.putString(datas.getStringValue2());
			this.statement.putLong(datas.getLValue2());
			this.statement.putInt(datas.getIValue1());
			this.statement.putBoolean(datas.getBValue1() == 1);
			this.statement.putByte(datas.getBValue2());
		}
	};
	
	public static void loadAllMail() {
		try {
			if (loadAllMail == null)
				loadAllMail = Server.getJDO().prepare("SELECT GUID, author_id, dest_id, title, content, delete_date, gold, is_cr, template FROM mail");
			loadAllMail.clear();
			loadAllMail.execute();
			while (loadAllMail.fetch()) {
				long GUID = loadAllMail.getLong();
				int author_id = loadAllMail.getInt();
				int dest_id = loadAllMail.getInt();
				String title = loadAllMail.getString();
				String content = loadAllMail.getString();
				long delete_date = loadAllMail.getLong();
				int gold = loadAllMail.getInt();
				boolean is_cr = loadAllMail.getBoolean();
				byte template = loadAllMail.getByte();
				mailMap.put(GUID, new Mail(GUID, author_id, dest_id, title, content, delete_date, gold, is_cr, template));
				if (GUID > currentGUID)
					currentGUID = GUID;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteMail(int GUID) {
		deleteMail.addDatas(new SQLDatas(GUID));
		Server.executeSQLRequest(deleteMail);
	}
	
	public static void sendMail(Player sender, int destID, String title, String content, int gold, boolean isCR, byte template) {
		long GUID = generateGUID();
		long deleteDate = isCR ? Server.getLoopTickTimer() + CR_DURATION : Server.getLoopTickTimer() + MAIL_DURATION;
		Mail mail = new Mail(GUID, sender.getUnitID(), destID, title, content, deleteDate, gold, isCR, template);
		mailMap.put(GUID, mail);
		//addMail.addDatas(new SQLDatas());
		Server.executeSQLRequest(addMail);
	}
	
	public static long generateGUID() {
		return ++currentGUID;
	}
}

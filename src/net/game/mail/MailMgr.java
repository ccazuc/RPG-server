package net.game.mail;

import java.sql.SQLException;
import java.util.HashMap;

import jdo.JDOStatement;
import net.Server;
import net.game.unit.Player;

public class MailMgr {

	private final static HashMap<Long, Mail> mailMap = new HashMap<Long, Mail>();
	private static JDOStatement loadAllMail;
	private final static long MAIL_DURATION = 1500000;
	private final static long CR_DURATION = 150000;
	private static long currentGUID;
	
	public static void loadAllMail() {
		try {
			if (loadAllMail == null)
				loadAllMail = Server.getJDO().prepare("SELECT GUID, author_id, dest_id, title, content, delete_date, gold, is_cr, template FROM mail");
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
			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteMail(int GUID) {
		//TODO: delete mail from DB
	}
	
	public static void sendMail(Player sender, int destID, String title, String content, int gold, boolean isCR, byte template) {
		long GUID = generateGUID();
		long deleteDate = isCR ? Server.getLoopTickTimer() + CR_DURATION : Server.getLoopTickTimer() + MAIL_DURATION;
		Mail mail = new Mail(GUID, sender.getUnitID(), destID, title, content, deleteDate, gold, isCR, template);
		mailMap.put(GUID, mail);
		//TODO: add mail to DB
	}
	
	public static long generateGUID() {
		return ++currentGUID;
	}
}

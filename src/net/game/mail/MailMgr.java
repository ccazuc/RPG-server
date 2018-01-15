package net.game.mail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import jdo.JDOStatement;
import net.Server;
import net.command.player.CommandMail;
import net.game.unit.Player;
import net.thread.sql.SQLDatas;
import net.thread.sql.SQLRequest;
import net.thread.sql.SQLRequestPriority;
import net.utils.Timer;

public class MailMgr {

	private final static HashMap<Integer, ArrayList<Mail>> mailMap = new HashMap<Integer, ArrayList<Mail>>();
	private static JDOStatement loadAllMail;
	private final static long MAIL_DURATION = 31 * 24 * Timer.MS_IN_HOUR;
	private final static long CR_DURATION = 2 * 24 * Timer.MS_IN_HOUR;
	public final static int MAIL_COST = 30;
	private static long currentGUID;
	private final static SQLRequest deleteMail = new SQLRequest("DELETE FROM `mail` WHERE `GUID` = ?", "Delete mail", SQLRequestPriority.LOW) {
		
		@Override
		public void gatherData() throws SQLException {
			this.statement.putLong((long)getNextObject());
		}
	};
	private final static SQLRequest addMail = new SQLRequest("INSERT INTO `mail` (`GUID`, `author_id` , `dest_id`, `title`, `content`, `delete_date`, `gold`, `is_cr`, `read_template`, `read`, `cr_paid`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", "Add mail", SQLRequestPriority.LOW) {
	
		@Override
		public void gatherData() throws SQLException {
			Mail mail = (Mail)getNextObject();
			this.statement.putLong(mail.getGUID());
			this.statement.putInt(mail.getAuthorID());
			this.statement.putInt(mail.getDestID());
			this.statement.putString(mail.getTitle());
			this.statement.putString(mail.getContent());
			this.statement.putLong(mail.getDeleteDate());
			this.statement.putInt(mail.getGold());
			this.statement.putBoolean(mail.getIsCR());
			this.statement.putByte(mail.getTemplate());
			this.statement.putBoolean(mail.getRead());
			this.statement.putBoolean(mail.getCRPaid());
		}
	};
	private final static SQLRequest readMail = new SQLRequest("UPDATE `mail` SET `read` = 1 WHERE `GUID` = ?", "Read mail", SQLRequestPriority.LOW) {
	
		@Override
		public void gatherData() throws SQLException {
			this.statement.putLong((long)getNextObject());
		}
	};
	
	public static void loadAllMail() {
		try {
			if (loadAllMail == null)
				loadAllMail = Server.getJDO().prepare("SELECT `id`, `author_id`, `dest_id`, `title`, `content`, `delete_date`, `gold`, `is_cr`, `read_template`, `cr_paid` FROM mail");
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
				boolean read = loadAllMail.getBoolean();
				boolean crPaid = loadAllMail.getBoolean();
				Mail mail = new Mail(GUID, author_id, dest_id, title, content, delete_date, gold, is_cr, template, read, crPaid);
				if (delete_date <= Server.getLoopTickTimer())
				{
					deleteMail.addDatas(new SQLDatas(GUID));
					Server.executeSQLRequest(deleteMail);
					if (is_cr)
						sendBackCR(mail);
					continue;
				}
				if (!mailMap.containsKey(dest_id))
					mailMap.put(dest_id, new ArrayList<Mail>());
				mailMap.get(dest_id).add(mail);
				if (GUID > currentGUID)
					currentGUID = GUID;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteMail(int targetId, long GUID)
	{
		ArrayList<Mail> list;
		if ((list = mailMap.get(targetId)) == null)
			return;
		boolean found = false;
		int i = -1;
		while (++i < list.size())
			if (list.get(i).getGUID() == GUID)
			{
				if (list.get(i).getIsCR())
					sendBackCR(list.get(i));
				list.remove(i);
				found = true;
				break;
			}
		if (!found)
			return;
		deleteMail.addDatas(new SQLDatas(GUID));
		Server.executeSQLRequest(deleteMail);
	}
	
	public static void sendBackCR(Mail mail)
	{
		Player player = Server.getInGameCharacter(mail.getAuthorID());
		Mail result = new Mail(generateGUID(), -1, mail.getAuthorID(), "Mail", "", Server.getLoopTickTimer() + MAIL_DURATION, 0, false, MailTemplate.CLASSIC.getValue(), false, true);
		if (player != null)
			CommandMail.sendMail(player, result, true);
		addMail.addDatas(new SQLDatas(result));
		Server.executeSQLRequest(addMail);
	}
	
	public static int openMail(int targetId, long GUID)
	{
		ArrayList<Mail> list;
		if ((list = mailMap.get(targetId)) == null)
			return (-1);
		int i = -1;
		Mail mail = null;
		while (++i < list.size())
			if (list.get(i).getGUID() == GUID)
			{
				mail = list.get(i);
				break;
			}
		if (mail == null) {
			return (-1);
		}
		mail.read();
		readMail.addDatas(new SQLDatas(GUID));
		Server.executeSQLRequest(readMail);
		return (0);
	}
	
	public static void sendMail(Player sender, int destID, String title, String content, int gold, boolean isCR, byte template)
	{
		long GUID = generateGUID();
		long deleteDate = isCR ? Server.getLoopTickTimer() + CR_DURATION : Server.getLoopTickTimer() + MAIL_DURATION;
		Mail mail = new Mail(GUID, sender.getUnitID(), sender.getName(), destID, title, content, deleteDate, gold, isCR, template, false);
		if (!mailMap.containsKey(destID))
			mailMap.put(destID, new ArrayList<Mail>());
		mailMap.get(destID).add(mail);
		Player dest = Server.getInGameCharacter(destID);
		if (dest != null)
			CommandMail.sendMail(dest, mail, true);
		addMail.addDatas(new SQLDatas(mail));
		Server.executeSQLRequest(addMail);
	}
	
	public static long generateGUID()
	{
		return (++currentGUID);
	}
	
	public static HashMap<Integer, ArrayList<Mail>> getMailMap()
	{
		return (mailMap);
	}
	
	public static ArrayList<Mail> getMailList(int targetId)
	{
		return (mailMap.get(targetId));
	}
	
	public static void checkExpiredMail()
	{
		Player player = null;
		for (ArrayList<Mail> list : mailMap.values())
		{
			int i = -1;
			while (++i < list.size())
			{
				if (list.get(i).getDeleteDate() <= Server.getLoopTickTimer())
				{
					deleteMail.addDatas(new SQLDatas(list.get(i).getGUID()));
					Server.executeSQLRequest(deleteMail);
					if ((player = Server.getInGameCharacter(list.get(i).getDestID())) != null)
						CommandMail.deleteMail(player, list.get(i));
					if (list.get(i).getIsCR())
						sendBackCR(list.get(i));
					list.remove(i);
				}
			}
		}
	}
}

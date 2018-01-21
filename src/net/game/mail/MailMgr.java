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

public class MailMgr {

	private final static HashMap<Integer, ArrayList<Mail>> mailMap = new HashMap<Integer, ArrayList<Mail>>();
	private static JDOStatement loadAllMail;
	public final static long MAIL_DURATION = 31l * 24 * 60 * 60 * 1000;
	public final static long CR_DURATION = 2 * 24 * 60 * 60 * 1000;
	public final static int MAIL_COST = 30;
	public final static int SUBJECT_MAX_LENGTH = 50;
	private static long currentGUID;
	private final static SQLRequest deleteMail = new SQLRequest("DELETE FROM `mail` WHERE `GUID` = ?", "Delete mail", SQLRequestPriority.LOW) {
		
		@Override
		public void gatherData() throws SQLException {
			this.statement.putLong((long)getNextObject());
		}
	};
	private final static SQLRequest addMail = new SQLRequest("INSERT INTO `mail` (`GUID`, `author_id` , `dest_id`, `title`, `content`, `delete_date`, `gold`, `is_cr`, `read_template`, `flag`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", "Add mail", SQLRequestPriority.LOW) {
	
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
			this.statement.putShort(mail.getFlag());
		}
	};
	private final static SQLRequest readMail = new SQLRequest("UPDATE `mail` SET `flag` = ? WHERE `GUID` = ?", "Read mail", SQLRequestPriority.LOW) {
	
		@Override
		public void gatherData() throws SQLException {
			this.statement.putShort((short)getNextObject());
			this.statement.putLong((long)getNextObject());
		}
	};
	
	public static void loadAllMail() {
		try {
			if (loadAllMail == null)
				loadAllMail = Server.getJDO().prepare("SELECT `guid`, `author_id`, `dest_id`, `title`, `content`, `delete_date`, `gold`, `is_cr`, `read_template`, `flag` FROM mail");
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
				short flag = loadAllMail.getShort();
				Mail mail = new Mail(GUID, author_id, dest_id, title, content, delete_date, gold, is_cr, template, flag);
				if (delete_date <= Server.getLoopTickTimer())
				{
					deleteMail.addDatas(new SQLDatas(GUID));
					Server.executeSQLRequest(deleteMail);
					if (is_cr && !mail.wasAlreadyReturned())
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
				if (list.get(i).getIsCR() && !list.get(i).wasAlreadyReturned())
					sendBackCR(list.get(i));
				list.remove(i);
				found = true;
				break;
			}
		if (!found)
			return;
		deleteMail(GUID);
	}
	
	public static void deleteMail(long GUID)
	{
		deleteMail.addDatas(new SQLDatas(GUID));
		Server.executeSQLRequest(deleteMail);		
	}
	
	public static void sendBackCR(Mail mail)
	{
		Player player = Server.getInGameCharacter(mail.getAuthorID());
		Mail result = new Mail(generateGUID(), -1, mail.getAuthorID(), "Mail", "", Server.getLoopTickTimer() + MAIL_DURATION, 0, false, MailTemplate.CLASSIC.getValue(), MailFlag.MAIL_RETURNED.getValue());
		if (player != null)
			CommandMail.sendMail(player, result, true, true);
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
		if (mail == null)
			return (-1);
		mail.setRead();
		readMail.addDatas(new SQLDatas(mail.getFlag(), GUID));
		Server.executeSQLRequest(readMail);
		return (0);
	}
	
	public static void sendMail(Player sender, int destID, String title, String content, int gold, boolean isCR, byte template)
	{
		long GUID = generateGUID();
		long deleteDate = isCR ? Server.getLoopTickTimer() + CR_DURATION : Server.getLoopTickTimer() + MAIL_DURATION;
		Mail mail = new Mail(GUID, sender.getUnitID(), sender.getName(), destID, title, content, deleteDate, gold, isCR, template, MailFlag.CAN_REPLY.getValue());
		if (!mailMap.containsKey(destID))
			mailMap.put(destID, new ArrayList<Mail>());
		mailMap.get(destID).add(mail);
		Player dest = Server.getInGameCharacter(destID);
		if (dest != null)
			CommandMail.sendMail(dest, mail, true, true);
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
	
	public static Mail getMail(int playerId, long GUID)
	{
		ArrayList<Mail> list;
		if ((list = mailMap.get(playerId)) == null)
			return (null);
		int i = -1;
		while (++i < list.size())
			if (list.get(i).getGUID() == GUID)
				return (list.get(i));
		return (null);
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
					if (list.get(i).getIsCR() && !list.get(i).wasAlreadyReturned())
						sendBackCR(list.get(i));
					list.remove(i);
				}
			}
		}
	}
}

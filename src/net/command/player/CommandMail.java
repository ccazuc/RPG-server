package net.command.player;

import java.util.ArrayList;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.DefaultRedAlert;
import net.game.log.Log;
import net.game.mail.Mail;
import net.game.mail.MailMgr;
import net.game.mail.MailTemplate;
import net.game.manager.CharacterMgr;
import net.game.unit.Player;

public class CommandMail extends Command {

	public CommandMail(String name, boolean debug)
	{
		super(name, debug);
	}
	
	@Override
	public void read(Player player)
	{
		Connection connection = player.getConnection();
		short packetId = connection.readShort();
		if (packetId == PacketID.MAIL_DELETE)
		{
			long GUID = connection.readLong();
			MailMgr.deleteMail(player, GUID);
		}
		else if (packetId == PacketID.MAIL_SEND)
		{
			String destName = connection.readString();
			String title = connection.readString();
			String content = connection.readString();
			int gold = connection.readInt();
			boolean isCr = connection.readBoolean();
			if (player.getGold() < MailMgr.MAIL_COST) {
				//TODO: send red alert not enough money
				return;
			}
			if (title.length() == 0 || title.length() > MailMgr.SUBJECT_MAX_LENGTH)
				return;
			int destID = CharacterMgr.loadCharacterIDFromName(destName);
			if (destID == -1) {
				CommandSendRedAlert.write(player, DefaultRedAlert.CANNOT_FIND_RECIPIENT);
				return;
			}
			player.setGold(player.getGold() - MailMgr.MAIL_COST, true);
			MailMgr.sendMail(player, destID, title, content, gold, isCr, MailTemplate.CLASSIC.getValue());
		}
		else if (packetId == PacketID.MAIL_OPENED)
		{
			long GUID = connection.readLong();
			if (MailMgr.openMail(player.getUnitID(), GUID) == -1)
				Log.writePlayerLog(player, "Tried to open a non-existing mail");
			else
				mailOpened(player, GUID);
		}
		else if (packetId == PacketID.MAIL_RETURN_CR)
		{
			long GUID = connection.readLong();
			Mail mail = MailMgr.getMail(player.getUnitID(), GUID);
			if (mail != null && mail.getIsCR())
			{
				deleteMail(player, mail);
				MailMgr.sendBackCR(mail);
			}			
		}
	}
	
	public static void sendMail(Player player, Mail mail, boolean sendHeader, boolean startPacket)
	{
		Connection connection = player.getConnection();
		if (startPacket)
			connection.startPacket();
		if (sendHeader)
		{
			connection.writeShort(PacketID.MAIL);
			connection.writeShort(PacketID.MAIL_RECEIVED);
		}
		connection.writeLong(mail.getGUID());
		connection.writeLong(mail.getDeleteDate());
		connection.writeString(mail.getAuthorName());
		connection.writeString(mail.getTitle());
		connection.writeString(mail.getContent());
		connection.writeInt(mail.getGold());
		connection.writeBoolean(mail.getIsCR());
		connection.writeByte(mail.getTemplate());
		connection.writeBoolean(mail.getRead());
		connection.writeBoolean(mail.canReply());
		if (startPacket)
		{
			connection.endPacket();
			connection.send();
		}
	}
	
	public static void mailOpened(Player player, long GUID)
	{
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.MAIL);
		connection.writeShort(PacketID.MAIL_OPENED);
		connection.writeLong(GUID);
		connection.endPacket();
		connection.send();
	}
	
	public static void initMail(Player player)
	{
		ArrayList<Mail> mailList = MailMgr.getMailList(player.getUnitID());
		if (mailList == null || mailList.size() == 0)
			return;
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.MAIL);
		connection.writeShort(PacketID.MAIL_INIT);
		connection.writeInt(mailList.size());
		int i = -1;
		while (++i < mailList.size())
			sendMail(player, mailList.get(i), false, false);
		connection.endPacket();
		connection.send();
	}
	
	public static void deleteMail(Player player, Mail mail)
	{
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.MAIL);
		connection.writeShort(PacketID.MAIL_DELETE);
		connection.writeLong(mail.getGUID());
		connection.endPacket();
		connection.send();
	}
	
	public static void mailSent(Player player)
	{
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.MAIL);
		connection.writeShort(PacketID.MAIL_SEND);
		connection.endPacket();
		connection.send();
	}
}

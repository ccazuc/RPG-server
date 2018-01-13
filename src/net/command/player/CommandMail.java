package net.command.player;

import java.util.HashMap;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
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
	public void read(Player player) {
		Connection connection = player.getConnection();
		short packetId = connection.readShort();
		if (packetId == PacketID.MAIL_DELETE) {
			long GUID = connection.readLong();
			MailMgr.deleteMail(GUID);
		}
		else if (packetId == PacketID.MAIL_SEND) {
			String destName = connection.readString();
			String title = connection.readString();
			String content = connection.readString();
			int gold = connection.readInt();
			boolean isCr = connection.readBoolean();
			if (player.getGold() < MailMgr.MAIL_COST) {
				//TODO: send red alert not enough money
				return;
			}
			int destID = CharacterMgr.loadCharacterIDFromName(destName);
			if (destID == -1) {
				//TODO: send red alert no player found
				return;
			}
			player.setGold(player.getGold() - MailMgr.MAIL_COST, true);
			MailMgr.sendMail(player, destID, title, content, gold, isCr, MailTemplate.CLASSIC.getValue());
		}
		else if (packetId == PacketID.MAIL_OPENED) {
			long GUID = connection.readLong();
			if (MailMgr.openMail(GUID) == -1)
				Log.writePlayerLog(player, "Tried to open a non-existing mail");
			else
				mailOpened(player, GUID);
		}
	}
	
	public static void sendMail(Player player, Mail mail) {
		if (!player.isOnline()) {
			return;
		}
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.MAIL);
		connection.writeShort(PacketID.MAIL_SEND);
		connection.writeLong(mail.getGUID());
		connection.writeLong(mail.getDeleteDate());
		connection.writeString(mail.getAutorName());
		connection.writeString(mail.getTitle());
		connection.writeString(mail.getContent());
		connection.writeInt(mail.getGold());
		connection.writeBoolean(mail.getIsCR());
		connection.writeByte(mail.getTemplate());
		connection.endPacket();
		connection.send();
	}
	
	public static void mailOpened(Player player, long GUID) {
		Connection connection = player.getConnection();
		connection.startPacket();
		connection.writeShort(PacketID.MAIL);
		connection.writeShort(PacketID.MAIL_OPENED);
		connection.writeLong(GUID);
		connection.endPacket();
		connection.send();
	}
	
	public static void initMail(Player player) {
		HashMap<Long, Mail> mailMap = MailMgr.getMailMap();
		for (Mail mail : mailMap.values()) {
			if (mail.getDestID() == player.getUnitID()) {
				sendMail(player, mail);
			}
		}
	}
}

package net.command.chat;

import java.util.ArrayList;

import net.Server;
import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.chat.ChatCommandHandler;
import net.game.manager.ChannelMgr;
import net.game.manager.IgnoreMgr;
import net.game.unit.Player;
import net.utils.Color;
import net.utils.StringUtils;

public class CommandSendMessage extends Command {

	private final static int MAXIMUM_LENGTH = 255;
	
	public CommandSendMessage(String name, boolean debug)
	{
		super(name, debug);
	}	
	
	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		String message = connection.readString();
		MessageType type = MessageType.values()[connection.readByte()];
		if(message.length() >= 2 && message.charAt(0) == '.' && message.charAt(1) != '.') {
			ChatCommandHandler.parse(message, player);
			if(type == MessageType.WHISPER || type == MessageType.CHANNEL) { //TODO: find a better way to clear the buffer
				connection.readString();
			}
			return;
		}
		//if(player.isMuted()) {
			//return;
		//}
		if(message.length() > MAXIMUM_LENGTH) {
			message = message.substring(0, MAXIMUM_LENGTH);
		}
		message = StringUtils.removeSpacesDuplicate(message);
		if(type == MessageType.WHISPER) {
			String target = connection.readString();
			target = StringUtils.formatPlayerName(target);
			if(!StringUtils.checkPlayerNameLength(target)) {
				CommandPlayerNotFound.write(connection, target);
				return;
			}
			Player tmp = Server.getInGameCharacterByName(target);
			if(tmp == null) {
				CommandPlayerNotFound.write(connection, target);
				return;
			}
			writeWhisper(connection, tmp.getName(), message, false, tmp.isGMOn());
			if(!IgnoreMgr.isIgnored(tmp.getUnitID(), player.getUnitID())) {
				writeWhisper(tmp.getConnection(), tmp.getName(), message, true, player.isGMOn());
			}
			else {
				selfWithoutAuthor(connection, tmp.getName()+IgnoreMgr.ignoreMessage, MessageType.SELF, MessageColor.RED);
			}
		}
		else if(type == MessageType.PARTY) {
			if(player.getParty() == null) {
				selfWithoutAuthor(connection, "You are not in a party.", MessageType.SELF);
				return;
			}
			int i = player.getParty().getPlayerList().length;
			while(--i >= 0) {
				if(player.getParty().getPlayerList()[i] != null && !IgnoreMgr.isIgnored(player.getUnitID(), player.getParty().getPlayerList()[i].getUnitID())) {
					if(player.getParty().isPartyLeader(player)) {
						write(player.getParty().getPlayerList()[i].getConnection(), message, player.getName(), MessageType.PARTY_LEADER, player.isGMOn());
					}
					else {
						write(player.getParty().getPlayerList()[i].getConnection(), message, player.getName(), MessageType.PARTY, player.isGMOn());
					}
				}
			}
		}
		else if(type == MessageType.GUILD) {
			if(player.getGuild() == null) {
				selfWithoutAuthor(connection, "You are not in a guild.", MessageType.SELF);
				return;
			}
			if(!player.getGuild().getMember(player.getUnitID()).getRank().canTalkInGuildChannel()) {
				selfWithoutAuthor(connection, "You don't have the right to do this.", MessageType.SELF);
				return;
			}
			int i = player.getGuild().getMemberList().size();
			while(--i >= 0) {
				if(player.getGuild().getMemberList().get(i).isOnline() && player.getGuild().getMemberList().get(i).getRank().canListenGuildChannel() && !IgnoreMgr.isIgnored(player.getUnitID(), player.getGuild().getMemberList().get(i).getId())) {
					write(Server.getInGameCharacter(player.getGuild().getMemberList().get(i).getId()).getConnection(), message, player.getName(), MessageType.GUILD, player.isGMOn());
				}
			}
		}
		else if(type == MessageType.CHANNEL) {
			String channelID = connection.readString();
			ChannelMgr mgr = ChannelMgr.getChannelMgr(player.getFaction());
			if(!mgr.playerHasJoinChannel(channelID, player)) {
				return;
			}
			if(mgr.isMuted(channelID, player)) {
				return;
			}
			ArrayList<Integer> list = mgr.getPlayerList(channelID);
			boolean isGM = player.isGMOn();
			int i = list.size();
			while(--i >= 0) {
				Player channelMember = Server.getInGameCharacter(list.get(i));
				if(channelMember != null) {
					writeChannel(channelMember.getConnection(), channelID, message, player.getName(), isGM);
				}
			}
		}
		else {
			sendMessageToUsers(player.getUnitID(), message, player.getName(), type, player.isGMOn());
		}
	}
	
	private static void writeChannel(Connection connection, String channelID, String message, String author, boolean isGM) {
		connection.startPacket();
		connection.writeShort(PacketID.SEND_MESSAGE);
		connection.writeByte(MessageType.CHANNEL.getValue());
		connection.writeString(message);
		connection.writeString(channelID);
		connection.writeBoolean(true);
		connection.writeString(author);
		connection.writeBoolean(isGM);
		connection.endPacket();
		connection.send();
	}
	
	public static void writeChannel(Connection connection, String channelID, String message) {
		connection.startPacket();
		connection.writeShort(PacketID.SEND_MESSAGE);
		connection.writeByte(MessageType.CHANNEL.getValue());
		connection.writeString(message);
		connection.writeString(channelID);
		connection.writeBoolean(false);
		connection.endPacket();
		connection.send();
	}
	
	private static void writeWhisper(Connection connection, String name, String message, boolean isTarget, boolean isGM) { //used for whisper
		connection.startPacket();
		connection.writeShort(PacketID.SEND_MESSAGE);
		connection.writeByte(MessageType.WHISPER.getValue());
		connection.writeString(message);
		connection.writeString(name);
		connection.writeBoolean(isTarget);
		connection.writeBoolean(isGM);
		connection.endPacket();
		connection.send();
	}
	
	private static void sendMessageToUsers(int id, String message, String author, MessageType type, boolean isGM) { //used for say and yell
		for(Player player : Server.getInGamePlayerList().values()) {
			if(!IgnoreMgr.isIgnored(id, player.getUnitID())) {
				write(player.getConnection(), message, author, type, isGM);
			}
		}
	}
	
	public static void write(Connection connection, String message, String author, MessageType type, boolean isGM) { //used 
		connection.startPacket();
		connection.writeShort(PacketID.SEND_MESSAGE);
		connection.writeByte(type.getValue());
		connection.writeString(message);
		connection.writeString(author);
		connection.writeBoolean(isGM);
		connection.endPacket();
		connection.send();
	}
	
	public static void selfWithAuthor(Connection connection, String message, String author, MessageType type) { //used 
		connection.startPacket();
		connection.writeShort(PacketID.SEND_MESSAGE);
		connection.writeByte(type.getValue());
		connection.writeString(message);
		connection.writeBoolean(true);
		connection.writeString(author);
		connection.writeBoolean(true);
		connection.writeByte(MessageColor.YELLOW.getValue());
		connection.endPacket();
		connection.send();
	}
	
	public static void selfWithAuthor(Connection connection, String message, String author, MessageType type, Color color) { //used 
		connection.startPacket();
		connection.writeShort(PacketID.SEND_MESSAGE);
		connection.writeByte(type.getValue());
		connection.writeString(message);
		connection.writeBoolean(true);
		connection.writeString(author);
		connection.writeBoolean(false);
		connection.writeColor(color);
		connection.endPacket();
		connection.send();
	}
	
	public static void selfWithAuthor(Connection connection, String message, String author, MessageType type, MessageColor color) { //used 
		connection.startPacket();
		connection.writeShort(PacketID.SEND_MESSAGE);
		connection.writeByte(type.getValue());
		connection.writeString(message);
		connection.writeBoolean(true);
		connection.writeString(author);
		connection.writeBoolean(true);
		connection.writeByte(color.getValue());
		connection.endPacket();
		connection.send();
	}
	
	public static void selfWithoutAuthor(Connection connection, String message, MessageType type, Color color) { //used 
		connection.startPacket();
		connection.writeShort(PacketID.SEND_MESSAGE);
		connection.writeByte(type.getValue());
		connection.writeString(message);
		connection.writeBoolean(false);
		connection.writeBoolean(false);
		connection.writeColor(color);
		connection.endPacket();
		connection.send();
	}
	
	public static void selfWithoutAuthor(Connection connection, String message, MessageType type, MessageColor color) { //used 
		connection.startPacket();
		connection.writeShort(PacketID.SEND_MESSAGE);
		connection.writeByte(type.getValue());
		connection.writeString(message);
		connection.writeBoolean(false);
		connection.writeBoolean(true);
		connection.writeByte(color.getValue());
		connection.endPacket();
		connection.send();
	}
	
	public static void selfWithoutAuthor(Connection connection, String message, MessageType type) { //used 
		connection.startPacket();
		connection.writeShort(PacketID.SEND_MESSAGE);
		connection.writeByte(type.getValue());
		connection.writeString(message);
		connection.writeBoolean(false);
		connection.writeBoolean(true);
		connection.writeByte(MessageColor.YELLOW.getValue());
		connection.endPacket();
		connection.send();
	}
}

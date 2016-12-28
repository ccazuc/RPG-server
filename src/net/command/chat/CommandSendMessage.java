package net.command.chat;

import net.Server;
import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.Player;
import net.game.chat.ChatCommandHandler;
import net.game.manager.IgnoreMgr;
import net.utils.Color;

public class CommandSendMessage extends Command {

	private final static int MAXIMUM_LENGTH = 255;
	
	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		String message = connection.readString();
		MessageType type = MessageType.values()[connection.readChar()];
		if(message.length() >= 2 && message.charAt(0) == '.' && message.charAt(1) != '.') {
			ChatCommandHandler.parse(message, player);
			return;
		}
		if(message.length() > MAXIMUM_LENGTH) {
			message = message.substring(0, MAXIMUM_LENGTH);
		}
		if(type == MessageType.WHISPER) {
			String target = connection.readString();
			target = target.substring(0, 1).toUpperCase()+target.substring(1).toLowerCase();
			Player temp = Server.getInGameCharacterByName(target);
			if(temp == null) {
				CommandPlayerNotFound.write(connection, target);
				return;
			}
			writeWhisper(connection, temp.getName(), message, false);
			if(!IgnoreMgr.isIgnored(temp.getCharacterId(), player.getCharacterId())) {
				writeWhisper(temp.getConnection(), temp.getName(), message, true);
			}
			else {
				selfWithoutAuthor(connection, temp.getName()+IgnoreMgr.ignoreMessage, MessageType.SELF, MessageColor.RED);
			}
		}
		else if(type == MessageType.PARTY) {
			if(player.getParty() != null) {
				int i = 0;
				while(i < player.getParty().getPlayerList().length) {
					if(player.getParty().getPlayerList()[i] != null && !IgnoreMgr.isIgnored(player.getCharacterId(), player.getParty().getPlayerList()[i].getid())) {
						if(player.getParty().isPartyLeader(player)) {
							write(player.getParty().getPlayerList()[i].getConnection(), message, player.getName(), MessageType.PARTY_LEADER);
						}
						else {
							write(player.getParty().getPlayerList()[i].getConnection(), message, player.getName(), MessageType.PARTY);
						}
					}
					i++;
				}
			}
			else {
				selfWithoutAuthor(connection, "You are not in a party.", MessageType.SELF);
			}
		}
		else if(type == MessageType.GUILD) {
			if(player.getGuild() == null) {
				selfWithoutAuthor(connection, "You are not in a guild.", MessageType.SELF);
				return;
			}
			
			if(!player.getGuild().getMember(player.getCharacterId()).getRank().canTalkInGuildChannel()) {
				selfWithoutAuthor(connection, "You don't have the right to do this.", MessageType.SELF);
				return;
			}
			int i = 0;
			while(i < player.getGuild().getMemberList().size()) {
				if(player.getGuild().getMemberList().get(i).isOnline() && player.getGuild().getMemberList().get(i).getRank().canListenGuildChannel() && !IgnoreMgr.isIgnored(player.getCharacterId(), player.getGuild().getMemberList().get(i).getId())) {
					write(Server.getInGameCharacter(player.getGuild().getMemberList().get(i).getId()).getConnection(), message, player.getName(), MessageType.GUILD);
				}
				i++;
			}
		}
		else {
			sendMessageToUsers(player.getCharacterId(), message, player.getName(), type);
		}
	}
	
	private static void writeWhisper(Connection connection, String name, String message, boolean isTarget) { //used for whisper
		connection.startPacket();
		connection.writeShort(PacketID.SEND_MESSAGE);
		connection.writeChar(MessageType.WHISPER.getValue());
		connection.writeString(message);
		connection.writeString(name);
		connection.writeBoolean(isTarget);
		connection.endPacket();
		connection.send();
	}
	
	private static void sendMessageToUsers(int id, String message, String author, MessageType type) { //used for say and yell
		for(Player player : Server.getInGamePlayerList().values()) {
			if(!IgnoreMgr.isIgnored(id, player.getCharacterId())) {
				write(player.getConnection(), message, author, type);
			}
		}
	}
	
	public static void write(Connection connection, String message, String author, MessageType type) { //used 
		connection.startPacket();
		connection.writeShort(PacketID.SEND_MESSAGE);
		connection.writeChar(type.getValue());
		connection.writeString(message);
		connection.writeString(author);
		connection.endPacket();
		connection.send();
	}
	
	public static void selfWithAuthor(Connection connection, String message, String author, MessageType type) { //used 
		connection.startPacket();
		connection.writeShort(PacketID.SEND_MESSAGE);
		connection.writeChar(type.getValue());
		connection.writeString(message);
		connection.writeBoolean(true);
		connection.writeString(author);
		connection.writeBoolean(true);
		connection.writeChar(MessageColor.YELLOW.getValue());
		connection.endPacket();
		connection.send();
	}
	
	public static void selfWithAuthor(Connection connection, String message, String author, MessageType type, Color color) { //used 
		connection.startPacket();
		connection.writeShort(PacketID.SEND_MESSAGE);
		connection.writeChar(type.getValue());
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
		connection.writeChar(type.getValue());
		connection.writeString(message);
		connection.writeBoolean(true);
		connection.writeString(author);
		connection.writeBoolean(true);
		connection.writeChar(color.getValue());
		connection.endPacket();
		connection.send();
	}
	
	public static void selfWithoutAuthor(Connection connection, String message, MessageType type, Color color) { //used 
		connection.startPacket();
		connection.writeShort(PacketID.SEND_MESSAGE);
		connection.writeChar(type.getValue());
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
		connection.writeChar(type.getValue());
		connection.writeString(message);
		connection.writeBoolean(false);
		connection.writeBoolean(true);
		connection.writeChar(color.getValue());
		connection.endPacket();
		connection.send();
	}
	
	public static void selfWithoutAuthor(Connection connection, String message, MessageType type) { //used 
		connection.startPacket();
		connection.writeShort(PacketID.SEND_MESSAGE);
		connection.writeChar(type.getValue());
		connection.writeString(message);
		connection.writeBoolean(false);
		connection.writeBoolean(true);
		connection.writeChar(MessageColor.YELLOW.getValue());
		connection.endPacket();
		connection.send();
	}
}

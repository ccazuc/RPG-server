package net.command.chat;

import net.Server;
import net.command.Command;
import net.connection.Connection;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.Player;

public class CommandSendMessage extends Command {

	private final static int MAXIMUM_LENGTH = 255;
	public CommandSendMessage(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	@Override
	public void read() {
		String message = this.connection.readString();
		MessageType type = MessageType.values()[this.connection.readChar()];
		if(type == MessageType.WHISPER) {
			String target = this.connection.readString();
			target = target.substring(0, 1).toUpperCase()+target.substring(1).toLowerCase();
			Player player = Server.getInGameCharacter(target);
			if(player != null) {
				writeWhisper(this.connection, player.getName(), message, false);
				writeWhisper(player.getConnection(), this.player.getName(), message, true);
			}
			else {
				CommandPlayerNotFound.write(this.connection, target);
			}
		}
		else if(type == MessageType.PARTY) {
			if(this.player.getParty() != null) {
				int i = 0;
				while(i < this.player.getParty().getPlayerList().length) {
					if(this.player.getParty().getPlayerList()[i] != null) {
						write(this.player.getParty().getPlayerList()[i].getConnection(), message, this.player.getName(), MessageType.PARTY);
					}
					i++;
				}
			}
			else {
				write(this.connection, "You are not in a party.", MessageType.SELF);
			}
		}
		else {
			if(message.length() < MAXIMUM_LENGTH) {
				sendMessageToUsers(message, this.player.getName(), type);
			}
		}
	}
	
	private static void writeWhisper(Connection connection, String name, String message, boolean isTarget) { //used for whisper
		connection.writeByte(PacketID.SEND_MESSAGE);
		connection.writeChar(MessageType.WHISPER.getValue());
		connection.writeString(message);
		connection.writeString(name);
		connection.writeBoolean(isTarget);
		connection.send();
	}
	
	private static void sendMessageToUsers(String message, String author, MessageType type) { //used for say and yell
		for(Player player : Server.getInGamePlayerList().values()) {
			write(player.getConnection(), message, author, type);
		}
	}
	
	public static void write(Connection connection, String message, String author, MessageType type) { //us
		connection.writeByte(PacketID.SEND_MESSAGE);
		connection.writeChar(type.getValue());
		connection.writeString(message);
		connection.writeString(author);
		connection.send();
	}
	
	public static void write(Connection connection, String message, MessageType type) { //used for self
		connection.writeByte(PacketID.SEND_MESSAGE);
		connection.writeChar(type.getValue());
		connection.writeString(message);
		connection.send();
	}
}

package net.command.chat;

import java.util.ArrayList;

import net.Server;
import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.manager.ChannelMgr;
import net.game.unit.Player;

public class CommandChannel extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		short packetId = connection.readShort();
		if(packetId == PacketID.CHANNEL_JOIN) {
			String channelID = connection.readString();
			String password = connection.readString();
			if(player.getNumberChatChannelJoined() == ChannelMgr.MAXIMUM_CHANNEL_JOINED) {
				return;
			}
			if(ChannelMgr.playerHasJoinChannel(channelID, player)) {
				return;
			}
			if(!ChannelMgr.checkPassword(channelID, password)) {
				CommandSendMessage.selfWithoutAuthor(connection, "Incorrect password for the channel "+channelID, MessageType.SELF);
				return;
			}
			notifyPlayerJoinedChannel(player, channelID);
			ChannelMgr.addPlayer(channelID, password, player);
			joinChannel(player, channelID);
			player.joinedChannel(channelID);
			sendMembers(player.getConnection(), channelID);
		}
		else if(packetId == PacketID.CHANNEL_LEAVE) {
			String channelID = connection.readString();
			if(!ChannelMgr.removePlayer(channelID, player)) {
				return;
			}
			notifyPlayerLeftChannel(player, channelID);
			leaveChannel(player, channelID);
			player.leftChannel(channelID);
		}
		else if(packetId == PacketID.CHANNEL_CHANGE_PASSWORD) {
			
		}
		else if(packetId == PacketID.CHANNEL_INVITE_PLAYER) {
			
		}
		else if(packetId == PacketID.CHANNEL_BAN_PLAYER) {
			
		}
		else if(packetId == PacketID.CHANNEL_KICK_PLAYER) {
			
		}
		else if(packetId == PacketID.CHANNEL_SET_LEADER) {
			
		}
		else if(packetId == PacketID.CHANNEL_MUTE_PLAYER) {
			
		}
	}
	
	public static void notifyPlayerLeftChannelOnLogout(Player player) {
		if(player.getJoinedChannelList() == null) {
			return;
		}
		int i = player.getJoinedChannelList().size();
		while(--i >= 0) {
			notifyPlayerLeftChannel(player, player.getJoinedChannelList().get(i));
		}
	}
	
	private static void notifyPlayerJoinedChannel(Player player, String channelID) {
		ArrayList<Integer> list = ChannelMgr.getPlayerList(channelID);
		int i = list.size();
		while(--i >= 0) {
			Player member = Server.getInGameCharacter(list.get(i));
			if(member != null) {
				member.getConnection().startPacket();
				member.getConnection().writeShort(PacketID.CHANNEL);
				member.getConnection().writeShort(PacketID.CHANNEL_MEMBER_JOINED);
				member.getConnection().writeInt(player.getUnitID());
				member.getConnection().writeString(player.getName());
				member.getConnection().endPacket();
				member.getConnection().send();
			}
		}
	}
	
	private static void notifyPlayerLeftChannel(Player player, String channelID) {
		ArrayList<Integer> list = ChannelMgr.getPlayerList(channelID);
		int i = list.size();
		while(--i >= 0) {
			Player member = Server.getInGameCharacter(list.get(i));
			if(member != null) {
				member.getConnection().startPacket();
				member.getConnection().writeShort(PacketID.CHANNEL);
				member.getConnection().writeShort(PacketID.CHANNEL_MEMBER_LEFT);
				member.getConnection().writeInt(player.getUnitID());
				member.getConnection().endPacket();
				member.getConnection().send();
			}
		}
	}
	
	public static void sendMembers(Connection connection, String channelID) {
		connection.startPacket();
		connection.writeShort(PacketID.CHANNEL);
		connection.writeShort(PacketID.CHANNEL_SEND_MEMBERS);
		ArrayList<Integer> list = ChannelMgr.getPlayerList(channelID);
		int i = list.size();
		while(--i >= 0) {
			Player player = Server.getInGameCharacter(list.get(i));
			if(player != null) {
				connection.writeInt(player.getUnitID());
				connection.writeString(player.getName());
			}
		}
		connection.writeInt(-1);
		connection.endPacket();
		connection.send();
	}
	
	public static void joinChannel(Player player, String channelID) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.CHANNEL);
		player.getConnection().writeShort(PacketID.CHANNEL_JOIN);
		player.getConnection().writeString(channelID);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	public static void leaveChannel(Player player, String channelID) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.CHANNEL);
		player.getConnection().writeShort(PacketID.CHANNEL_LEAVE);
		player.getConnection().writeString(channelID);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
}

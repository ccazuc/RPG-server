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
		ChannelMgr mgr = ChannelMgr.getChannelMgr(player.getFaction());
		if(packetId == PacketID.CHANNEL_JOIN) {
			String channelName = connection.readString();
			String channelID = ChannelMgr.formatChannelName(channelName);
			int value = connection.readInt();
			String password = connection.readString();
			if(player.getNumberChatChannelJoined() == ChannelMgr.MAXIMUM_CHANNEL_JOINED) {
				return;
			}
			if(mgr.playerHasJoinChannel(channelID, player)) {
				return;
			}
			if(!mgr.channelExists(channelID)) {
				mgr.createChannel(channelID, password, player);
			}
			else if(!mgr.checkPassword(channelID, password)) {
				CommandSendMessage.selfWithoutAuthor(connection, "Incorrect password for the channel "+channelID, MessageType.SELF);
				return;
			}
			notifyPlayerJoinedChannel(player, channelID);
			mgr.addPlayer(channelID, password, player);
			joinChannel(player, channelName, channelID, value, password);
			sendMembers(player.getConnection(), channelID, player);
		}
		else if(packetId == PacketID.CHANNEL_LEAVE) {
			String channelID = connection.readString();
			if(!mgr.removePlayer(channelID, player)) {
				return;
			}
			notifyPlayerLeftChannel(player, channelID);
			leaveChannel(player, channelID);
		}
		else if(packetId == PacketID.CHANNEL_CHANGE_PASSWORD) { //TODO: error message and send message to all users
			String channelID = connection.readString();
			String password = connection.readString();
			if(!mgr.isLeader(channelID, player)) {
				//send error message
				return;
			}
			mgr.setPassword(channelID, password);
			notifyPasswordChanged(player, channelID);
		}
		else if(packetId == PacketID.CHANNEL_INVITE_PLAYER) {
			
		}
		else if(packetId == PacketID.CHANNEL_BAN_PLAYER) { //TODO: error message and send message to all users
			String channelID = connection.readString();
			int playerID = connection.readInt();
			if(!mgr.playerHasJoinChannel(channelID, player)) {
				return;
			}
			if(!mgr.isLeader(channelID, player) && !mgr.isModerator(channelID, player)) {
				//TODO: not enought rights
				return;
			}
			Player target = Server.getInGameCharacter(playerID);
			if(target == null) {
				return;
			}
			if(mgr.isModerator(channelID, target)) {
				//TODO: send not enough right
				return;
			}
			if(mgr.isBanned(channelID, target)) {
				return;
			}
			mgr.banPlayer(channelID, target);
		}
		else if(packetId == PacketID.CHANNEL_KICK_PLAYER) { //TODO: error message and send message to all users
			String channelID = connection.readString();
			int playerID = connection.readInt();
			if(!mgr.playerHasJoinChannel(channelID, player)) {
				return;
			}
			Player target = Server.getInGameCharacter(playerID);
			if(target == null) {
				return;
			}
			if(!mgr.playerHasJoinChannel(channelID, target)) {
				return;
			}
			if(mgr.isLeader(channelID, player) || (mgr.isModerator(channelID, player) && !mgr.isModerator(channelID, target))) {
				mgr.removePlayer(channelID, target);
				notifyPlayerKicked(channelID, player, target);
			}
			else {
				//send not enough right
			}
		}
		else if(packetId == PacketID.CHANNEL_SET_LEADER) {
			String channelID = connection.readString();
			int playerID = connection.readInt();
			if(!mgr.playerHasJoinChannel(channelID, player)) {
				return;
			}
			if(!mgr.isLeader(channelID, player)) {
				//error message
				return;
			}
			Player target = Server.getInGameCharacter(playerID);
			if(target == null) {
				return;
			}
			mgr.setLeader(channelID, target);
			notifyPlayerLeader(channelID, target, true);
			//send message to users
		}
		else if(packetId == PacketID.CHANNEL_MUTE_PLAYER) {
			String channelID = connection.readString();
			int playerID = connection.readInt();
			if(!mgr.playerHasJoinChannel(channelID, player)) {
				return;
			}
			Player target = Server.getInGameCharacter(playerID);
			if(target == null) {
				return;
			}
			if(!mgr.playerHasJoinChannel(channelID, target)) {
				return;
			}
			if(mgr.isLeader(channelID, player) || (mgr.isModerator(channelID, player) && !mgr.isModerator(channelID, target))) {
				mgr.mutePlayer(channelID, target);
			}
			else {
				//send not enough right
			}
		}
		else if(packetId == PacketID.CHANNEL_SET_MODERATOR) {
			String channelID = connection.readString();
			int playerID = connection.readInt();
			boolean isModerator = connection.readBoolean();
			Player target = Server.getInGameCharacter(playerID);
			if(target == null) {
				return;
			}
			if(!mgr.playerHasJoinChannel(channelID, target)) {
				return;
			}
			if(!mgr.isLeader(channelID, player)) {
				//TODO: send not enough rights
				return;
			}
			mgr.setModerator(channelID, target, isModerator);
		}
	}
	
	public static void notifyPlayerKicked(String channelID, Player player, Player kicked) {
		ArrayList<Integer> list = ChannelMgr.getChannelMgr(player.getFaction()).getPlayerList(channelID);
		int i = list.size();
		while(--i >= 0) {
			Player member = Server.getInGameCharacter(list.get(i));
			if(member != null) {
				member.getConnection().startPacket();
				member.getConnection().writeShort(PacketID.CHANNEL);
				member.getConnection().writeShort(PacketID.CHANNEL_KICK_PLAYER);
				member.getConnection().writeString(channelID);
				member.getConnection().writeString(player.getName());
				member.getConnection().writeInt(kicked.getUnitID());
				member.getConnection().endPacket();
				member.getConnection().send();
			}
		}
	}
	
	public static void notifyPlayerLeader(String channelID, Player player, boolean chatMessage) {
		ArrayList<Integer> list = ChannelMgr.getChannelMgr(player.getFaction()).getPlayerList(channelID);
		int i = list.size();
		while(--i >= 0) {
			Player member = Server.getInGameCharacter(list.get(i));
			if(member != null) {
				member.getConnection().startPacket();
				member.getConnection().writeShort(PacketID.CHANNEL);
				member.getConnection().writeShort(PacketID.CHANNEL_SET_LEADER);
				member.getConnection().writeString(channelID);
				member.getConnection().writeInt(player.getUnitID());
				member.getConnection().writeBoolean(chatMessage);
				member.getConnection().endPacket();
				member.getConnection().send();
			}
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
	
	private static void notifyPasswordChanged(Player player, String channelID) {
		ArrayList<Integer> list = ChannelMgr.getChannelMgr(player.getFaction()).getPlayerList(channelID);
		int i = list.size();
		while(--i >= 0) {
			Player member = Server.getInGameCharacter(list.get(i));
			if(member != null) {
				member.getConnection().startPacket();
				member.getConnection().writeShort(PacketID.CHANNEL);
				member.getConnection().writeShort(PacketID.CHANNEL_CHANGE_PASSWORD);
				member.getConnection().writeString(channelID);
				member.getConnection().writeInt(player.getUnitID());
				member.getConnection().writeString(player.getName());
				member.getConnection().endPacket();
				member.getConnection().send();
			}
		}
	}
	
	private static void notifyPlayerJoinedChannel(Player player, String channelID) {
		ArrayList<Integer> list = ChannelMgr.getChannelMgr(player.getFaction()).getPlayerList(channelID);
		int i = list.size();
		while(--i >= 0) {
			Player member = Server.getInGameCharacter(list.get(i));
			if(member != null) {
				member.getConnection().startPacket();
				member.getConnection().writeShort(PacketID.CHANNEL);
				member.getConnection().writeShort(PacketID.CHANNEL_MEMBER_JOINED);
				member.getConnection().writeString(channelID);
				member.getConnection().writeInt(player.getUnitID());
				member.getConnection().writeString(player.getName());
				member.getConnection().endPacket();
				member.getConnection().send();
			}
		}
	}
	
	private static void notifyPlayerLeftChannel(Player player, String channelID) {
		ArrayList<Integer> list = ChannelMgr.getChannelMgr(player.getFaction()).getPlayerList(channelID);
		int i = list.size();
		while(--i >= 0) {
			Player member = Server.getInGameCharacter(list.get(i));
			if(member != null) {
				member.getConnection().startPacket();
				member.getConnection().writeShort(PacketID.CHANNEL);
				member.getConnection().writeShort(PacketID.CHANNEL_MEMBER_LEFT);
				member.getConnection().writeString(channelID);
				member.getConnection().writeInt(player.getUnitID());
				member.getConnection().endPacket();
				member.getConnection().send();
			}
		}
	}
	
	public static void sendMembers(Connection connection, String channelID, Player player) {
		connection.startPacket();
		connection.writeShort(PacketID.CHANNEL);
		connection.writeShort(PacketID.CHANNEL_SEND_MEMBERS);
		ArrayList<Integer> list = ChannelMgr.getChannelMgr(player.getFaction()).getPlayerList(channelID);
		connection.writeString(channelID);
		connection.writeInt(ChannelMgr.getChannelMgr(player.getFaction()).getLeaderID(channelID));
		int i = list.size();
		while(--i >= 0) {
			Player target = Server.getInGameCharacter(list.get(i));
			if(target != null) {
				connection.writeInt(target.getUnitID());
				connection.writeString(target.getName());
			}
		}
		connection.writeInt(-1);
		connection.endPacket();
		connection.send();
	}
	
	public static void joinChannel(Player player, String channelName, String channelID, int value, String password) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.CHANNEL);
		player.getConnection().writeShort(PacketID.CHANNEL_JOIN);
		player.getConnection().writeString(channelName);
		player.getConnection().writeString(channelID);
		player.getConnection().writeInt(value);
		player.getConnection().writeString(password);
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

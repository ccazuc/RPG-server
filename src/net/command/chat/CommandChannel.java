package net.command.chat;

import java.util.ArrayList;

import net.Server;
import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.log.Log;
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
		else if(packetId == PacketID.CHANNEL_CHANGE_PASSWORD) { //TODO: error message and send message to all users
			String channelID = connection.readString();
			String password = connection.readString();
			if(!ChannelMgr.isLeader(channelID, player)) {
				//send error message
				return;
			}
			ChannelMgr.setPassword(channelID, password);
		}
		else if(packetId == PacketID.CHANNEL_INVITE_PLAYER) {
			
		}
		else if(packetId == PacketID.CHANNEL_BAN_PLAYER) { //TODO: error message and send message to all users
			String channelID = connection.readString();
			String playerName = connection.readString();
			if(!ChannelMgr.playerHasJoinChannel(channelID, player)) {
				Log.writePlayerLog(player, "Tried to ban "+playerName+" from channel "+channelID+" whereas he hasn't joined the channel.");
				player.close();
				return;
			}
			Player target = Server.getInGameCharacterByName(playerName);
			if(target == null) {
				return;
			}
			if(ChannelMgr.isBanned(channelID, target)) {
				return;
			}
			if(ChannelMgr.isLeader(channelID, player) || (ChannelMgr.isModerator(channelID, player) && !ChannelMgr.isModerator(channelID, target))) {
				ChannelMgr.banPlayer(channelID, target);
			}
			else {
				//send not enough rights
			}
		}
		else if(packetId == PacketID.CHANNEL_KICK_PLAYER) { //TODO: error message and send message to all users
			String channelID = connection.readString();
			String playerName = connection.readString();
			if(!ChannelMgr.playerHasJoinChannel(channelID, player)) {
				Log.writePlayerLog(player, "Tried to kick "+playerName+" from channel "+channelID+" whereas he hasn't joined the channel.");
				player.close();
				return;
			}
			Player target = Server.getInGameCharacterByName(playerName);
			if(target == null) {
				return;
			}
			if(!ChannelMgr.playerHasJoinChannel(channelID, target)) {
				return;
			}
			if(ChannelMgr.isLeader(channelID, player) || (ChannelMgr.isModerator(channelID, player) && !ChannelMgr.isModerator(channelID, target))) {
				ChannelMgr.removePlayer(channelID, target);
			}
			else {
				//send not enough right
			}
		}
		else if(packetId == PacketID.CHANNEL_SET_LEADER) {
			String channelID = connection.readString();
			String playerName = connection.readString();
			if(!ChannelMgr.playerHasJoinChannel(channelID, player)) {
				Log.writePlayerLog(player, "Tried to give "+playerName+" channel leader from channel "+channelID+" whereas he hasn't joined the channel.");
				player.close();
				return;
			}
			if(!ChannelMgr.isLeader(channelID, player)) {
				//error message
				return;
			}
			ChannelMgr.setLeader(channelID, player);
			//send message to users
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

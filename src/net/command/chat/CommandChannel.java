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
			if(player.getNumberChatChannelJoined() == ChannelMgr.MAXIMUM_CHANNEL_JOINED) {
				return;
			}
			ChannelMgr.addPlayer(channelID, player);
			joinChannel(player, channelID);
			player.setNumberChatChannelJoined((byte)(player.getNumberChatChannelJoined()+1));
		}
		else if(packetId == PacketID.CHANNEL_LEAVE) {
			String channelID = connection.readString();
			if(!ChannelMgr.removePlayer(channelID, player)) {
				return;
			}
			leaveChannel(player, channelID);
			player.setNumberChatChannelJoined((byte)(player.getNumberChatChannelJoined()-1));
		}
		else if(packetId == PacketID.CHANNEL_SEND_MEMBERS) {
			String channelID = connection.readString();
			sendMembers(player.getConnection(), channelID);
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

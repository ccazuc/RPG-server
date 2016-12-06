package net.command.player;

import net.Server;
import net.command.Command;
import net.command.chat.CommandPlayerNotFound;
import net.command.chat.CommandSendMessage;
import net.command.chat.MessageType;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.Party;
import net.game.Player;

public class CommandParty extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		short packetId = connection.readShort();
		if(packetId == PacketID.PARTY_ADD_MEMBER) {
			String name = connection.readString();
			if(!(name.length() > 2)) {
				CommandPlayerNotFound.write(connection, name);
				return;
			}
			name = name.substring(0, 1).toUpperCase()+name.substring(1).toLowerCase();
			Player member = Server.getInGameCharacter(name);
			if(member == null) {
				CommandPlayerNotFound.write(connection, name);
				return;
			}
			if(member.getCharacterId() == player.getCharacterId()) {
				CommandSendMessage.write(connection, "You can't invite yourself in a party.", MessageType.SELF);
				return;
			}
			if(!(isPartyLeader(player, player.getParty() == null || (player.getParty() != null && player.getParty().isPartyLeader(player))))) {
				return;
			}
			if(member.getParty() != null) {
				CommandSendMessage.write(connection, name+" is already in a party.", MessageType.SELF);
				CommandSendMessage.write(member.getConnection(), player.getName()+" tried to invite you in a party.", MessageType.SELF);
				return;
			}
			inviteRequest(member.getConnection(), player.getName());
			player.setPlayerParty(member);
			member.setPlayerParty(player);
			player.setHasInitParty(true);
			CommandSendMessage.write(connection, "You invited "+name+" to join your party.", MessageType.SELF);
		}
		else if(packetId == PacketID.PARTY_DECLINE_REQUEST) {
			if(player.getPlayerParty() != null && player.getPlayerParty().getConnection() != null) {
				requestDeclined(player.getPlayerParty().getConnection(), player.getName());
			}
			else if(player.getPlayerParty() != null) {
				player.getPlayerParty().setParty(null);
			}
			player.setPlayerParty(null);
		}
		else if(packetId == PacketID.PARTY_KICK_PLAYER) {
			int id = connection.readInt();
			if(player.getParty() == null) {
				CommandSendMessage.write(connection, "You are not member of a party.", MessageType.SELF);
				return;
			}
			if(!isPartyLeader(player, player.getParty().isPartyLeader(player))) {
				return;
			}
			if(player.getCharacterId() == id) {
				CommandSendMessage.write(connection, "You can't kick yourself.", MessageType.SELF);
				return;
			}
			Player member = Server.getInGameCharacter(id);
			if(member == null) {
				CommandSendMessage.write(connection, "Player not found", MessageType.SELF);
				return;
			}
			if(!(member.getParty() == player.getParty())) {
				CommandSendMessage.write(connection, member.getName()+" is not a member of your party.", MessageType.SELF);
				return;
			}
			kickPlayer(member);
		}
		else if(packetId == PacketID.PARTY_SET_LEADER) {
			int id = connection.readInt();
			if(player.getParty() == null) {
				CommandSendMessage.write(connection, "You are not member of a party.", MessageType.SELF);
				return;
			}
			if(isPartyLeader(player, player.getParty().isPartyLeader(player))) {
				return;
			}
			if(player.getCharacterId() == id) {
				CommandSendMessage.write(connection, "You already are the leader.", MessageType.SELF);
				return;
			}
			Player member = Server.getInGameCharacter(id);
			if(member == null) {
				CommandSendMessage.write(connection, "Player not found", MessageType.SELF);
				return;
			}
			if(!(member.getParty() == player.getParty())) {
				CommandSendMessage.write(connection, member.getName()+" is not a member of your party.", MessageType.SELF);
				return;
			}
			int i = 0;
			while(i < player.getParty().getPlayerList().length) {
				if(player.getParty().getPlayerList()[i] != null) {
					if(player.getParty().getPlayerList()[i] == member) {
						setLeader(member.getConnection(), member.getCharacterId());
						CommandSendMessage.write(member.getConnection(), "You are now the party leader.", MessageType.SELF);
					}
					else {
						setLeader(player.getParty().getPlayerList()[i].getConnection(), member.getCharacterId());
						CommandSendMessage.write(player.getParty().getPlayerList()[i].getConnection(), member.getName()+" is now the group leader.", MessageType.SELF);
					}
				}
				i++;
				player.getParty().setLeader(member);
			}
		}
		else if(packetId == PacketID.PARTY_ACCEPT_REQUEST) {
			if(player.getPlayerParty() == null) {
				CommandSendMessage.write(player.getPlayerParty().getConnection(), "Nobody invited you to join their party.", MessageType.SELF);
				return;
			}
			if(player.getPlayerParty().getParty() == null) { //if player who sent request is already in a party
				if(!player.getPlayerParty().hasInitParty()) {
					System.out.println("CommandParty:PARTY_ACCEPT_REQUEST ERROR.");
					return;
				}
				Party party = new Party(Server.getInGameCharacter(player.getPlayerParty().getCharacterId()), player);
				player.getPlayerParty().setParty(party);
				player.setParty(party);
				CommandSendMessage.write(player.getPlayerParty().getConnection(), player.getName()+" joined your party.", MessageType.SELF);
				newParty(player, player.getPlayerParty());
				newParty(player.getPlayerParty(), player);
				player.getPlayerParty().setPlayerParty(null);
				player.setPlayerParty(null);
			}
			else {
				if(!isPartyLeader(player, player.getPlayerParty().getParty().isPartyLeader(player.getPlayerParty()))) {
					return;
				}
				player.getPlayerParty().getParty().addMember(player);
				player.setParty(player.getPlayerParty().getParty());
				int i = 0;
				boolean partyInit = false;
				while(i < player.getParty().getPlayerList().length) {
					if(player.getParty().getPlayerList()[i] != null && player.getParty().getPlayerList()[i].getCharacterId() != player.getCharacterId()) {
						memberJoinedParty(player.getParty().getPlayerList()[i].getConnection(), player); //send datas to everyone about this.player
						if(!partyInit) {
							newParty(player, player.getParty().getPlayerList()[i]);
							partyInit = true;
						}
						else {
							memberJoinedParty(connection, player.getParty().getPlayerList()[i]); //send datas about everyone to this.player
						}
						CommandSendMessage.write(player.getParty().getPlayerList()[i].getConnection(), player.getName()+" joined your party.", MessageType.SELF);
					}
					i++;
				}
			}
		}
	}
	
	private static void inviteRequest(Connection connection, String name) {
		connection.writeShort(PacketID.PARTY);
		connection.writeShort(PacketID.PARTY_ADD_MEMBER);
		connection.writeString(name);
		connection.send();
	}
	
	private static void requestDeclined(Connection connection, String name) {
		connection.writeShort(PacketID.PARTY);
		connection.writeShort(PacketID.PARTY_DECLINE_REQUEST);
		connection.writeString(name);
		connection.send();
	}
	
	public static void leaveParty(Player player) {
		if(player.getParty() != null) {
			if(player.getParty().getNumberMembers() <= 2) {
				disbandParty(player);
			}
			else {
				kickPlayer(player);
			}
		}
		if(player.getPlayerParty() != null) {
			player.getPlayerParty().setPlayerParty(null);
			player.setPlayerParty(null);
		}
	}
	
	private static void kickPlayer(Player player) {
		boolean wasLeader = false;
		if(player.getParty().isPartyLeader(player)) {
			int i = 0;
			while(i < player.getParty().getPlayerList().length) {
				if(player.getParty().getPlayerList()[i] != null && player.getParty().getPlayerList()[i].getCharacterId() != player.getCharacterId()) {
					player.getParty().setLeader(player.getParty().getPlayerList()[i]);
					break;
				}
				i++;
			}
		}
		int i = 0;
		while(i < player.getParty().getPlayerList().length) {
			if(player.getParty().getPlayerList()[i] == player) {
				player.getConnection().writeShort(PacketID.PARTY);
				player.getConnection().writeShort(PacketID.PARTY_LEFT);
				player.getConnection().send();
				CommandSendMessage.write(player.getConnection(), "You left the party.", MessageType.SELF);
			}
			else if(player.getParty().getPlayerList()[i] != null) {
				player.getParty().getPlayerList()[i].getConnection().writeShort(PacketID.PARTY);
				player.getParty().getPlayerList()[i].getConnection().writeShort(PacketID.PARTY_MEMBER_LEFT);
				player.getParty().getPlayerList()[i].getConnection().writeInt(player.getCharacterId());
				player.getParty().getPlayerList()[i].getConnection().send();
				CommandSendMessage.write(player.getParty().getPlayerList()[i].getConnection(), player.getName()+" left the party.", MessageType.SELF);
				if(wasLeader) {
					setLeader(player.getParty().getPlayerList()[i].getConnection(), player.getCharacterId());
				}
			}
			i++;
		}
		player.setHasInitParty(false);
		player.getParty().removeMember(player);
		player.getParty().updateMemberPosition();
		player.setParty(null);
	}
	
	private static void disbandParty(Player player) {
		int i = 0;
		while(i < player.getParty().getPlayerList().length) {
			if(player.getParty().getPlayerList()[i] != null) {
				player.getParty().getPlayerList()[i].getConnection().writeShort(PacketID.PARTY);
				player.getParty().getPlayerList()[i].getConnection().writeShort(PacketID.PARTY_DISBAND);
				player.getParty().getPlayerList()[i].getConnection().send();
				System.out.println(player.getConnection().wBufferRemaining()+" remaining, capacity: "+player.getConnection().wBufferCapacity()+" "+player.getConnection());
				CommandSendMessage.write(player.getParty().getPlayerList()[i].getConnection(), "You left the party.", MessageType.SELF);
			}
			i++;
		}
		i = 0;
		Party part = player.getParty();
		while(i < part.getPlayerList().length) {
			if(part.getPlayerList()[i] != null) {
				part.getPlayerList()[i].setParty(null);
				part.getPlayerList()[i].setHasInitParty(false);
			}
			i++;
		}
	}
	
	private static void setLeader(Connection connection, int id) {
		connection.writeShort(PacketID.PARTY);
		connection.writeShort(PacketID.PARTY_SET_LEADER);
		connection.writeInt(id);
		connection.send();
	}
	
	private static void memberJoinedParty(Connection connection, Player joined) {
		connection.writeShort(PacketID.PARTY);
		connection.writeShort(PacketID.PARTY_MEMBER_JOINED);
		connection.writeString(joined.getName());
		connection.writeInt(joined.getStamina());
		connection.writeInt(joined.getMaxStamina());
		connection.writeInt(joined.getMana());
		connection.writeInt(joined.getMaxMana());
		connection.writeInt(joined.getLevel());
		connection.writeInt(joined.getCharacterId());
		connection.writeChar(joined.getClasse().getValue());
		connection.send();
	}
	
	private static void newParty(Player player, Player joined) {
		player.getConnection().writeShort(PacketID.PARTY);
		player.getConnection().writeShort(PacketID.PARTY_NEW);
		player.getConnection().writeBoolean(player.hasInitParty());
		player.getConnection().writeString(joined.getName());
		player.getConnection().writeInt(joined.getStamina());
		player.getConnection().writeInt(joined.getMaxStamina());
		player.getConnection().writeInt(joined.getMana());
		player.getConnection().writeInt(joined.getMaxMana());
		player.getConnection().writeInt(joined.getLevel());
		player.getConnection().writeInt(joined.getCharacterId());
		player.getConnection().writeChar(joined.getClasse().getValue());
		player.getConnection().send();
	}
	
	private static boolean isPartyLeader(Player player, boolean isLeader) {
		if(isLeader) {
			return true;
		}
		else {
			CommandSendMessage.write(player.getPlayerParty().getConnection(), "You are not the party leader.", MessageType.SELF);
			return false;
		}
	}
}
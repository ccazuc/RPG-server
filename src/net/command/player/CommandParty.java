package net.command.player;

import net.Server;
import net.command.Command;
import net.command.chat.CommandPlayerNotFound;
import net.command.chat.CommandSendMessage;
import net.command.chat.MessageType;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.Party;
import net.game.log.Log;
import net.game.manager.IgnoreMgr;
import net.game.unit.Player;
import net.utils.StringUtils;

public class CommandParty extends Command {

	public CommandParty(String name, boolean debug)
	{
		super(name, debug);
	}
	
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
			name = StringUtils.formatPlayerName(name);
			if(!StringUtils.checkPlayerNameLength(name)) {
				CommandPlayerNotFound.write(connection, name);
				return;
			}
			Player member = Server.getInGameCharacterByName(name);
			if(member == null) {
				CommandPlayerNotFound.write(connection, name);
				return;
			}
			if(member.getUnitID() == player.getUnitID()) {
				CommandSendMessage.selfWithoutAuthor(connection, "You can't invite yourself in a party.", MessageType.SELF);
				return;
			}
			if(IgnoreMgr.isIgnored(member.getUnitID(), player.getUnitID())) {
				CommandSendMessage.selfWithoutAuthor(connection, member.getName()+IgnoreMgr.ignoreMessage, MessageType.SELF);
				return;
			}
			if(!(isPartyLeader(player, player.getParty() == null || (player.getParty() != null && player.getParty().isPartyLeader(player))))) {
				return;
			}
			if(member.getParty() != null) {
				CommandSendMessage.selfWithoutAuthor(connection, name.concat(" is already in a party."), MessageType.SELF);
				CommandSendMessage.selfWithAuthor(member.getConnection(), " tried to invite you in a party.", player.getName(), MessageType.SELF);
				return;
			}
			inviteRequest(member.getConnection(), player.getName());
			player.setPlayerParty(member);
			member.setPlayerParty(player);
			player.setHasInitParty(true);
			CommandSendMessage.selfWithoutAuthor(connection, new StringBuilder().append("You invited ").append(name).append(" to join your party.").toString(), MessageType.SELF);
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
				CommandSendMessage.selfWithoutAuthor(connection, "You are not member of a party.", MessageType.SELF);
				return;
			}
			if(!isPartyLeader(player, player.getParty().isPartyLeader(player))) {
				return;
			}
			if(player.getUnitID() == id) {
				CommandSendMessage.selfWithoutAuthor(connection, "You can't kick yourself.", MessageType.SELF);
				return;
			}
			Player member = Server.getInGameCharacter(id);
			if(member == null) {
				CommandSendMessage.selfWithoutAuthor(connection, "Player not found", MessageType.SELF);
				return;
			}
			if(!(member.getParty() == player.getParty())) {
				CommandSendMessage.selfWithoutAuthor(connection, member.getName().concat(" is not a member of your party."), MessageType.SELF);
				return;
			}
			kickPlayer(member);
		}
		else if(packetId == PacketID.PARTY_SET_LEADER) {
			int id = connection.readInt();
			if(player.getParty() == null) {
				CommandSendMessage.selfWithoutAuthor(connection, "You are not member of a party.", MessageType.SELF);
				return;
			}
			if(isPartyLeader(player, player.getParty().isPartyLeader(player))) {
				return;
			}
			if(player.getUnitID() == id) {
				CommandSendMessage.selfWithoutAuthor(connection, "You already are the leader.", MessageType.SELF);
				return;
			}
			Player member = Server.getInGameCharacter(id);
			if(member == null) {
				CommandSendMessage.selfWithoutAuthor(connection, "Player not found", MessageType.SELF);
				return;
			}
			if(!(member.getParty() == player.getParty())) {
				CommandSendMessage.selfWithoutAuthor(connection, member.getName().concat(" is not a member of your party."), MessageType.SELF);
				Log.writePlayerLog(player, "Tried to give leader to someone who's not in his party");
				return;
			}
			int i = 0;
			while(i < player.getParty().getPlayerList().length) {
				if(player.getParty().getPlayerList()[i] != null) {
					if(player.getParty().getPlayerList()[i] == member) {
						setLeader(member.getConnection(), member.getUnitID());
						CommandSendMessage.selfWithoutAuthor(member.getConnection(), "You are now the party leader.", MessageType.SELF);
					}
					else {
						setLeader(player.getParty().getPlayerList()[i].getConnection(), member.getUnitID());
						CommandSendMessage.selfWithoutAuthor(player.getParty().getPlayerList()[i].getConnection(), member.getName().concat(" is now the group leader."), MessageType.SELF);
					}
				}
				i++;
				player.getParty().setLeader(member);
			}
		}
		else if(packetId == PacketID.PARTY_ACCEPT_REQUEST) {
			if(player.getPlayerParty() == null) {
				Log.writePlayerLog(player, "accepted a party request whereas nobody sent one to him");
				CommandSendMessage.selfWithoutAuthor(player.getPlayerParty().getConnection(), "Nobody invited you to join their party.", MessageType.SELF);
				return;
			}
			if(player.getPlayerParty().getParty() == null) { //if player who sent request is already in a party
				if(!player.getPlayerParty().hasInitParty()) {
					System.out.println("CommandParty:PARTY_ACCEPT_REQUEST ERROR.");
					return;
				}
				Party party = new Party(Server.getInGameCharacter(player.getPlayerParty().getUnitID()), player);
				player.getPlayerParty().setParty(party);
				player.setParty(party);
				CommandSendMessage.selfWithoutAuthor(player.getPlayerParty().getConnection(), player.getName().concat(" joined your party."), MessageType.SELF);
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
					if(player.getParty().getPlayerList()[i] != null && player.getParty().getPlayerList()[i].getUnitID() != player.getUnitID()) {
						memberJoinedParty(player.getParty().getPlayerList()[i].getConnection(), player); //send datas to everyone about this.player
						if(!partyInit) {
							newParty(player, player.getParty().getPlayerList()[i]);
							partyInit = true;
						}
						else {
							memberJoinedParty(connection, player.getParty().getPlayerList()[i]); //send datas about everyone to this.player
						}
						CommandSendMessage.selfWithoutAuthor(player.getParty().getPlayerList()[i].getConnection(), player.getName().concat(" joined your party."), MessageType.SELF);
					}
					i++;
				}
			}
		}
	}
	
	private static void inviteRequest(Connection connection, String name) {
		connection.startPacket();
		connection.writeShort(PacketID.PARTY);
		connection.writeShort(PacketID.PARTY_ADD_MEMBER);
		connection.writeString(name);
		connection.endPacket();
		connection.send();
	}
	
	private static void requestDeclined(Connection connection, String name) {
		connection.startPacket();
		connection.writeShort(PacketID.PARTY);
		connection.writeShort(PacketID.PARTY_DECLINE_REQUEST);
		connection.writeString(name);
		connection.endPacket();
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
				if(player.getParty().getPlayerList()[i] != null && player.getParty().getPlayerList()[i].getUnitID() != player.getUnitID()) {
					player.getParty().setLeader(player.getParty().getPlayerList()[i]);
					break;
				}
				i++;
			}
		}
		int i = 0;
		while(i < player.getParty().getPlayerList().length) {
			if(player.getParty().getPlayerList()[i] == player) {
				player.getConnection().startPacket();
				player.getConnection().writeShort(PacketID.PARTY);
				player.getConnection().writeShort(PacketID.PARTY_LEFT);
				player.getConnection().endPacket();
				player.getConnection().send();
				CommandSendMessage.selfWithoutAuthor(player.getConnection(), "You left the party.", MessageType.SELF);
			}
			else if(player.getParty().getPlayerList()[i] != null) {
				player.getParty().getPlayerList()[i].getConnection().startPacket();
				player.getParty().getPlayerList()[i].getConnection().writeShort(PacketID.PARTY);
				player.getParty().getPlayerList()[i].getConnection().writeShort(PacketID.PARTY_MEMBER_LEFT);
				player.getParty().getPlayerList()[i].getConnection().writeInt(player.getUnitID());
				player.getParty().getPlayerList()[i].getConnection().endPacket();
				player.getParty().getPlayerList()[i].getConnection().send();
				CommandSendMessage.selfWithoutAuthor(player.getParty().getPlayerList()[i].getConnection(), player.getName().concat(" left the party."), MessageType.SELF);
				if(wasLeader) {
					setLeader(player.getParty().getPlayerList()[i].getConnection(), player.getUnitID());
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
				player.getParty().getPlayerList()[i].getConnection().startPacket();
				player.getParty().getPlayerList()[i].getConnection().writeShort(PacketID.PARTY);
				player.getParty().getPlayerList()[i].getConnection().writeShort(PacketID.PARTY_DISBAND);
				player.getParty().getPlayerList()[i].getConnection().endPacket();
				player.getParty().getPlayerList()[i].getConnection().send();
				System.out.println(player.getConnection().wBufferRemaining()+" remaining, capacity: "+player.getConnection().wBufferCapacity()+" "+player.getConnection());
				CommandSendMessage.selfWithoutAuthor(player.getParty().getPlayerList()[i].getConnection(), "You left the party.", MessageType.SELF);
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
		connection.startPacket();
		connection.writeShort(PacketID.PARTY);
		connection.writeShort(PacketID.PARTY_SET_LEADER);
		connection.writeInt(id);
		connection.endPacket();
		connection.send();
	}
	
	private static void memberJoinedParty(Connection connection, Player joined) {
		connection.startPacket();
		connection.writeShort(PacketID.PARTY);
		connection.writeShort(PacketID.PARTY_MEMBER_JOINED);
		connection.writeString(joined.getName());
		connection.writeInt(joined.getStamina());
		connection.writeInt(joined.getMaxStaminaEffective());
		connection.writeInt(joined.getMana());
		connection.writeInt(joined.getMaxManaEffective());
		connection.writeInt(joined.getLevel());
		connection.writeInt(joined.getUnitID());
		connection.writeByte(joined.getClasse().getValue());
		connection.endPacket();
		connection.send();
	}
	
	private static void newParty(Player player, Player joined) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.PARTY);
		player.getConnection().writeShort(PacketID.PARTY_NEW);
		player.getConnection().writeBoolean(player.hasInitParty());
		player.getConnection().writeString(joined.getName());
		player.getConnection().writeInt(joined.getStamina());
		player.getConnection().writeInt(joined.getMaxStaminaEffective());
		player.getConnection().writeInt(joined.getMana());
		player.getConnection().writeInt(joined.getMaxManaEffective());
		player.getConnection().writeInt(joined.getLevel());
		player.getConnection().writeInt(joined.getUnitID());
		player.getConnection().writeByte(joined.getClasse().getValue());
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	private static boolean isPartyLeader(Player player, boolean isLeader) {
		if(isLeader) {
			return true;
		}
		else {
			CommandSendMessage.selfWithoutAuthor(player.getPlayerParty().getConnection(), "You are not the party leader.", MessageType.SELF);
			return false;
		}
	}
}

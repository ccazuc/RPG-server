package net.command;

import net.Server;
import net.command.chat.CommandPlayerNotFound;
import net.command.chat.CommandSendMessage;
import net.command.chat.MessageType;
import net.connection.Connection;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.Party;
import net.game.Player;

public class CommandParty extends Command {
	
	public CommandParty(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	@Override
	public void read() {
		byte packetId = this.connection.readByte();
		if(packetId == PacketID.PARTY_ADD_MEMBER) {
			String name = this.connection.readString();
			name = name.substring(0, 1).toUpperCase()+name.substring(1).toLowerCase();
			Player player = Server.getInGameCharacter(name);
			if(player != null) {
				if(player.getCharacterId() != this.player.getCharacterId()) {
					if(this.player.getParty() == null || (this.player.getParty() != null && this.player.getParty().isPartyLeader(this.player))) {
						if(player.getParty() == null) {
							inviteRequest(player.getConnection(), this.player.getName());
							this.player.setPlayerParty(player);
							player.setPlayerParty(this.player);
							this.player.setHasInitParty(true);
							CommandSendMessage.write(this.connection, "You invited "+name+" to join your party.", MessageType.SELF);
						}
						else {
							CommandSendMessage.write(this.connection, name+" is already in a party.", MessageType.SELF);
							CommandSendMessage.write(player.getConnection(), this.player.getName()+" tried to invite you in a party.", MessageType.SELF);
						}
					}
					else {
						//System.out.println(this.player.getParty().isPartyLeader(this.player)+" "+this.player.getParty().isPartyLeader(this.player.getPlayerParty()));
						CommandSendMessage.write(this.connection, "You are not the party leader.", MessageType.SELF);
					}
				}
				else {
					CommandSendMessage.write(this.connection, "You can't invite yourself in a party.", MessageType.SELF);
				}
			}
			else {
				CommandPlayerNotFound.write(this.connection, name);
			}
		}
		else if(packetId == PacketID.PARTY_DECLINE_REQUEST) {
			requestDeclined(this.player.getPlayerParty().getConnection(), this.player.getName());
			this.player.getPlayerParty().setParty(null);
			this.player.setPlayerParty(null);
		}
		else if(packetId == PacketID.PARTY_ACCEPT_REQUEST) {
			if(this.player.getPlayerParty().getParty() == null) { //if player who sent request is already in a party
				if(this.player.getPlayerParty().hasInitParty()) {
					Party party = new Party(Server.getInGameCharacter(this.player.getPlayerParty().getCharacterId()), this.player);
					this.player.getPlayerParty().setParty(party);
					this.player.setParty(party);
					CommandSendMessage.write(this.player.getPlayerParty().getConnection(), this.player.getName()+" joined your party.", MessageType.SELF);
					newParty(this.player, this.player.getPlayerParty());
					newParty(this.player.getPlayerParty(), this.player);
					this.player.getPlayerParty().setPlayerParty(null);
					this.player.setPlayerParty(null);
				}
				else {
					//System.out.println(this.player.hasInitParty()+" "+this.player.getPlayerParty().hasInitParty());
					System.out.println("CommandParty:PARTY_ACCEPT_REQUEST ERROR.");
				}
			}
			else {
				if(this.player.getPlayerParty().getParty().isPartyLeader(this.player.getPlayerParty())) {
					this.player.getPlayerParty().getParty().addMember(this.player);
					this.player.setParty(this.player.getPlayerParty().getParty());
					int i = 0;
					boolean partyInit = false;
					while(i < this.player.getParty().getPlayerList().length) {
						if(this.player.getParty().getPlayerList()[i] != null && this.player.getParty().getPlayerList()[i].getCharacterId() != this.player.getCharacterId()) {
							memberJoinedParty(this.player.getParty().getPlayerList()[i].getConnection(), this.player); //send datas to everyone about this.player
							if(!partyInit) {
								newParty(this.player, this.player.getParty().getPlayerList()[i]);
								partyInit = true;
							}
							else {
								memberJoinedParty(this.connection, this.player.getParty().getPlayerList()[i]); //send datas about everyone to this.player
							}
							CommandSendMessage.write(this.player.getParty().getPlayerList()[i].getConnection(), this.player.getName()+" joined your party.", MessageType.SELF);
						}
						i++;
					}
				}
				else {
					//System.out.println(this.player.getParty().isPartyLeader(this.player)+" "+this.player.getParty().isPartyLeader(this.player.getPlayerParty()));
					CommandSendMessage.write(this.player.getPlayerParty().getConnection(), "You are not the party leader.", MessageType.SELF);
				}
			}
		}
	}
	
	private static void inviteRequest(Connection connection, String name) {
		connection.writeByte(PacketID.PARTY);
		connection.writeByte(PacketID.PARTY_ADD_MEMBER);
		connection.writeString(name);
		connection.send();
	}
	
	private static void requestDeclined(Connection connection, String name) {
		connection.writeByte(PacketID.PARTY);
		connection.writeByte(PacketID.PARTY_DECLINE_REQUEST);
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
				player.getConnection().writeByte(PacketID.PARTY);
				player.getConnection().writeByte(PacketID.PARTY_LEFT);
				player.getConnection().send();
				CommandSendMessage.write(player.getConnection(), "You left the party.", MessageType.SELF);
			}
			else if(player.getParty().getPlayerList()[i] != null) {
				player.getParty().getPlayerList()[i].getConnection().writeByte(PacketID.PARTY);
				player.getParty().getPlayerList()[i].getConnection().writeByte(PacketID.PARTY_MEMBER_LEFT);
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
				player.getParty().getPlayerList()[i].getConnection().writeByte(PacketID.PARTY);
				player.getParty().getPlayerList()[i].getConnection().writeByte(PacketID.PARTY_DISBAND);
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
		connection.writeByte(PacketID.PARTY);
		connection.writeByte(PacketID.PARTY_SET_LEADER);
		connection.writeInt(id);
		connection.send();
	}
	
	private static void memberJoinedParty(Connection connection, Player joined) {
		connection.writeByte(PacketID.PARTY);
		connection.writeByte(PacketID.PARTY_MEMBER_JOINED);
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
		player.getConnection().writeByte(PacketID.PARTY);
		player.getConnection().writeByte(PacketID.PARTY_NEW);
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
}

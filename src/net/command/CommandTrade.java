package net.command;

import net.Server;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.Player;

public class CommandTrade extends Command {
	
	public CommandTrade(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	@Override
	public void read() {
		byte packetID = this.connection.readByte();
		if(packetID == PacketID.TRADE_NEW) { //declare a new trade
			int traded = this.connection.readInt();
			Player trade = Server.getCharacter(traded);
			//System.out.println(trade.getName()+" "+this.player.getPlayerTrade()+" "+trade.getPlayerTrade());
			if(this.player.getPlayerTrade() == null && trade.getPlayerTrade() == null) { //players are not trading
				trade.setPlayerTrade(this.player);
				this.player.setPlayerTrade(trade);
				write(PacketID.TRADE_REQUEST, trade, this.player.getName());
			}
			else {
				//add red alert player busy
			}
		}
		else if(packetID == PacketID.TRADE_NEW_CONFIRM) { //confirm the trade
			this.player.getPlayerTrade().getConnection().writeByte(PacketID.TRADE);
			this.player.getPlayerTrade().getConnection().writeByte(PacketID.TRADE_NEW_CONFIRM);
			this.player.getPlayerTrade().getConnection().send();
		}
		else if(packetID == PacketID.TRADE_ADD_ITEM) { //add an item on trade's frame
			this.player.setHasAcceptedTrade(false);
			int id = this.connection.readInt();
			int slot = this.connection.readInt();
			if(this.connection.readBoolean()) {
				if(this.player.getPlayerTrade().itemHasBeenSendToClient(id)) {
					sendKnownItem(this.player.getPlayerTrade(), id, slot, this.connection.readInt(), this.connection.readInt(), this.connection.readInt());
				}
				else {
					
				}
			}
			else {
				if(this.player.getPlayerTrade().itemHasBeenSendToClient(id)) {
					sendKnownItem(this.player.getPlayerTrade(), id, slot);
				}
				else {
					
				}
			}
		}
		else if(packetID == PacketID.TRADE_ACCEPT) { //lock the trade
			this.player.getPlayerTrade().getConnection().writeByte(PacketID.TRADE);
			this.player.getPlayerTrade().getConnection().writeByte(PacketID.TRADE_ACCEPT);
			this.player.getPlayerTrade().getConnection().send();
		}
		else if(packetID == PacketID.TRADE_CLOSE) { //cancel the trade
			this.player.getPlayerTrade().setPlayerTrade(null);
			this.player.setPlayerTrade(null);
			tradeCancel(this.player);
			tradeCancel(this.player.getPlayerTrade());
		}
	}
	
	private static void write(byte packetId, Player player, String name) {
		player.getConnection().writeByte(PacketID.TRADE);
		player.getConnection().writeByte(packetId);
		player.getConnection().writeString(name);
		player.getConnection().send();
	}
	
	private static void sendKnownItem(Player player, int id, int slot) {
		player.getConnection().writeByte(PacketID.TRADE);
		player.getConnection().writeByte(PacketID.KNOWN_ITEM);
		player.getConnection().writeInt(id);
		player.getConnection().writeInt(slot);
		player.getConnection().writeBoolean(false);
		player.getConnection().send();
	}
	
	private static void sendKnownItem(Player player, int id, int slot, int gem1Id, int gem2Id, int gem3Id) {
		player.getConnection().writeByte(PacketID.TRADE);
		player.getConnection().writeByte(PacketID.KNOWN_ITEM);
		player.getConnection().writeInt(id);
		player.getConnection().writeInt(slot);
		player.getConnection().writeBoolean(true);
		player.getConnection().writeInt(gem1Id);
		player.getConnection().writeInt(gem2Id);
		player.getConnection().writeInt(gem3Id);
		player.getConnection().send();
	}
	
	private static void tradeCancel(Player player) {
		player.getConnection().writeByte(PacketID.TRADE);
		player.getConnection().writeByte(PacketID.TRADE_CLOSE);
		player.getConnection().send();
	}
}

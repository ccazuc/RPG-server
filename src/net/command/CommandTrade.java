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
		if(packetID == PacketID.TRADE_NEW) {
			int traded = this.connection.readInt();
			Player trade = Server.getCharacter(traded);
			System.out.println(trade.getName()+" "+this.player.getPlayerTradeId()+" "+trade.getPlayerTradeId());
			if(this.player.getPlayerTradeId() == 0 && trade.getPlayerTradeId() == 0) {
				trade.setPlayerTradeId(this.player.getCharacterId());
				this.player.setPlayerTradeId(traded);
				write(PacketID.TRADE_REQUEST, trade, this.player.getName());
			}
			else {
				//add red alert player busy
			}
		}
		else if(packetID == PacketID.TRADE_NEW_CONFIRM) {
			Player trade = Server.getCharacter(this.player.getPlayerTradeId());
			trade.getConnection().writeByte(PacketID.TRADE);
			trade.getConnection().writeByte(PacketID.TRADE_NEW_CONFIRM);
			trade.getConnection().send();
		}
		else if(packetID == PacketID.TRADE_ADD_ITEM) {
			
		}
		else if(packetID == PacketID.TRADE_ACCEPT) {
			
		}
	}
	
	public static void write(byte packetId, Player player, String name) {
		player.getConnection().writeByte(PacketID.TRADE);
		player.getConnection().writeByte(packetId);
		System.out.println(packetId);
		player.getConnection().writeString(name);
		player.getConnection().send();
	}
}

package net.command.player;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.aura.AppliedAura;
import net.game.unit.Player;

public class CommandAura extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		short packetId = connection.readShort();
		if(packetId == PacketID.AURA_CANCEL) {
			
		}
	}
	
	public static void sendAura(Player player, int unitID, AppliedAura aura) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.AURA);
		player.getConnection().writeShort(PacketID.AURA_SEND);
		player.getConnection().writeInt(unitID);
		player.getConnection().writeInt(aura.getAura().getId());
		player.getConnection().writeLong(aura.getEndTimer());
		player.getConnection().writeByte(aura.getNumberStack());
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	public static void updateStack(Player player, int unitID, AppliedAura aura) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.AURA);
		player.getConnection().writeShort(PacketID.AURA_UPDATE_STACK);
		player.getConnection().writeInt(unitID);
		player.getConnection().writeInt(aura.getAura().getId());
		player.getConnection().writeByte(aura.getNumberStack());
		player.getConnection().endPacket();
		player.getConnection().send();
	}
}

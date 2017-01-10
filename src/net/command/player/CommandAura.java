package net.command.player;

import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.aura.AppliedAura;
import net.game.aura.Aura;
import net.game.aura.AuraMgr;
import net.game.aura.AuraRemoveList;
import net.game.log.Log;
import net.game.unit.Player;

public class CommandAura extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		short packetId = connection.readShort();
		if(packetId == PacketID.AURA_CANCEL) {
			int auraID = connection.readInt();
			System.out.println("Aura ID : "+auraID);
			Aura aura = AuraMgr.getAura(auraID);
			if(aura == null) {
				Log.writePlayerLog(player, "Tried to remove a non-existing aura, auraID : "+auraID);
				return;
			}
			if(!aura.isVisible()) {
				Log.writePlayerLog(player, "Tried to remove a non-visible, auraID : "+auraID);
				return;
			}
			if(!aura.isBuff()) {
				Log.writePlayerLog(player, "Tried to remove a debuff aura, auraID : "+auraID);
				return;
			}
			if(!player.removeAura(auraID, AuraRemoveList.CANCEL)) {
				Log.writePlayerLog(player, "Tried to remove the aura "+auraID+" whereas he's not affected by it.");
			}
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
	
	public static void updateAura(Player player, int unitID, AppliedAura aura) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.AURA);
		player.getConnection().writeShort(PacketID.AURA_UPDATE);
		player.getConnection().writeInt(unitID);
		player.getConnection().writeInt(aura.getAura().getId());
		player.getConnection().writeLong(aura.getEndTimer());
		player.getConnection().writeByte(aura.getNumberStack());
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	public static void removeAura(Player player, int unitID, int auraID) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.AURA);
		player.getConnection().writeShort(PacketID.AURA_CANCEL);
		player.getConnection().writeInt(unitID);
		player.getConnection().writeInt(auraID);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
}

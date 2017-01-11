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
import net.game.unit.Unit;

public class CommandAura extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		short packetId = connection.readShort();
		if(packetId == PacketID.AURA_CANCEL) {
			int auraID = connection.readInt();
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
	
	public static void initAura(Player player, Unit unit) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.AURA);
		player.getConnection().writeShort(PacketID.AURA_INIT);
		int i = 0;
		player.getConnection().writeInt(unit.getUnitID());
		player.getConnection().writeShort((short)unit.getAuraList().size());
		while(i < unit.getAuraList().size()) {
			player.getConnection().writeInt(unit.getAuraList().get(i).getAura().getId());
			player.getConnection().writeLong(unit.getAuraList().get(i).getEndTimer());
			player.getConnection().writeByte(unit.getAuraList().get(i).getNumberStack());
			i++;
		}
		player.getConnection().endPacket();
		player.getConnection().send();
	}
}

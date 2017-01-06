package net.command.player;

import net.command.Command;
import net.connection.PacketID;
import net.game.unit.Player;

public class CommandSendSpellCD extends Command {

	
	public static void sendCD(Player player, int spellID, int cdLength, long startTimer) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.SEND_SPELL_CD);
		player.getConnection().writeInt(spellID);
		player.getConnection().writeInt(cdLength);
		player.getConnection().writeLong(startTimer);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
}

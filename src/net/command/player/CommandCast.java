package net.command.player;

import net.Server;
import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.DefaultRedAlert;
import net.game.Player;
import net.game.spell.Spell;
import net.game.spell.SpellManager;

public class CommandCast extends Command {

	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		short packetId = connection.readShort();
		if(packetId == PacketID.SPELL_CAST_REQUEST) {
			int id = connection.readInt();
			if(player.getStamina() <= 0) {
				return;
			}
			Spell spell = SpellManager.getBookSpell(id);
			if(spell == null) {
				player.close();
				return;
			}
			/*if(!player.hasUnlockedSpell(id)) {
			 	player.close();
				return;
			}*/
			if(player.isCasting()) {
				CommandSendRedAlert.write(player, DefaultRedAlert.ALREADY_CASTING);
				return;
			}
			if(player.getGCDEndTimer() > Server.getLoopTickTimer()) { 
				CommandSendRedAlert.write(player, DefaultRedAlert.ALREADY_CASTING);
			}
			if(spell.getCastTime() == 0) {
				spell.action(player, player.getTarget());
				player.setGCDStartTimer(Server.getLoopTickTimer());
				player.setGCDEndTimer(Server.getLoopTickTimer()+1500);
				CommandSendGCD.sendGCD(player, player.getGCDStartTimer(), player.getGCDEndTimer());
				return;
			}
			player.cast(spell);
			write(player, id, Server.getLoopTickTimer());
			/*if(!player.canCastSpell()) {
				return;
			}*/
		}
	}
	
	public static void write(Player player, int spellID, long startCastTimer) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.SPELL_CAST);
		player.getConnection().writeShort(PacketID.SPELL_CAST_START);
		player.getConnection().writeInt(spellID);
		player.getConnection().writeLong(startCastTimer);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
}

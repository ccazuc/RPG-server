package net.command.player;

import net.Server;
import net.command.Command;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.DefaultRedAlert;
import net.game.Player;
import net.game.Unit;
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
			Spell spell = SpellManager.getSpell(id);
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
				CommandSendRedAlert.write(player, DefaultRedAlert.SPELL_NOT_READY_YET);
				return;
			}
			if(player.getSpellCD(id) > Server.getLoopTickTimer()) {
				CommandSendRedAlert.write(player, DefaultRedAlert.SPELL_NOT_READY_YET);
				return;
			}
			if(spell.getCastTime() == 0) {
				//spell.action(player, player.getTarget());
				if(spell.triggerGCD()) {
					CommandSendGCD.sendGCD(player, Server.getLoopTickTimer(), Server.getLoopTickTimer()+Unit.GCD);
					player.startGCD(Server.getLoopTickTimer());
				}
				return;
			}
			player.cast(spell);
			cast(player, id, Server.getLoopTickTimer(), spell.getCastTime());
			player.setSpellCD(id, Server.getLoopTickTimer()+spell.getSpellBaseCd());
			CommandSendSpellCD.sendCD(player, id, spell.getSpellBaseCd(), Server.getLoopTickTimer());
			/*if(!player.canCastSpell()) {
				return;
			}*/
		}
	}
	
	public static void cast(Player player, int spellID, long startCastTimer, int castLength) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.SPELL_CAST);
		player.getConnection().writeShort(PacketID.SPELL_CAST_START);
		player.getConnection().writeInt(spellID);
		player.getConnection().writeLong(startCastTimer);
		player.getConnection().writeInt(castLength);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
}

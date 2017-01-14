package net.command.player.spell;

import net.connection.PacketID;
import net.game.spell.Spell;
import net.game.unit.Player;

public class CommandSpellUnlocked {

	public static void addUnlockedSpell(Player player, int spellID) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.SPELL_UNLOCKED);
		player.getConnection().writeShort(PacketID.SPELL_UNLOCKED_ADD);
		player.getConnection().writeInt(spellID);
		player.getConnection().endPacket();
		player.getConnection().send();
	}

	public static void removeUnlockedSpell(Player player, int spellID) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.SPELL_UNLOCKED);
		player.getConnection().writeShort(PacketID.SPELL_UNLOCKED_REMOVE);
		player.getConnection().writeInt(spellID);
		player.getConnection().endPacket();
		player.getConnection().send();
	}
	
	public static void initSpellUnlocked(Player player) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.SPELL_UNLOCKED);
		player.getConnection().writeShort(PacketID.SPELL_UNLOCKED_INIT);
		player.getConnection().writeShort((short)player.getSpellUnlocked().size());
		for(Spell spell : player.getSpellUnlocked().values()) {
			player.getConnection().writeInt(spell.getSpellId());
		}
		player.getConnection().endPacket();
		player.getConnection().send();
	}
}

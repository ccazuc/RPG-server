package net.command.player;

import net.command.Command;
import net.connection.PacketID;
import net.game.unit.Player;
import net.game.unit.Unit;
import net.game.unit.UnitType;

public class CommandSendTarget extends Command {

	@Override
	public void read(Player player) {
		Unit unit = new Unit(UnitType.NPC, 5, 10000, 10000, 5000, 5000, 5, "TestTarget", 1000, 20, 50, 500, 1000);
		player.setTarget(unit);
		sendTarget(player, unit);
	}
	
	public static void sendTarget(Player player, Unit unit) {
		player.getConnection().startPacket();
		player.getConnection().writeShort(PacketID.SEND_TARGET);
		player.getConnection().writeInt(unit.getid());
		player.getConnection().writeInt(unit.getStamina());
		player.getConnection().writeInt(unit.getMana());
		player.getConnection().writeString(unit.getName());
		player.getConnection().writeInt(unit.getLevel());
		player.getConnection().endPacket();
		player.getConnection().send();
	}
}

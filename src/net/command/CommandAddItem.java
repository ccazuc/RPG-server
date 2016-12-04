package net.command;

import net.Server;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.Player;
import net.game.item.Item;
import net.game.item.bag.Container;
import net.game.item.gem.Gem;
import net.game.item.potion.Potion;
import net.game.item.stuff.Stuff;

public class CommandAddItem extends Command {
	
	public CommandAddItem() {}
	
	@Override
	public void read(Player player) {
		Connection connection = player.getConnection();
		int character_id = connection.readInt();
		int item_id = connection.readInt();
		int number = connection.readInt();
		if(!(player.getAccountRank() >= 1 && Item.exists(item_id))) {
			return;
		}
		Player member = character_id == player.getCharacterId() ? player : Server.getInGameCharacter(character_id);
		if(member == null) {
			return;
		}
		if(member.itemHasBeenSendToClient(item_id)) {
			writeKnownItem(member, item_id, number);
		}
		else {
			writeUnknownItem(member, item_id, number);
		}
	}
	
	private static void writeKnownItem(Player player, int id, int number) {
		player.getConnection().writeByte(PacketID.ADD_ITEM);
		player.getConnection().writeByte(PacketID.KNOWN_ITEM);
		player.getConnection().writeInt(id);
		player.getConnection().writeInt(number);
		player.getConnection().send();
	}
	
	private static void writeUnknownItem(Player player, int id, int number) {
		Item temp = Item.getItem(id);
		player.getConnection().writeByte(PacketID.ADD_ITEM);
		player.getConnection().writeByte(PacketID.UNKNOWN_ITEM);
		player.getConnection().writeInt(number);
		player.getConnection().writeChar(temp.getItemType().getValue());
		if(temp.isContainer()) {
			player.getConnection().writeContainer((Container)temp);
		}
		else if(temp.isGem()) {
			player.getConnection().writeGem((Gem)temp);
		}
		else if(temp.isPotion()) {
			player.getConnection().writePotion((Potion)temp);
		}
		else if(temp.isStuff()) {
			player.getConnection().writeStuff((Stuff)temp);
		}
		else if(temp.isWeapon()) {
			player.getConnection().writeWeapon((Stuff)temp);
		}
		player.getConnection().send();
	}
}

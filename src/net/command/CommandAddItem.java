package net.command;

import net.Servers;
import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.Player;
import net.game.item.Item;
import net.game.item.bag.Container;
import net.game.item.gem.Gem;
import net.game.item.potion.Potion;
import net.game.item.stuff.Stuff;

public class CommandAddItem extends Command {
	
	public CommandAddItem(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	@Override
	public void read() {
		int character_id = this.connection.readInt();
		int item_id = this.connection.readInt();
		int number = this.connection.readInt();
		if(this.player.getAccountRank() >= 1 && Item.exists(item_id)) {
			Player player = character_id == this.player.getCharacterId() ? this.player : Servers.getCharacter(character_id);
			if(player != null) {
				if(player.itemHasBeenSendToClient(item_id)) {
					writeKnownItem(player, item_id, number);
				}
				else {
					writeUnknownItem(player, item_id, number);
				}
			}
		}
	}
	
	private void writeKnownItem(Player player, int id, int number) {
		player.getConnection().writeByte(PacketID.ADD_ITEM);
		player.getConnection().writeByte(PacketID.KNOWN_ITEM);
		player.getConnection().writeInt(id);
		player.getConnection().writeInt(number);
		player.getConnection().send();
	}
	
	private void writeUnknownItem(Player player, int id, int number) {
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

package net.command;

import java.sql.SQLException;

import net.connection.ConnectionManager;
import net.connection.PacketID;
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
		byte packetID = this.connection.readByte();
		int id = this.connection.readInt();
		int number = this.connection.readInt();
		if(packetID == PacketID.KNOWN_ITEM) {
			if(Item.exists(id)) {
				try {
					if(this.player.addItem(Item.getItem(id), number)) {
						writeKnownItem(id, number);
					}
				} 
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		else if(packetID == PacketID.UNKNOWN_ITEM) {
			if(Item.exists(id)) {
				try {
					if(this.player.addItem(Item.getItem(id), number));
					writeUnknownItem(id, number);
				}
				catch(SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void writeKnownItem(int id, int number) {
		this.connection.writeByte(PacketID.ADD_ITEM);
		this.connection.writeByte(PacketID.KNOWN_ITEM);
		this.connection.writeInt(id);
		this.connection.writeInt(number);
		this.connection.send();
	}
	
	private void writeUnknownItem(int id, int number) {
		Item temp = Item.getItem(id);
		this.connection.writeByte(PacketID.ADD_ITEM);
		this.connection.writeByte(PacketID.UNKNOWN_ITEM);
		this.connection.writeInt(number);
		this.connection.writeChar(temp.getItemType().getValue());
		if(temp.isContainer()) {
			this.connection.writeContainer((Container)temp);
		}
		else if(temp.isGem()) {
			this.connection.writeGem((Gem)temp);
		}
		else if(temp.isPotion()) {
			this.connection.writePotion((Potion)temp);
		}
		else if(temp.isStuff()) {
			this.connection.writeStuff((Stuff)temp);
		}
		else if(temp.isWeapon()) {
			this.connection.writeWeapon((Stuff)temp);
		}
		this.connection.send();
	}
}

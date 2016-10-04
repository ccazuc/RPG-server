package net.command;

import java.sql.SQLException;

import net.connection.ConnectionManager;
import net.connection.PacketID;
import net.game.item.Item;

public class CommandAddItem extends Command {
	
	public CommandAddItem(ConnectionManager connectionManager) {
		super(connectionManager);
	}
	
	@Override
	public void read() {
		int id = this.connection.readInt();
		int number = this.connection.readInt();
		if(Item.exists(id)) {
			Item temp = Item.getItem(id);
			if(number == 1) {
				try {
					if(this.player.addItem(temp, number)) {
						write(id, number);
					}
				} 
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
			else if(number > 1) {
				try {
					if(temp.isStackable()) {
						if(this.player.addItem(temp, number)) {
							write(id, number);
						}
					}
					else {
						if(this.player.addMultipleUnstackableItem(id, number)) {
							write(id, number);
						}
					}
				}
				catch(SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void write(int id, int number) {
		this.connection.writeByte(PacketID.ADD_ITEM);
		this.connection.writeByte(PacketID.ADD_ITEM_CONFIRMED);
		this.connection.writeInt(id);
		this.connection.writeInt(number);
		this.connection.send();
	}
}

package net.game.item;

public enum DragItem {

	BAG((byte)0),
	INVENTORY((byte)1),
	BANK((byte)2),
	GUILDBANK((byte)3);
	
	private byte value;
	
	private DragItem(byte c) {
		this.value = c;
	}
	
	public byte getValue() {
		return this.value;
	}
	
	public static DragItem getValue(byte c) {
		if(c > 0 && c <= DragItem.values().length) {
			return DragItem.values()[c];
		}
		return null;
	}
}

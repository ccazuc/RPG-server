package net.game.item;

public enum DragItem {

	BAG((char)0),
	INVENTORY((char)1),
	BANK((char)2),
	GUILDBANK((char)3);
	
	private char value;
	
	private DragItem(char c) {
		this.value = c;
	}
	
	public char getValue() {
		return this.value;
	}
	
	public static DragItem getValue(char c) {
		if(c > 0 && c <= DragItem.values().length) {
			return DragItem.values()[c];
		}
		return null;
	}
}

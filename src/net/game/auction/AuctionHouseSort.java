package net.game.auction;

public enum AuctionHouseSort {

	ERROR((byte)0),
	RARITY_ASCENDING((byte)1),
	RARITY_DESCENDING((byte)2),
	LEVEL_ASCENDING((byte)3),
	LEVEL_DESCENDING((byte)4),
	TIME_LEFT_ASCENDING((byte)5),
	TIME_LEFT_DESCENDING((byte)6),
	VENDOR_ASCENDING((byte)7),
	VENDOR_DESCENDING((byte)8),
	BID_ASCENDING((byte)9),
	BID_DESCENDING((byte)10),
	
	;
	
	private final byte value;
	
	private AuctionHouseSort(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}
	
	public static AuctionHouseSort getSort(byte value) {
		if(value >= 0 && value < values().length) {
			return values()[value];
		}
		return ERROR;
	}
}

package net.game.auction;

public enum AuctionHouseSort {

	RARITY_ASCENDING((byte)0),
	RARITY_DESCENDING((byte)1),
	LEVEL_ASCENDING((byte)2),
	LEVEL_DESCENDING((byte)3),
	TIME_LEFT_ASCENDING((byte)4),
	TIME_LEFT_DESCENDING((byte)5),
	VENDOR_ASCENDING((byte)6),
	VENDOR_DESCENDING((byte)7),
	BID_ASCENDING((byte)8),
	BID_DESCENDING((byte)9),
	
	;
	
	private final byte value;
	
	private AuctionHouseSort(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}
}

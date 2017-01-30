package net.game.auction;

public enum AuctionHouseQualityFilter {

	ALL((byte)0),
	POOR((byte)1),
	COMMON((byte)2),
	UNCOMMON((byte)3),
	RARE((byte)4),
	LEGENDARY((byte)5),
	
	;
	
	private final byte value;
	
	private AuctionHouseQualityFilter(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}
}

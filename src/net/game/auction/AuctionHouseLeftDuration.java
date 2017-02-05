package net.game.auction;

public enum AuctionHouseLeftDuration {

	SHORT((byte)0, 0),		//Less than 30 minutes
	MEDIUM((byte)1, 1800000),	//Between 30 minutes and 2 hours
	LONG((byte)2, 7200000),		//Between 2 hours and 12 hours
	VERY_LONG((byte)3, 43200000),	//Greater than 12 hours
	
	;
	
	private final byte value;
	private final int duration;
	
	private AuctionHouseLeftDuration(byte value, int duration) {
		this.value = value;
		this.duration = duration;
	}
	
	public int getDuration() {
		return this.duration;
	}
	
	public byte getValue() {
		return this.value;
	}
	
	public static AuctionHouseLeftDuration getDuration(int timeLeft) {
		if(timeLeft >= VERY_LONG.getDuration()) {
			return VERY_LONG;
		}
		if(timeLeft >= LONG.getDuration()) {
			return LONG;
		}
		if(timeLeft >= MEDIUM.getDuration()) {
			return MEDIUM;
		}
		return SHORT;
	}
}

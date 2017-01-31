package net.game.auction;

public enum AuctionHouseDuration {

	ERROR((byte)0, 0, 0),
	NORMAL((byte)1, .15f, 43200000),	//12 hours
	LONG((byte)2, .3f, 86400000),		//24 hours
	VERY_LONG((byte)3, .6f, 172800000),	//48 hours
	
	;
	
	private final byte value;
	private final float feesCoefficient;
	private final int duration;
	
	private AuctionHouseDuration(byte value, float f, int duration) {
		this.value = value;
		this.feesCoefficient = f;
		this.duration = duration;
	}
	
	public float getCoefficient() {
		return this.feesCoefficient;
	}
	
	public int getDuration() {
		return this.duration;
	}
	
	public byte getValue() {
		return this.value;
	}
	
	public static AuctionHouseDuration getDuration(byte value) {
		if(value >= 0 && value < values().length) {
			return values()[value];
		}
		return ERROR;
	}
	
	public static AuctionHouseDuration getDuration(int timeLeft) {
		if(timeLeft >= VERY_LONG.duration) {
			return VERY_LONG;
		}
		if(timeLeft >= LONG.duration) {
			return LONG;
		}
		return NORMAL;
	}
}

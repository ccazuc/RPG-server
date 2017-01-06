package net.game.unit;

import net.game.Party;

public enum TargetType {

	ARENA((byte)5),
	ARENA_PET((byte)5),
	FOCUS((byte)1),
	MOUSEOVER((byte)1),
	PARTY((byte)(Party.MAXIMUM_PARTY_SIZE-1)),
	PARTY_PET((byte)(Party.MAXIMUM_PARTY_SIZE-1)),
	PET((byte)1),
	PLAYER((byte)1),
	RAID((byte)40),
	RAID_PET((byte)40),
	TARGET((byte)1),
	VEHICULE((byte)1),
	
	;
	private byte maxIndex;
	
	private TargetType(byte maxIndex) {
		this.maxIndex = maxIndex;
	}
	
	public byte getMaxIndex() {
		return this.maxIndex;
	}
}

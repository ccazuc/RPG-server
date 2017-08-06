package net.game.ai;

import net.game.unit.ClassType;
import net.game.unit.Unit;

public class AIMgr {
	
	public static AI getAI(Unit unit) {
		ClassType type = unit.getClassType();
		if (type == ClassType.DRUID) {
			
		}
		if (type == ClassType.PRIEST) {
			return new PriestAI(unit);
		}
		return new PriestAI(unit);
	}
}

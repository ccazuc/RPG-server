package net.game.aura;

public enum AuraEffect {

	REDUCE_STAMINA((byte)0),
	REDUCE_MAX_STAMINA((byte)1),
	REDUCE_MANA((byte)2),
	REDUCE_MAX_MANA((byte)3),
	REDUCE_ARMOR((byte)4),
	REDUCE_CARAC((byte)5),
	REDUCE_STRENGTH((byte)6),
	REDUCE_AGILITY((byte)7),
	REDUCE_ATTACK_POWER((byte)8),
	REDUCE_INTELLIGENCE((byte)9),
	REDUCE_SPELL_POWER((byte)10),
	REDUCE_ATTACK_SPEED((byte)11),
	REDUCE_HASTE((byte)12),
	REDUCE_SPELL_HASTE((byte)13),
	REDUCE_CRITICAL((byte)14),
	REDUCE_SPELL_CRITICAL((byte)15),
	REDUCE_HEALING_POWER((byte)16),
	REDUCE_HEALING_TAKEN((byte)17),
	INCREASE_STAMINA((byte)18),
	INCREASE_MAX_STAMINA((byte)19),
	INCREASE_STRENGTH((byte)20),
	INCREASE_AGILITY((byte)21),
	INCREASE_MANA((byte)22),
	INCREASE_MAX_MANA((byte)23),
	INCREASE_ARMOR((byte)24),
	INCREASE_CARAC((byte)25),
	INCREASE_ATTACK_POWER((byte)26),
	INCREASE_INTELLIGENCE((byte)27),
	INCREASE_SPELL_POWER((byte)28),
	INCREASE_ATTACK_SPEED((byte)29),
	INCREASE_HASTE((byte)30),
	INCREASE_SPELL_HASTE((byte)31),
	INCREASE_CRITICAL((byte)32),
	INCREASE_SPELL_CRITICAL((byte)33),
	INCREASE_HEALING_POWER((byte)34),
	INCREASE_HEALING_TAKEN((byte)35),
	MOUNT((byte)36),
	NONE((byte)37),
	SPELL_MODIFIER((byte)38),
	STUN((byte)39),
	FEAR((byte)40),
	SILENCE((byte)41),
	IMMUNE_PHYSICAL((byte)42),
	IMMUNE_MAGICAL((byte)43),
	IMMUNE_ALL((byte)44),
	;
	
	private byte value;
	
	private AuraEffect(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}
}

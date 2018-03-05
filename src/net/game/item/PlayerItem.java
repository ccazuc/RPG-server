package net.game.item;

import net.game.unit.Player;
import net.thread.log.LogRunnable;

public class PlayerItem {

	private final long GUID;
	private final Item item;
	private int enchantmentId;
	private int[] gems;
	private short amount;
	private final ItemSourceType sourceType;
	private final int ownerGUID;
	private final long creationTimestamp;
	
	public PlayerItem(Item item, Player player, long GUID, ItemSourceType sourceType, short amount, long creationTimestamp)
	{
		this.GUID = GUID;
		this.item = item;
		this.sourceType = sourceType;
		this.ownerGUID = player.getUnitID();
		this.creationTimestamp = creationTimestamp;
		this.amount = amount;
	}
	
	public Item getItem()
	{
		return (this.item);
	}
	
	public void setGem(Player player, int gemId, int slot)
	{
		if (slot < 0 || slot >= this.gems.length)
		{
			LogRunnable.writePlayerLog(player, "Invalid gem slot: " + slot);
			return;
		}
		this.gems[slot] = gemId;
	}
	
	public void setEnchantment(int enchantmentId)
	{
		this.enchantmentId = enchantmentId;
	}
	
	public int getenchantmentId()
	{
		return (this.enchantmentId);
	}
	
	public ItemSourceType getSourceType()
	{
		return (this.sourceType);
	}
	
	public int getOwnerGUID()
	{
		return (this.ownerGUID);
	}
	
	public long getCreationTimestamp()
	{
		return (this.creationTimestamp);
	}
	
	public short getAmount()
	{
		return (this.amount);
	}
}

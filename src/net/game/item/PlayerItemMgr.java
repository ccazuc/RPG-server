package net.game.item;

import java.util.HashMap;

import net.Server;
import net.game.unit.Player;

public class PlayerItemMgr {

	private final static HashMap<Long, PlayerItem> playerItemMap = new HashMap<Long, PlayerItem>();
	private static long currentGUID;
	
	public static void createNewPlayerItem(Item item, Player player, ItemSourceType sourceType, short amount)
	{
		PlayerItem playerItem = new PlayerItem(item, player, generateGUID(), sourceType, amount, Server.getLoopTickTimer());
		playerItemMap.put(generateGUID(), playerItem);
	}
	
	public static long generateGUID()
	{
		return (++currentGUID);
	}
}

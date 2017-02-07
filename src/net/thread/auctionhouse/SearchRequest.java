package net.thread.auctionhouse;

import java.util.LinkedList;

import net.command.item.CommandAuction;
import net.game.auction.AuctionEntry;
import net.game.auction.AuctionHouseFilter;
import net.game.auction.AuctionHouseMgr;
import net.game.auction.AuctionHouseQualityFilter;
import net.game.auction.AuctionHouseSort;
import net.game.unit.Player;

public class SearchRequest implements AuctionHouseTask {

	private final Player player;
	private final String search;
	private final short page;
	private final short minLevel;
	private final short maxLevel;
	private final AuctionHouseQualityFilter qualityFilter;
	private final AuctionHouseSort sort;
	private final AuctionHouseFilter filter;
	private final boolean exactWord;
	private final boolean isUsable;
	
	public SearchRequest(Player player, String search, short page, short minLevel, short maxLevel, AuctionHouseQualityFilter qualityFilter, AuctionHouseSort sort, AuctionHouseFilter filter, boolean exactWord, boolean isUsable) {
		this.player = player;
		this.search = search;
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		this.page = page;
		this.qualityFilter = qualityFilter;
		this.sort = sort;
		this.filter = filter;
		this.exactWord = exactWord;
		this.isUsable = isUsable;
	}
	
	@Override
	public void execute() {
		LinkedList<AuctionEntry> entryList = AuctionHouseMgr.getEntryList(this.player, this);
		if(entryList == null || entryList.size() == 0) {
			CommandAuction.sendQuery(this.player, null, 0, (short)0);
			return;
		}
		if(this.page > Math.ceil(entryList.size()/(double)AuctionHouseMgr.NUMBER_RESULT_PER_PAGE)) {
			return;
		}
		CommandAuction.sendQuery(this.player, entryList, (this.page-1)*AuctionHouseMgr.NUMBER_RESULT_PER_PAGE, this.page);
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public String getSearch() {
		return this.search;
	}
	
	public short getMinLevel() {
		return this.minLevel;
	}
	
	public short getMaxLevel() {
		return this.maxLevel;
	}
	
	public AuctionHouseQualityFilter getQualityFilter() {
		return this.qualityFilter;
	}
	
	public AuctionHouseSort getSort() {
		return this.sort;
	}
	
	public AuctionHouseFilter getFilter() {
		return this.filter;
	}
	
	public boolean getExactWord() {
		return this.exactWord;
	}
	
	public boolean isUsable() {
		return this.isUsable;
	}
}

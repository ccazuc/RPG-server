package net.game.auction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import net.game.unit.Player;
import net.thread.auctionhouse.SearchRequest;
import net.utils.StringUtils;

public class AuctionHouse {

	private final ArrayList<LinkedList<AuctionEntry>> sortedList;
	private final HashMap<Integer, AuctionEntry> entryMap;
	
	public AuctionHouse() {
		this.sortedList = new ArrayList<LinkedList<AuctionEntry>>();
		this.entryMap = new HashMap<Integer, AuctionEntry>();
		int i = 0;
		while(i < AuctionHouseSort.values().length) {
			this.sortedList.add(new LinkedList<AuctionEntry>());
			i++;
		}
	}
	
	public void addItem(AuctionEntry entry) {
		this.entryMap.put(entry.getEntryID(), entry);
		addEntryInBidAscendingList(entry);
		addEntryInBidDescendingList(entry);
		addEntryInSellerAscendingList(entry);
		addEntryInSellerDescendingList(entry);
		addEntryInTimeLeftAscendingList(entry);
		addEntryInTimeLeftDescendingList(entry);
		addEntryInLevelAscendingList(entry);
		addEntryInLevelDescendingList(entry);
		addEntryInRarityAscendingList(entry);
		addEntryInRarityDescendingList(entry);
	}
	
	public void removeItem(AuctionEntry entry) {
		this.entryMap.remove(entry.getEntryID());
		int i = this.sortedList.size();
		while(--i >= 0) {
			removeItemInSortedList(this.sortedList.get(i), entry);
		}
	}
	
	public ArrayList<AuctionEntry> getItemSoldByPlayerList(Player player) {
		LinkedList<AuctionEntry> list = this.sortedList.get(0);
		ArrayList<AuctionEntry> resultList = null;
		AuctionEntry entry;
		ListIterator<AuctionEntry> ite = list.listIterator();
		boolean init = false;
		int playerID = player.getUnitID();
		while(ite.hasNext()) {
			entry = ite.next();
			if(entry.getSellerID() == playerID) {
				if(!init) {
					resultList = new ArrayList<AuctionEntry>();
					init = true;
				}
				resultList.add(entry);
			}
		}
		return resultList;
	}
	
	public AuctionEntry getEntry(int entryID) {
		return this.entryMap.get(entryID);
	}
	
	public LinkedList<AuctionEntry> getEntryList(SearchRequest request) {
		LinkedList<AuctionEntry> list = this.sortedList.get(request.getSort().getValue());
		final ListIterator<AuctionEntry> iterator = list.listIterator();
		AuctionEntry entry;
		LinkedList<AuctionEntry> result = null;
		boolean init = false;
		boolean searchName = request.getSearch().length() != 0;
		short minLevel = request.getMinLevel();
		short maxLevel = request.getMaxLevel();
		boolean usable = request.isUsable();
		byte quality = (byte)(request.getQualityFilter().getValue()-2);
		boolean exactWord = request.getExactWord();
		boolean qualityAll = request.getQualityFilter() == AuctionHouseQualityFilter.ALL;
		String search = StringUtils.toLowerCase(request.getSearch());
		if(minLevel > maxLevel && maxLevel != 0) {
			return null;
		}
		boolean hasMaxLevel = maxLevel != 0;
		boolean hasMinLevel = minLevel != 0;
		while(iterator.hasNext()) {
			entry = iterator.next();
			if((hasMinLevel && entry.getItem().getLevel() < minLevel) || (hasMaxLevel && entry.getItem().getLevel() > maxLevel)) {
				continue;
			}
			if(!qualityAll && quality != entry.getItem().getQuality().getValue()) {
				continue;
			}
			//TODO: filter itemType
			if(usable) {
				//TODO: filter equipable items
			}
			if(searchName) {
				if(!(exactWord && entry.getItem().getLowerCaseName().equals(search)) && (!exactWord && !entry.getItem().getLowerCaseName().contains(search))) {
					continue;
				}
			}
			if(!init) {
				result = new LinkedList<AuctionEntry>();
				init = true;
			}
			result.add(entry);
		}
		return result;
	}
	
	private void addEntryInBidAscendingList(AuctionEntry auction) {
		LinkedList<AuctionEntry> list = this.sortedList.get(AuctionHouseSort.BID_ASCENDING.getValue());
		final ListIterator<AuctionEntry> iterator = list.listIterator();
		AuctionEntry entry;
		while(iterator.hasNext()) {
			entry = iterator.next();
			if(auction.getBuyoutPrice() < entry.getBuyoutPrice()) {
				iterator.previous();
				iterator.add(auction);
				return;
			}
		}
		iterator.add(auction);
	}
	
	private void addEntryInBidDescendingList(AuctionEntry auction) {
		LinkedList<AuctionEntry> list = this.sortedList.get(AuctionHouseSort.BID_DESCENDING.getValue());
		final ListIterator<AuctionEntry> iterator = list.listIterator();
		AuctionEntry entry;
		while(iterator.hasNext()) {
			entry = iterator.next();
			if(auction.getBuyoutPrice() > entry.getBuyoutPrice()) {
				iterator.previous();
				iterator.add(auction);
				return;
			}
		}
		iterator.add(auction);
	}
	
	private void addEntryInSellerAscendingList(AuctionEntry auction) {
		LinkedList<AuctionEntry> list = this.sortedList.get(AuctionHouseSort.VENDOR_ASCENDING.getValue());
		final ListIterator<AuctionEntry> iterator = list.listIterator();
		AuctionEntry entry;
		while(iterator.hasNext()) {
			entry = iterator.next();
			if(auction.getSellerName().compareTo(entry.getSellerName()) > 0) {
				iterator.previous();
				iterator.add(auction);
				return;
			}
		}
		iterator.add(auction);
	}
	
	private void addEntryInSellerDescendingList(AuctionEntry auction) {
		LinkedList<AuctionEntry> list = this.sortedList.get(AuctionHouseSort.VENDOR_DESCENDING.getValue());
		final ListIterator<AuctionEntry> iterator = list.listIterator();
		AuctionEntry entry;
		while(iterator.hasNext()) {
			entry = iterator.next();
			if(auction.getSellerName().compareTo(entry.getSellerName()) < 0) {
				iterator.previous();
				iterator.add(auction);
				return;
			}
		}
		iterator.add(auction);
	}
	
	private void addEntryInTimeLeftAscendingList(AuctionEntry auction) {
		LinkedList<AuctionEntry> list = this.sortedList.get(AuctionHouseSort.TIME_LEFT_ASCENDING.getValue());
		final ListIterator<AuctionEntry> iterator = list.listIterator();
		AuctionEntry entry;
		while(iterator.hasNext()) {
			entry = iterator.next();
			if(auction.getAuctionEndTimer() > entry.getAuctionEndTimer()) {
				iterator.previous();
				iterator.add(auction);
				return;
			}
		}
		iterator.add(auction);
	}
	
	private void addEntryInTimeLeftDescendingList(AuctionEntry auction) {
		LinkedList<AuctionEntry> list = this.sortedList.get(AuctionHouseSort.TIME_LEFT_DESCENDING.getValue());
		final ListIterator<AuctionEntry> iterator = list.listIterator();
		AuctionEntry entry;
		while(iterator.hasNext()) {
			entry = iterator.next();
			if(auction.getAuctionEndTimer() < entry.getAuctionEndTimer()) {
				iterator.previous();
				iterator.add(auction);
				return;
			}
		}
		iterator.add(auction);
	}
	
	private void addEntryInLevelAscendingList(AuctionEntry auction) {
		LinkedList<AuctionEntry> list = this.sortedList.get(AuctionHouseSort.LEVEL_ASCENDING.getValue());
		final ListIterator<AuctionEntry> iterator = list.listIterator();
		AuctionEntry entry;
		while(iterator.hasNext()) {
			entry = iterator.next();
			if(auction.getItem().getLevel() > entry.getItem().getLevel()) {
				iterator.previous();
				iterator.add(auction);
				return;
			}
		}
		iterator.add(auction);
	}
	
	private void addEntryInLevelDescendingList(AuctionEntry auction) {
		LinkedList<AuctionEntry> list = this.sortedList.get(AuctionHouseSort.LEVEL_DESCENDING.getValue());
		final ListIterator<AuctionEntry> iterator = list.listIterator();
		AuctionEntry entry;
		while(iterator.hasNext()) {
			entry = iterator.next();
			if(auction.getAuctionEndTimer() < entry.getAuctionEndTimer()) {
				iterator.previous();
				iterator.add(auction);
				return;
			}
		}
		iterator.add(auction);
	}
	
	private void addEntryInRarityAscendingList(AuctionEntry auction) {
		LinkedList<AuctionEntry> list = this.sortedList.get(AuctionHouseSort.RARITY_ASCENDING.getValue());
		final ListIterator<AuctionEntry> iterator = list.listIterator();
		AuctionEntry entry;
		while(iterator.hasNext()) {
			entry = iterator.next();
			if(auction.getItem().getQuality().getValue() > entry.getItem().getQuality().getValue()) {
				iterator.previous();
				iterator.add(auction);
				return;
			}
		}
		iterator.add(auction);
	}
	
	private void addEntryInRarityDescendingList(AuctionEntry auction) {
		LinkedList<AuctionEntry> list = this.sortedList.get(AuctionHouseSort.RARITY_DESCENDING.getValue());
		final ListIterator<AuctionEntry> iterator = list.listIterator();
		AuctionEntry entry;
		while(iterator.hasNext()) {
			entry = iterator.next();
			if(auction.getItem().getQuality().getValue() < entry.getItem().getQuality().getValue()) {
				iterator.previous();
				iterator.add(auction);
				return;
			}
		}
		iterator.add(auction);
	}
	
	private static void removeItemInSortedList(LinkedList<AuctionEntry> sortedList, AuctionEntry removedEntry) {
		final ListIterator<AuctionEntry> iterator = sortedList.listIterator();
		AuctionEntry entry;
		while(iterator.hasNext()) {
			entry = iterator.next();
			if(entry == removedEntry) {
				iterator.remove();
				return;
			}
		}
	}
	
	/*private static ArrayList<AuctionEntry> filterListByQuality(ArrayList<AuctionEntry> list, AuctionHouseQualityFilter qualityFilter) {
		if(qualityFilter == AuctionHouseQualityFilter.ALL) {
			return list;
		}
		ArrayList<AuctionEntry> result = new ArrayList<AuctionEntry>();
		int i = list.size();
		while(--i >= 0) {
			if(list.get(i).getItem().getQuality().getValue()+1 == qualityFilter.getValue()) {
				result.add(list.get(i));
			}
		}
		return result;
	}
	
	private static ArrayList<AuctionEntry> sortList(ArrayList<AuctionEntry> list, AuctionHouseSort sorted) {
		if(sorted == AuctionHouseSort.BID_ASCENDING) {
			return sortByBidAscending(list, 0, list.size()-1);
		}
		if(sorted == AuctionHouseSort.BID_DESCENDING) {
			return sortByBidDescending(list, 0, list.size()-1);
		}
		if(sorted == AuctionHouseSort.LEVEL_ASCENDING) {
			
		}
		if(sorted == AuctionHouseSort.LEVEL_DESCENDING) {
			
		}
		if(sorted == AuctionHouseSort.RARITY_ASCENDING) {
			
		}
		if(sorted == AuctionHouseSort.RARITY_DESCENDING) {
			
		}
		if(sorted == AuctionHouseSort.TIME_LEFT_ASCENDING) {
			
		}
		if(sorted == AuctionHouseSort.TIME_LEFT_DESCENDING) {
			
		}
		if(sorted == AuctionHouseSort.VENDOR_ASCENDING) {
			
		}
		if(sorted == AuctionHouseSort.VENDOR_DESCENDING) {
			
		}
		System.out.println("Error in AuctionHouse.sortList, sorted not found.");
		return null;
	}
	
	private static int partitionBidAscending(ArrayList<AuctionEntry> list, int left, int right) {
	      int i = left, j = right;
	      AuctionEntry tmp;
	      int pivot = list.get((left + right) / 2).getBidPrice();
	      while(i <= j) {
	            while(list.get(i).getBidPrice() < pivot)
	                  i++;
	            while(list.get(i).getBidPrice() > pivot)
	                  j--;
	            if(i <= j) {
	                  tmp = list.get(i);
	                  list.set(i, list.get(j));
	                  list.set(j, tmp);
	                  i++;
	                  j--;
	            }
	      };
	      return i;
	}

	private static ArrayList<AuctionEntry> sortByBidAscending(ArrayList<AuctionEntry> list, int left, int right) {
	      int index = partitionBidAscending(list, left, right);
	      if (left < index - 1) {
		      sortByBidAscending(list, left, index - 1);
	      }
	      if (index < right) {
		      sortByBidAscending(list, index, right);
	      }
	      return list;
	}
	
	private static int partitionBidDescending(ArrayList<AuctionEntry> list, int left, int right) {
	      int i = left, j = right;
	      AuctionEntry tmp;
	      int pivot = list.get((left + right) / 2).getBidPrice();
	      while(i <= j) {
	            while(list.get(i).getBidPrice() > pivot)
	                  i++;
	            while(list.get(i).getBidPrice() < pivot)
	                  j--;
	            if(i <= j) {
	                  tmp = list.get(i);
	                  list.set(i, list.get(j));
	                  list.set(j, tmp);
	                  i++;
	                  j--;
	            }
	      };
	      return i;
	}

	private static ArrayList<AuctionEntry> sortByBidDescending(ArrayList<AuctionEntry> list, int left, int right) {
	      int index = partitionBidDescending(list, left, right);
	      if (left < index - 1) {
		      sortByBidDescending(list, left, index - 1);
	      }
	      if (index < right) {
		      sortByBidDescending(list, index, right);
	      }
	      return list;
	}*/
}

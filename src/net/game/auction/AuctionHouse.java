package net.game.auction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import net.thread.auctionhouse.SearchRequest;

public class AuctionHouse {

	private final ArrayList<LinkedList<AuctionEntry>> sortedList;
	private final HashMap<Integer, AuctionEntry> entryMap;
	
	public AuctionHouse() {
		this.sortedList = new ArrayList<LinkedList<AuctionEntry>>();
		this.entryMap = new HashMap<Integer, AuctionEntry>();
	}
	
	public void addItem(AuctionEntry entry) {
		entry.setID(AuctionHouseMgr.generateEntryID());
		this.entryMap.put(entry.getID(), entry);
		addEntryInBidAscendingList(entry);
		addEntryInBidDescendingList(entry);
		addEntryInSellerAscendingList(entry);
		addEntryInSellerDescendingList(entry);
		addEntryInTimeLeftAscendingList(entry);
		addEntryInTimeLeftDescendingList(entry);
		addEntryInLevelAscendingList(entry);
		addEntryInLevelDescendingList(entry);
		addEntryInQualityAscendingList(entry);
		addEntryInQualityDescendingList(entry);
	}
	
	public HashMap<Integer, AuctionEntry> getRawEntryList() {
		return this.entryMap;
	}
	
	public LinkedList<AuctionEntry> getEntryList(SearchRequest request) {
		LinkedList<AuctionEntry> list = this.sortedList.get(request.getSort().getValue());
		final ListIterator<AuctionEntry> iterator = list.listIterator();
		AuctionEntry entry;
		LinkedList<AuctionEntry> result = null;
		boolean init = false;
		boolean searchName = request.getSearch().length() == 0;
		int minLevel = request.getMinLevel();
		int maxLevel = request.getMaxLevel();
		boolean usable = request.isUsable();
		if(minLevel > maxLevel) {
			return null;
		}
		while(iterator.hasNext()) {
			entry = iterator.next();
			if(entry.getItem().getLevel() < minLevel || entry.getItem().getLevel() > maxLevel) {
				continue;
			}
			//TODO: filter quality and itemType
			if(usable) {
				//TODO: filter equipable items
			}
			if(!searchName) {
				//TODO: filter by name
			}
			if(!init) {
				result = new LinkedList<AuctionEntry>();
				init = true;
			}
			result.add(entry);
		}
		return list;
	}
	
	private void addEntryInBidAscendingList(AuctionEntry auction) {
		LinkedList<AuctionEntry> list = this.sortedList.get(AuctionHouseSort.BID_ASCENDING.getValue());
		final ListIterator<AuctionEntry> iterator = list.listIterator();
		AuctionEntry entry;
		while(iterator.hasNext()) {
			entry = iterator.next();
			if(auction.getBidPrice() >= entry.getBidPrice()) {
				iterator.add(auction);
				return;
			}
		}
	}
	
	private void addEntryInBidDescendingList(AuctionEntry auction) {
		LinkedList<AuctionEntry> list = this.sortedList.get(AuctionHouseSort.BID_DESCENDING.getValue());
		final ListIterator<AuctionEntry> iterator = list.listIterator();
		AuctionEntry entry;
		while(iterator.hasNext()) {
			entry = iterator.next();
			if(auction.getBidPrice() <= entry.getBidPrice()) {
				iterator.add(auction);
				return;
			}
		}
	}
	
	private void addEntryInSellerAscendingList(AuctionEntry auction) {
		LinkedList<AuctionEntry> list = this.sortedList.get(AuctionHouseSort.VENDOR_ASCENDING.getValue());
		final ListIterator<AuctionEntry> iterator = list.listIterator();
		AuctionEntry entry;
		while(iterator.hasNext()) {
			entry = iterator.next();
			if(auction.getSellerName().compareTo(entry.getSellerName()) >= 0) {
				iterator.add(auction);
				return;
			}
		}
	}
	
	private void addEntryInSellerDescendingList(AuctionEntry auction) {
		LinkedList<AuctionEntry> list = this.sortedList.get(AuctionHouseSort.VENDOR_ASCENDING.getValue());
		final ListIterator<AuctionEntry> iterator = list.listIterator();
		AuctionEntry entry;
		while(iterator.hasNext()) {
			entry = iterator.next();
			if(auction.getSellerName().compareTo(entry.getSellerName()) <= 0) {
				iterator.add(auction);
				return;
			}
		}
	}
	
	private void addEntryInTimeLeftAscendingList(AuctionEntry auction) {
		LinkedList<AuctionEntry> list = this.sortedList.get(AuctionHouseSort.TIME_LEFT_ASCENDING.getValue());
		final ListIterator<AuctionEntry> iterator = list.listIterator();
		AuctionEntry entry;
		while(iterator.hasNext()) {
			entry = iterator.next();
			if(auction.getAuctionEndTimer() >= entry.getAuctionEndTimer()) {
				iterator.add(auction);
				return;
			}
		}
	}
	
	private void addEntryInTimeLeftDescendingList(AuctionEntry auction) {
		LinkedList<AuctionEntry> list = this.sortedList.get(AuctionHouseSort.TIME_LEFT_DESCENDING.getValue());
		final ListIterator<AuctionEntry> iterator = list.listIterator();
		AuctionEntry entry;
		while(iterator.hasNext()) {
			entry = iterator.next();
			if(auction.getAuctionEndTimer() <= entry.getAuctionEndTimer()) {
				iterator.add(auction);
				return;
			}
		}
	}
	
	private void addEntryInLevelAscendingList(AuctionEntry auction) {
		LinkedList<AuctionEntry> list = this.sortedList.get(AuctionHouseSort.LEVEL_ASCENDING.getValue());
		final ListIterator<AuctionEntry> iterator = list.listIterator();
		AuctionEntry entry;
		while(iterator.hasNext()) {
			entry = iterator.next();
			if(auction.getItem().getLevel() >= entry.getItem().getLevel()) {
				iterator.add(auction);
				return;
			}
		}
	}
	
	private void addEntryInLevelDescendingList(AuctionEntry auction) {
		LinkedList<AuctionEntry> list = this.sortedList.get(AuctionHouseSort.LEVEL_DESCENDING.getValue());
		final ListIterator<AuctionEntry> iterator = list.listIterator();
		AuctionEntry entry;
		while(iterator.hasNext()) {
			entry = iterator.next();
			if(auction.getAuctionEndTimer() <= entry.getAuctionEndTimer()) {
				iterator.add(auction);
				return;
			}
		}
	}
	
	private void addEntryInQualityAscendingList(AuctionEntry auction) {
		LinkedList<AuctionEntry> list = this.sortedList.get(AuctionHouseSort.LEVEL_ASCENDING.getValue());
		final ListIterator<AuctionEntry> iterator = list.listIterator();
		AuctionEntry entry;
		while(iterator.hasNext()) {
			entry = iterator.next();
			if(auction.getItem().getQuality().getValue() >= entry.getItem().getQuality().getValue()) {
				iterator.add(auction);
				return;
			}
		}
	}
	
	private void addEntryInQualityDescendingList(AuctionEntry auction) {
		LinkedList<AuctionEntry> list = this.sortedList.get(AuctionHouseSort.LEVEL_DESCENDING.getValue());
		final ListIterator<AuctionEntry> iterator = list.listIterator();
		AuctionEntry entry;
		while(iterator.hasNext()) {
			entry = iterator.next();
			if(auction.getItem().getQuality().getValue() <= entry.getItem().getQuality().getValue()) {
				iterator.add(auction);
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

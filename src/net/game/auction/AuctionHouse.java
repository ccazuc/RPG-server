package net.game.auction;

import java.util.ArrayList;
import java.util.HashMap;

public class AuctionHouse {

	private final ArrayList<ArrayList<AuctionEntry>> filterList;
	private final HashMap<Integer, AuctionEntry> entryMap;
	
	public AuctionHouse() {
		this.filterList = new ArrayList<ArrayList<AuctionEntry>>();
		this.entryMap = new HashMap<Integer, AuctionEntry>();
	}
	
	public void addItem(AuctionEntry entry) {
		entry.setID(AuctionHouseMgr.generateEntryID());
		this.entryMap.put(entry.getID(), entry);
		//TODO: add item in the correct filterList
	}
	
	public HashMap<Integer, AuctionEntry> getRawEntryList() {
		return this.entryMap;
	}
	
	public ArrayList<AuctionEntry> getEntryList(AuctionHouseFilter itemTypeFilter, AuctionHouseSort sorted, AuctionHouseQualityFilter qualityFilter, short page) {
		ArrayList<AuctionEntry> list = this.filterList.get(itemTypeFilter.getValue());
		list = filterListByQuality(list, qualityFilter);
		list = sortList(list, sorted);
		return list;
	}
	
	private static ArrayList<AuctionEntry> filterListByQuality(ArrayList<AuctionEntry> list, AuctionHouseQualityFilter qualityFilter) {
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
	}
}

package net.game.manager;

import java.sql.SQLException;
import java.util.HashMap;

import jdo.JDOStatement;
import net.Server;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.item.Item;
import net.game.item.bag.ContainerManager;
import net.game.item.gem.GemColor;
import net.game.item.gem.GemManager;
import net.game.item.potion.PotionManager;
import net.game.item.stuff.Stuff;
import net.game.item.stuff.StuffManager;
import net.game.item.weapon.WeaponManager;
import net.game.unit.Player;

public class ItemMgr {
	
	private static final HashMap<Integer, JDOStatement> getBagItemsMap = new HashMap<Integer, JDOStatement>();
	private static final HashMap<Integer, JDOStatement> setBagItemsMap = new HashMap<Integer, JDOStatement>();
	//private static String getBagRequest;
	//private static String setBagRequest;
	//private static JDOStatement getBagItem;
	//private static JDOStatement setBagItem;
	private static JDOStatement getEquippedBag;
	private static JDOStatement setEquippedBag;
	private static JDOStatement getEquippedItem;
	private static JDOStatement setEquippedItem;
	
	public static void initSQLRequest() {
		/*int x = 1;
		int i = 1;
		StringBuilder builderSetBagItemsRequest = new StringBuilder();
		while(i < 97) {
			x = 1;
			builderSetBagItemsRequest.append("slot"+i+" = ?, numberstack"+i+" = ?, ");
			while(x <= 3) {
				if(i == 96 && x == 3) {
					builderSetBagItemsRequest.append("slot"+i+"_gem"+x+" = ? ");
				}
				else {
					builderSetBagItemsRequest.append("slot"+i+"_gem"+x+" = ?, ");
				}
				x++;
			}
			i++;
		}
		setBagRequest = builderSetBagItemsRequest.toString();
		x = 1;
		i = 1;*/
		/*StringBuilder builderGetBagItemsRequest = new StringBuilder();
		while(i < 97) {
			x = 1;
			builderGetBagItemsRequest.append("slot"+Integer.toString(i)+", numberstack"+Integer.toString(i)+", ");
			while(x <= 3) {
				if(i == 96 && x == 3) {
					builderGetBagItemsRequest.append("slot"+Integer.toString(i)+"_gem"+Integer.toString(x)+" ");
				}
				else {
					builderGetBagItemsRequest.append("slot"+Integer.toString(i)+"_gem"+Integer.toString(x)+", ");
				}
				x++;
			}
			i++;
		}
		getBagRequest = builderGetBagItemsRequest.toString();*/
	}
	
	/*public void getBagItems(Player player) throws SQLException {
		int i = 1;
		int id;
		int number;
		int gem1Id;
		int gem2Id;
		int gem3Id;
		if(getBagItem == null) {
			String request = "";
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT ");
			builder.append(getBagRequest);
			builder.append("FROM bag WHERE character_id = ?");
			request = builder.toString();
			getBagItem = Server.getJDO().prepare(request);
		}
		i = 0;
		getBagItem.clear();
		getBagItem.putInt(player.getUnitID());
		getBagItem.execute();
		if(getBagItem.fetch()) {
			Connection connection = player.getConnectionManager().getConnection();
			connection.writeByte(PacketID.LOAD_BAG_ITEMS);
			connection.writeInt(player.getBag().getBag().length);
			while(i < player.getBag().getBag().length) {
				id = getBagItem.getInt();
				number = getBagItem.getInt();
				gem1Id = getBagItem.getInt();
				gem2Id = getBagItem.getInt();
				gem3Id = getBagItem.getInt();
				connection.writeInt(id);
				connection.writeInt(number);
				connection.writeInt(gem1Id);
				connection.writeInt(gem2Id);
				connection.writeInt(gem3Id);
				if(StuffManager.exists(id)) {
					player.getBag().setBag(i, StuffManager.getClone(id));
					Stuff temp = (Stuff)player.getBag().getBag(i);
					if(GemManager.exists(gem1Id) && temp.getGemSlot1() != GemColor.NONE) {
						temp.setEquippedGem1(GemManager.getClone(gem1Id));
					}
					if(GemManager.exists(gem2Id) && temp.getGemSlot2() != GemColor.NONE) {
						temp.setEquippedGem2(GemManager.getClone(gem2Id));
					}
					if(GemManager.exists(gem3Id) && temp.getGemSlot3() != GemColor.NONE) {
						temp.setEquippedGem3(GemManager.getClone(gem3Id));
					}
					player.getBag().setBag(i, temp);
					((Stuff)player.getBag().getBag(i)).checkBonusTypeActivated();
					connection.writeChar(ItemType.STUFF.getValue());
				}
				else if(PotionManager.exists(id)) {
					player.getBag().setBag(i, PotionManager.getClone(id));
					player.getBag().getNumberStack().put(player.getBag().getBag(i), number);
					connection.writeChar(ItemType.POTION.getValue());
				}
				else if(WeaponManager.exists(id)) {
					player.getBag().setBag(i, WeaponManager.getClone(id));
					Stuff temp = (Stuff)player.getBag().getBag(i);
					if(GemManager.exists(gem1Id)) {
						temp.setEquippedGem1(GemManager.getClone(gem1Id));
					}
					if(GemManager.exists(gem2Id)) {
						temp.setEquippedGem2(GemManager.getClone(gem2Id));
					}
					if(GemManager.exists(gem3Id)) {
						temp.setEquippedGem3(GemManager.getClone(gem3Id));
					}
					player.getBag().setBag(i, temp);
					connection.writeChar(ItemType.WEAPON.getValue());
				}
				else if(GemManager.exists(id)) {
					player.getBag().setBag(i, GemManager.getClone(id));
					connection.writeChar(ItemType.GEM.getValue());
				}
				else {
					player.getBag().setBag(i, null);
					connection.writeChar((char)0);
				}
				i++;
			}
			connection.send();
		}
	}*/
	
	public static void getBagItems(Player player) {
		int i = 1;
		int id;
		int number;
		int gem1Id;
		int gem2Id;
		int gem3Id;
		try {
			/*if(getBagItem == null) {
				String request = "";
				StringBuilder builder = new StringBuilder();
				builder.append("SELECT ");
				builder.append(getBagRequest);
				builder.append("FROM bag WHERE character_id = ?");
				request = builder.toString();
				getBagItem = Server.getAsyncHighPriorityJDO().prepare(request);
			}*/
			JDOStatement getBagItem = null;
			if(getBagItemsMap.containsKey(player.getBag().getBag().length)) {
				getBagItem = getBagItemsMap.get(player.getBag().getBag().length);
			}
			else  {
				getBagItem = prepareGetBagRequest(player.getBag().getBag().length);
				getBagItemsMap.put(player.getBag().getBag().length, getBagItem);
			}
			i = 0; 
			int numberBagItems = 0;
			getBagItem.clear();
			getBagItem.putInt(player.getUnitID());
			getBagItem.execute();
			if(getBagItem.fetch()) {
				Connection connection = player.getConnectionManager().getConnection();
				connection.startPacket();
				connection.writeShort(PacketID.LOAD_BAG_ITEMS);
				while(i < player.getBag().getBag().length) {
					id = getBagItem.getInt();
					number = getBagItem.getInt();
					gem1Id = getBagItem.getInt();
					gem2Id = getBagItem.getInt();
					gem3Id = getBagItem.getInt();
					if(StuffManager.exists(id)) {
						player.getBag().setBag(i, StuffManager.getClone(id));
						Stuff temp = (Stuff)player.getBag().getBag(i);
						if(GemManager.exists(gem1Id) && temp.getGemSlot1() != GemColor.NONE) {
							temp.setEquippedGem(0, GemManager.getClone(gem1Id));
						}
						if(GemManager.exists(gem2Id) && temp.getGemSlot2() != GemColor.NONE) {
							temp.setEquippedGem(1, GemManager.getClone(gem2Id));
						}
						if(GemManager.exists(gem3Id) && temp.getGemSlot3() != GemColor.NONE) {
							temp.setEquippedGem(2, GemManager.getClone(gem3Id));
						}
						player.getBag().setBag(i, temp);
						((Stuff)player.getBag().getBag(i)).checkBonusTypeActivated();
						numberBagItems++;
					}
					else if(PotionManager.exists(id)) {
						player.getBag().setBag(i, PotionManager.getClone(id));
						player.getBag().getBag(i).setAmount(number);;
						numberBagItems++;
					}
					else if(WeaponManager.exists(id)) {
						player.getBag().setBag(i, WeaponManager.getClone(id));
						Stuff temp = (Stuff)player.getBag().getBag(i);
						if(GemManager.exists(gem1Id)) {
							temp.setEquippedGem(0, GemManager.getClone(gem1Id));
						}
						if(GemManager.exists(gem2Id)) {
							temp.setEquippedGem(1, GemManager.getClone(gem2Id));
						}
						if(GemManager.exists(gem3Id)) {
							temp.setEquippedGem(2, GemManager.getClone(gem3Id));
						}
						player.getBag().setBag(i, temp);
						numberBagItems++;
					}
					else if(GemManager.exists(id)) {
						player.getBag().setBag(i, GemManager.getClone(id));
						numberBagItems++;
					}
					else {
						player.getBag().setBag(i, null);
					}
					i++;
				}
				i = 0;
				connection.writeInt(numberBagItems);
				while(i < player.getBag().getBag().length) {
					if(player.getBag().getBag(i) == null) {
						i++;
						continue;
					}
					if(player.getBag().getBag(i).isContainer() || player.getBag().getBag(i).isGem()) {
						connection.writeInt(i);
						connection.writeInt(player.getBag().getBag(i).getId());
						connection.writeByte(player.getBag().getBag(i).getItemType().getValue());
					}
					else if(player.getBag().getBag(i).isPotion()) {
						connection.writeInt(i);
						connection.writeInt(player.getBag().getBag(i).getId());
						connection.writeByte(player.getBag().getBag(i).getItemType().getValue());
						connection.writeInt(player.getBag().getBag(i).getAmount());
					}
					else if(player.getBag().getBag(i).isStuff() || player.getBag().getBag(i).isWeapon()) {
						connection.writeInt(i);
						connection.writeInt(player.getBag().getBag(i).getId());
						connection.writeByte(player.getBag().getBag(i).getItemType().getValue());
						if(((Stuff)player.getBag().getBag(i)).getEquippedGem(0) != null || ((Stuff)player.getBag().getBag(i)).getEquippedGem(1) != null || ((Stuff)player.getBag().getBag(i)).getEquippedGem(2) != null) {
							connection.writeBoolean(true);
							int j = 0;
							while(j < 3) {
								if(((Stuff)player.getBag().getBag(i)).getEquippedGem(j) != null) {
									connection.writeInt(((Stuff)player.getBag().getBag(i)).getEquippedGem(j).getId());
								}
								else {
									connection.writeInt(0);
								}
								j++;
							}
						}
						else {
							connection.writeBoolean(false);
						}
					}
					i++;
				}
				connection.endPacket();
				connection.send();
				player.getBag().updateBagItem();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setBagItems(Player player) {
		try {
			int i = 0;
			/*if(setBagItem == null) {
				String request = "";
				StringBuilder builder = new StringBuilder();
				builder.append("UPDATE bag SET ");
				builder.append(setBagRequest);
				builder.append("WHERE character_id = ?");
				request = builder.toString();
				setBagItem = Server.getJDO().prepare(request);
			}*/
			JDOStatement setBagItem = null;
			if(setBagItemsMap.containsKey(player.getBag().getBag().length)) {
				setBagItem = setBagItemsMap.get(player.getBag().getBag().length);
			}
			else  {
				setBagItem = prepareSetBagRequest(player.getBag().getBag().length);
				setBagItemsMap.put(player.getBag().getBag().length, setBagItem);
			}
			setBagItem.clear();
			while(i < player.getBag().getBag().length) {
				if(i < player.getBag().getBag().length) {
					Item tempBag = player.getBag().getBag(i);
					if(tempBag == null) {
						setBagItem.putInt(0);
						setBagItem.putInt(0);
						setBagItem.putInt(0);
						setBagItem.putInt(0);
						setBagItem.putInt(0);
					}
					else if(tempBag.isStuff() || tempBag.isWeapon()) {
						setBagItem.putInt(tempBag.getId());
						setBagItem.putInt(0);
						int j = 0;
						while(j < 3) {
							if(((Stuff)tempBag).getEquippedGem(j) == null) {
								setBagItem.putInt(0);
							}
							else {
								setBagItem.putInt((((Stuff)tempBag).getEquippedGem(j).getId()));
							}
							j++;
						}
						/*if(((Stuff)tempBag).getEquippedGem(0) == null) {
							setBagItem.putInt(0);
						}
						else {
							setBagItem.putInt((((Stuff)tempBag).getEquippedGem(0).getId()));
						}
						if(((Stuff)tempBag).getEquippedGem(1) == null) {
							setBagItem.putInt(0);
						}
						else {
							setBagItem.putInt((((Stuff)tempBag).getEquippedGem(1).getId()));
						}
						if(((Stuff)tempBag).getEquippedGem(2) == null) {
							setBagItem.putInt(0);
						}
						else {
							setBagItem.putInt((((Stuff)tempBag).getEquippedGem(2).getId()));
						}*/
					}
					else if(tempBag.isContainer() || tempBag.isGem()) {
						setBagItem.putInt(tempBag.getId());
						setBagItem.putInt(0);
						setBagItem.putInt(0);
						setBagItem.putInt(0);
						setBagItem.putInt(0);
					}
					else if(tempBag.isItem() || tempBag.isPotion()) {
							setBagItem.putInt(tempBag.getId());
							setBagItem.putInt(tempBag.getAmount());
							setBagItem.putInt(0);
							setBagItem.putInt(0);
							setBagItem.putInt(0);
					}
				}
				else {
					setBagItem.putInt(0);
					setBagItem.putInt(0);
					setBagItem.putInt(0);
					setBagItem.putInt(0);
					setBagItem.putInt(0);
				}
				i++;
			}
			setBagItem.putInt(player.getUnitID());
			setBagItem.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void getEquippedBags(Player player) {
		try {
			if(getEquippedBag == null) {
				getEquippedBag = Server.getAsyncHighPriorityJDO().prepare("SELECT slot1, slot2, slot3, slot4 FROM character_containers WHERE character_id = ?");
			}
			getEquippedBag.clear();
			getEquippedBag.putInt(player.getUnitID());
			getEquippedBag.execute();
			int i = 0;
			int id;
			if(getEquippedBag.fetch()) {
				while(i < player.getBag().getEquippedBag().length) {
					id = getEquippedBag.getInt();
					if(ContainerManager.exists(id)) {
						player.getBag().setEquippedBag(i, ContainerManager.getClone(id));
					}
					else {
						player.getBag().setEquippedBag(i, null);
					}
					i++;
				}
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setEquippedBags(Player player) throws SQLException {
		if(setEquippedBag == null) {
			setEquippedBag = Server.getJDO().prepare("UPDATE character_containers SET slot1 = ?, slot2 = ?, slot3 = ?, slot4 = ? WHERE character_id = ?");
		}
		setEquippedBag.clear();
		int i = 0;
		while(i < player.getBag().getEquippedBag().length) {
			if(player.getBag().getEquippedBag(i) == null) {
				setEquippedBag.putInt(0);
			}
			else {
				setEquippedBag.putInt(player.getBag().getEquippedBag(i).getId());
			}
			i++;
		}
		setEquippedBag.putInt(player.getUnitID());
		setEquippedBag.execute();
	}
	
	public void setEquippedItems(Player player) throws SQLException {
		if(setEquippedItem == null) {
			setEquippedItem = Server.getJDO().prepare("UPDATE character_stuff SET head = ?, head_gem1 = ?, head_gem2 = ?, head_gem3 = ?, necklace = ?, necklace_gem1 = ?, necklace_gem2 = ?, necklace_gem3 = ?, shoulders = ?, shoulders_gem1 = ?, shoulders_gem2 = ?, shoulders_gem3 = ?, back = ?, back_gem1 = ?, back_gem2 = ?, back_gem3 = ?, chest = ?, chest_gem1 = ?, chest_gem2 = ?, chest_gem3 = ?, useless = ?, useless_gem1 = ?, useless_gem2 = ?, useless_gem3 = ?, useless2 = ?, useless2_gem1 = ?, useless2_gem2 = ?, useless2_gem3 = ?, wrists = ?, wrists_gem1 = ?, wrists_gem2 = ?, wrists_gem3 = ?, gloves = ?, gloves_gem1 = ?, gloves_gem2 = ?, gloves_gem3 = ?, belt = ?, belt_gem1 = ?, belt_gem2 = ?, belt_gem3 = ?, leggings = ?, leggings_gem1 = ?, leggings_gem2 = ?, leggings_gem3 = ?, boots = ?, boots_gem1 = ?, boots_gem2 = ?, boots_gem3 = ?, ring = ?, ring_gem1 = ?, ring_gem2 = ?, ring_gem3 = ?, ring2 = ?, ring2_gem1 = ?, ring2_gem2 = ?, ring2_gem3 = ?, trinket = ?, trinket_gem1 = ?, trinket_gem2 = ?, trinket_gem3 = ?, trinket2 = ?, trinket2_gem1 = ?, trinket2_gem2 = ?, trinket2_gem3 = ?, mainhand = ?, mainhand_gem1 = ?, mainhand_gem2 = ?, mainhand_gem3 = ?, offhand = ?, offhand_gem1 = ?, offhand_gem2 = ?, offhand_gem3 = ?, ranged = ?, ranged_gem1 = ?, ranged_gem2 = ?, ranged_gem3 = ? WHERE character_id = ?");
		}
		setEquippedItem.clear();
		int i = 0;
		while(i < player.getStuff().length-1) {
			if(player.getStuff(i) == null) {
				setEquippedItem.putInt(0);
				setEquippedItem.putInt(0);
				setEquippedItem.putInt(0);
				setEquippedItem.putInt(0);
			}
			else {
				setEquippedItem.putInt(player.getStuff(i).getId());
				int j = -1;
				while(++j < 3) {
					if(player.getStuff(i).getEquippedGem(j) != null) {
						setEquippedItem.putInt(player.getStuff(i).getEquippedGem(j).getId());
					}
					else {
						setEquippedItem.putInt(0);
					}
				}
			}
			i++;
		}
		setEquippedItem.putInt(player.getUnitID());
		setEquippedItem.execute();
	}
	
	public static void getEquippedItems(Player player) {
		int id;
		int gem1Id;
		int gem2Id;
		int gem3Id;
		Stuff tmp;
		try {
			if(getEquippedItem == null) {
				getEquippedItem = Server.getAsyncHighPriorityJDO().prepare("SELECT head, head_gem1, head_gem2, head_gem3, necklace, necklace_gem1, necklace_gem2, necklace_gem3, shoulders, shoulders_gem1, shoulders_gem2, shoulders_gem3, back, back_gem1, back_gem2, back_gem3, chest, chest_gem1, chest_gem2, chest_gem3, wrists, wrists_gem1, wrists_gem2, wrists_gem3, gloves, gloves_gem1, gloves_gem2, gloves_gem3, belt, belt_gem1, belt_gem2, belt_gem3, leggings, leggings_gem1, leggings_gem2, leggings_gem3, boots, boots_gem1, boots_gem2, boots_gem3, ring, ring2, trinket, trinket2, mainhand, mainhand_gem1, mainhand_gem2, mainhand_gem3, offhand, offhand_gem1, offhand_gem2, offhand_gem3, ranged, ranged_gem1, ranged_gem2, ranged_gem3 FROM character_stuff WHERE character_id = ?");
			}
			getEquippedItem.clear();
			getEquippedItem.putInt(player.getUnitID());
			getEquippedItem.execute();
			Connection connection = player.getConnectionManager().getConnection();
			if(getEquippedItem.fetch()) {
				connection.startPacket();
				connection.writeShort(PacketID.LOAD_EQUIPPED_ITEMS);
				id = getEquippedItem.getInt();
				gem1Id = getEquippedItem.getInt();
				gem2Id = getEquippedItem.getInt();
				gem3Id = getEquippedItem.getInt();
				connection.writeInt(id);
				writeGem(connection, gem1Id, gem2Id, gem3Id);
				if(StuffManager.exists(id) && StuffManager.getStuff(id).isHead() && player.canEquipStuff(StuffManager.getStuff(id))) {
					tmp = StuffManager.getClone(id);
					player.setStuff(0, tmp);
					setGems(tmp, gem1Id, gem2Id, gem3Id);
				}
				id = getEquippedItem.getInt();
				gem1Id = getEquippedItem.getInt();
				gem2Id = getEquippedItem.getInt();
				gem3Id = getEquippedItem.getInt();
				connection.writeInt(id);
				writeGem(connection, gem1Id, gem2Id, gem3Id);
				if(StuffManager.exists(id) && StuffManager.getStuff(id).isNecklace() && player.canEquipStuff(StuffManager.getStuff(id))) {
					tmp = StuffManager.getClone(id);
					player.setStuff(1, tmp);
					setGems(tmp, gem1Id, gem2Id, gem3Id);
				}
				id = getEquippedItem.getInt();
				gem1Id = getEquippedItem.getInt();
				gem2Id = getEquippedItem.getInt();
				gem3Id = getEquippedItem.getInt();
				connection.writeInt(id);
				writeGem(connection, gem1Id, gem2Id, gem3Id);
				if(StuffManager.exists(id) && StuffManager.getStuff(id).isShoulders() && player.canEquipStuff(StuffManager.getStuff(id))) {
					tmp = StuffManager.getClone(id);
					player.setStuff(2, tmp);
					setGems(tmp, gem1Id, gem2Id, gem3Id);
				}
				id = getEquippedItem.getInt();
				gem1Id = getEquippedItem.getInt();
				gem2Id = getEquippedItem.getInt();
				gem3Id = getEquippedItem.getInt();
				connection.writeInt(id);
				writeGem(connection, gem1Id, gem2Id, gem3Id);
				if(StuffManager.exists(id) && StuffManager.getStuff(id).isBack() && player.canEquipStuff(StuffManager.getStuff(id))) {
					tmp = StuffManager.getClone(id);
					player.setStuff(3, tmp);
					setGems(tmp, gem1Id, gem2Id, gem3Id);
				}
				id = getEquippedItem.getInt();
				gem1Id = getEquippedItem.getInt();
				gem2Id = getEquippedItem.getInt();
				gem3Id = getEquippedItem.getInt();
				connection.writeInt(id);
				writeGem(connection, gem1Id, gem2Id, gem3Id);
				if(StuffManager.exists(id) && StuffManager.getStuff(id).isChest() && player.canEquipStuff(StuffManager.getStuff(id))) {
					tmp = StuffManager.getClone(id);
					player.setStuff(4, tmp);
					setGems(tmp, gem1Id, gem2Id, gem3Id);
				}
				id = getEquippedItem.getInt();
				gem1Id = getEquippedItem.getInt();
				gem2Id = getEquippedItem.getInt();
				gem3Id = getEquippedItem.getInt();
				connection.writeInt(id);
				writeGem(connection, gem1Id, gem2Id, gem3Id);
				if(StuffManager.exists(id) && StuffManager.getStuff(id).isWrists() && player.canEquipStuff(StuffManager.getStuff(id))) {
					tmp = StuffManager.getClone(id);
					player.setStuff(7, tmp);
					setGems(tmp, gem1Id, gem2Id, gem3Id);
				}
				id = getEquippedItem.getInt();
				gem1Id = getEquippedItem.getInt();
				gem2Id = getEquippedItem.getInt();
				gem3Id = getEquippedItem.getInt();
				connection.writeInt(id);
				writeGem(connection, gem1Id, gem2Id, gem3Id);
				if(StuffManager.exists(id) && StuffManager.getStuff(id).isGloves() && player.canEquipStuff(StuffManager.getStuff(id))) {
					tmp = StuffManager.getClone(id);
					player.setStuff(8, tmp);
					setGems(tmp, gem1Id, gem2Id, gem3Id);
				}
				id = getEquippedItem.getInt();
				gem1Id = getEquippedItem.getInt();
				gem2Id = getEquippedItem.getInt();
				gem3Id = getEquippedItem.getInt();
				connection.writeInt(id);
				writeGem(connection, gem1Id, gem2Id, gem3Id);
				if(StuffManager.exists(id) && StuffManager.getStuff(id).isBelt() && player.canEquipStuff(StuffManager.getStuff(id))) {
					tmp = StuffManager.getClone(id);
					player.setStuff(9, tmp);
					setGems(tmp, gem1Id, gem2Id, gem3Id);
				}
				id = getEquippedItem.getInt();
				gem1Id = getEquippedItem.getInt();
				gem2Id = getEquippedItem.getInt();
				gem3Id = getEquippedItem.getInt();
				connection.writeInt(id);
				writeGem(connection, gem1Id, gem2Id, gem3Id);
				if(StuffManager.exists(id) && StuffManager.getStuff(id).isLeggings() && player.canEquipStuff(StuffManager.getStuff(id))) {
					tmp = StuffManager.getClone(id);
					player.setStuff(10, tmp);
					setGems(tmp, gem1Id, gem2Id, gem3Id);
				}
				id = getEquippedItem.getInt();
				gem1Id = getEquippedItem.getInt();
				gem2Id = getEquippedItem.getInt();
				gem3Id = getEquippedItem.getInt();
				connection.writeInt(id);
				writeGem(connection, gem1Id, gem2Id, gem3Id);
				if(StuffManager.exists(id) && StuffManager.getStuff(id).isBoots() && player.canEquipStuff(StuffManager.getStuff(id))) {
					tmp = StuffManager.getClone(id);
					player.setStuff(11, tmp);
					setGems(tmp, gem1Id, gem2Id, gem3Id);
				}
				id = getEquippedItem.getInt();
				connection.writeInt(id);
				if(StuffManager.exists(id) && StuffManager.getStuff(id).isRing() && player.canEquipStuff(StuffManager.getStuff(id))) {
					player.setStuff(12, StuffManager.getClone(id));
				}
				id = getEquippedItem.getInt();
				connection.writeInt(id);
				if(StuffManager.exists(id) && StuffManager.getStuff(id).isRing() && player.canEquipStuff(StuffManager.getStuff(id))) {
					player.setStuff(13, StuffManager.getClone(id));
				}
				id = getEquippedItem.getInt();
				connection.writeInt(id);
				if(StuffManager.exists(id) && StuffManager.getStuff(id).isTrinket() && player.canEquipStuff(StuffManager.getStuff(id))) {
					player.setStuff(14, StuffManager.getClone(id));
				}
				id = getEquippedItem.getInt();
				connection.writeInt(id);
				if(StuffManager.exists(id) && StuffManager.getStuff(id).isTrinket() && player.canEquipStuff(StuffManager.getStuff(id))) {
					player.setStuff(15, StuffManager.getClone(id));
				}
				id = getEquippedItem.getInt();
				gem1Id = getEquippedItem.getInt();
				gem2Id = getEquippedItem.getInt();
				gem3Id = getEquippedItem.getInt();
				connection.writeInt(id);
				writeGem(connection, gem1Id, gem2Id, gem3Id);
				if(WeaponManager.exists(id) && player.canEquipWeapon(WeaponManager.getWeapon(id))) {
					tmp = WeaponManager.getClone(id);
					player.setStuff(16, tmp);
					setGems(tmp, gem1Id, gem2Id, gem3Id);
				}
				id = getEquippedItem.getInt();
				gem1Id = getEquippedItem.getInt();
				gem2Id = getEquippedItem.getInt();
				gem3Id = getEquippedItem.getInt();
				connection.writeInt(id);
				writeGem(connection, gem1Id, gem2Id, gem3Id);
				if(WeaponManager.exists(id) && player.canEquipWeapon(WeaponManager.getWeapon(id))) {
					tmp = WeaponManager.getClone(id);
					player.setStuff(17, tmp);
					setGems(tmp, gem1Id, gem2Id, gem3Id);
				}
				id = getEquippedItem.getInt();
				gem1Id = getEquippedItem.getInt();
				gem2Id = getEquippedItem.getInt();
				gem3Id = getEquippedItem.getInt();
				connection.writeInt(id);
				writeGem(connection, gem1Id, gem2Id, gem3Id);
				if(WeaponManager.exists(id) && WeaponManager.getWeapon(id).isRanged() && player.canEquipWeapon(WeaponManager.getWeapon(id))) {
					tmp = WeaponManager.getClone(id);
					player.setStuff(18, tmp);
					setGems(tmp, gem1Id, gem2Id, gem3Id);
				}
				connection.endPacket();
				connection.send();
			}
			else {
				System.out.println("statement error (stuff load)");
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void writeGem(Connection connection, int gem1ID, int gem2ID, int gem3ID) {
		if(gem1ID != 0 || gem2ID != 0 || gem3ID != 0) {
			connection.writeBoolean(true);
			connection.writeInt(gem1ID);
			connection.writeInt(gem2ID);
			connection.writeInt(gem3ID);
		}
		else {
			connection.writeBoolean(false);
		}
	}
	
	private static JDOStatement prepareGetBagRequest(int length) throws SQLException {
		long timer = System.nanoTime();
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT ");
		int i = 1;
		int x = 1;
		length++;
		while(i < length) {
			x = 1;
			builder.append("slot"+i+", numberstack"+i+", ");
			while(x <= 3) {
				if(i == length-1 && x == 3) {
					builder.append("slot"+i+"_gem"+x+" ");
				}
				else {
					builder.append("slot"+i+"_gem"+x+", ");
				}
				x++;
			}
			i++;
		}
		builder.append("FROM bag WHERE character_id = ?");
		JDOStatement result = Server.getAsyncHighPriorityJDO().prepare(builder.toString());
		System.out.println("Get bag item request builds took "+(System.nanoTime()-timer)/1000+" µs.");
		return result;
	}
	
	private static JDOStatement prepareSetBagRequest(int length) throws SQLException {
		long timer = System.nanoTime();
		StringBuilder builder = new StringBuilder();
		builder.append("UPDATE bag SET ");
		int i = 1;
		int x = 1;
		length++;
		while(i < length) {
			x = 1;
			builder.append("slot"+i+" = ?, numberstack"+i+" = ?, ");
			while(x <= 3) {
				if(i == length-1 && x == 3) {
					builder.append("slot"+i+"_gem"+x+" = ? ");
				}
				else {
					builder.append("slot"+i+"_gem"+x+" = ?, ");
				}
				x++;
			}
			i++;
		}
		builder.append("WHERE character_id = ?");
		JDOStatement result = Server.getAsyncHighPriorityJDO().prepare(builder.toString());
		System.out.println("Set bag item request builds took "+(System.nanoTime()-timer)/1000+" µs.");
		return result;
	}
	
	private static void setGems(Stuff stuff, int gem1Id, int gem2Id, int gem3Id) {
		if(stuff.getGemSlot1() != GemColor.NONE && GemManager.exists(gem1Id)) {
			stuff.setEquippedGem(0, GemManager.getClone(gem1Id));
		}
		if(stuff.getGemSlot2() != GemColor.NONE && GemManager.exists(gem2Id)) {
			stuff.setEquippedGem(1, GemManager.getClone(gem2Id));
		}
		if(stuff.getGemSlot3() != GemColor.NONE && GemManager.exists(gem3Id)) {
			stuff.setEquippedGem(2, GemManager.getClone(gem3Id));
		}
	}
}

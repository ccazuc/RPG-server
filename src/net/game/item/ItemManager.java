package net.game.item;

import java.sql.SQLException;

import jdo.JDOStatement;
import net.Server;
import net.connection.Connection;
import net.connection.PacketID;
import net.game.Player;
import net.game.item.bag.BagManager;
import net.game.item.gem.GemColor;
import net.game.item.gem.GemManager;
import net.game.item.potion.PotionManager;
import net.game.item.stuff.Stuff;
import net.game.item.stuff.StuffManager;
import net.game.item.weapon.WeaponManager;

public class ItemManager {
	private static String getBagRequest;
	private static String setBagRequest;
	private static JDOStatement getBagItem;
	private static JDOStatement setBagItem;
	private static JDOStatement getEquippedBag;
	private static JDOStatement setEquippedBag;
	private static JDOStatement getEquippedItem;
	private static JDOStatement setEquippedItem;
	
	public static void initSQLRequest() {
		int x = 1;
		int i = 1;
		StringBuilder builderSetBagItemsRequest = new StringBuilder();
		while(i < 97) {
			x = 1;
			builderSetBagItemsRequest.append("slot"+Integer.toString(i)+" = ?, numberstack"+Integer.toString(i)+" = ?, ");
			while(x <= 3) {
				if(i == 96 && x == 3) {
					builderSetBagItemsRequest.append("slot"+Integer.toString(i)+"_gem"+Integer.toString(x)+" = ? ");
				}
				else {
					builderSetBagItemsRequest.append("slot"+Integer.toString(i)+"_gem"+Integer.toString(x)+" = ?, ");
				}
				x++;
			}
			i++;
		}
		setBagRequest = builderSetBagItemsRequest.toString();
		x = 1;
		i = 1;
		StringBuilder builderGetBagItemsRequest = new StringBuilder();
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
		getBagRequest = builderGetBagItemsRequest.toString();
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
		getBagItem.putInt(player.getCharacterId());
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
	
	public void getBagItems(Player player) throws SQLException {
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
		int numberBagItems = 0;
		getBagItem.clear();
		getBagItem.putInt(player.getCharacterId());
		getBagItem.execute();
		if(getBagItem.fetch()) {
			Connection connection = player.getConnectionManager().getConnection();
			connection.writeByte(PacketID.LOAD_BAG_ITEMS);
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
					numberBagItems++;
				}
				else if(PotionManager.exists(id)) {
					player.getBag().setBag(i, PotionManager.getClone(id));
					player.getBag().getNumberStack().put(player.getBag().getBag(i), number);
					numberBagItems++;
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
				if(player.getBag().getBag(i) != null) {
					if(player.getBag().getBag(i).isContainer() || player.getBag().getBag(i).isGem()) {
						connection.writeInt(i);
						connection.writeInt(player.getBag().getBag(i).getId());
						connection.writeChar(player.getBag().getBag(i).getItemType().getValue());
					}
					else if(player.getBag().getBag(i).isPotion()) {
						connection.writeInt(i);
						connection.writeInt(player.getBag().getBag(i).getId());
						connection.writeChar(player.getBag().getBag(i).getItemType().getValue());
						connection.writeInt(player.getBag().getNumberBagItem(player.getBag().getBag(i)));
					}
					else if(player.getBag().getBag(i).isStuff() || player.getBag().getBag(i).isWeapon()) {
						connection.writeInt(i);
						connection.writeInt(player.getBag().getBag(i).getId());
						connection.writeChar(player.getBag().getBag(i).getItemType().getValue());
						if(((Stuff)player.getBag().getBag(i)).getEquippedGem1() != null) {
							connection.writeInt(((Stuff)player.getBag().getBag(i)).getEquippedGem1().getId());
						}
						else {
							connection.writeInt(0);
						}
						if(((Stuff)player.getBag().getBag(i)).getEquippedGem2() != null) {
							connection.writeInt(((Stuff)player.getBag().getBag(i)).getEquippedGem2().getId());
						}
						else {
							connection.writeInt(0);
						}
						if(((Stuff)player.getBag().getBag(i)).getEquippedGem2() != null) {
							connection.writeInt(((Stuff)player.getBag().getBag(i)).getEquippedGem2().getId());
						}
						else {
							connection.writeInt(0);
						}
					}
				}
				i++;
			}
			connection.send();
		}
	}
	
	public void setBagItems(Player player) throws SQLException {
		int i = 0;
		if(setBagItem == null) {
			String request = "";
			StringBuilder builder = new StringBuilder();
			builder.append("UPDATE bag SET ");
			builder.append(setBagRequest);
			builder.append("WHERE character_id = ?");
			request = builder.toString();
			setBagItem = Server.getJDO().prepare(request);
		}
		setBagItem.clear();
		while(i < 96) {
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
					if(((Stuff)tempBag).getEquippedGem1() == null) {
						setBagItem.putInt(0);
					}
					else {
						setBagItem.putInt((((Stuff)tempBag).getEquippedGem1().getId()));
					}
					if(((Stuff)tempBag).getEquippedGem2() == null) {
						setBagItem.putInt(0);
					}
					else {
						setBagItem.putInt((((Stuff)tempBag).getEquippedGem2().getId()));
					}
					if(((Stuff)tempBag).getEquippedGem3() == null) {
						setBagItem.putInt(0);
					}
					else {
						setBagItem.putInt((((Stuff)tempBag).getEquippedGem3().getId()));
					}
				}
				else if(tempBag.isContainer() || tempBag.isGem()) {
					setBagItem.putInt(tempBag.getId());
					setBagItem.putInt(0);
					setBagItem.putInt(0);
					setBagItem.putInt(0);
					setBagItem.putInt(0);
				}
				else if(tempBag.isItem() || tempBag.isPotion()) {
					if(player.getBag().getNumberStack().containsKey(tempBag)) {
						setBagItem.putInt(tempBag.getId());
						setBagItem.putInt(player.getBag().getNumberStack().get(tempBag));
						setBagItem.putInt(0);
						setBagItem.putInt(0);
						setBagItem.putInt(0);
					}
					else {
						System.out.println("Error CharacterStuff/setBagItems");
						setBagItem.putInt(0);
						setBagItem.putInt(0);
						setBagItem.putInt(0);
						setBagItem.putInt(0);
						setBagItem.putInt(0);
					}
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
		setBagItem.putInt(player.getCharacterId());
		setBagItem.execute();
	}
	
	public void getEquippedBags(Player player) throws SQLException {
		if(getEquippedBag == null) {
			getEquippedBag = Server.getJDO().prepare("SELECT slot1, slot2, slot3, slot4 FROM character_containers WHERE character_id = ?");
		}
		getEquippedBag.clear();
		getEquippedBag.putInt(player.getCharacterId());
		getEquippedBag.execute();
		//Connection connection = player.getConnectionManager().getConnection();
		int i = 0;
		int id;
		if(getEquippedBag.fetch()) {
			while(i < player.getBag().getEquippedBag().length) {
				id = getEquippedBag.getInt();
				if(BagManager.exists(id)) {
					player.getBag().setEquippedBag(i, BagManager.getClone(id));
					//connection.writeContainer(BagManager.getContainer(id));
				}
				else {
					player.getBag().setEquippedBag(i, null);
					/*connection.writeInt(0);
					connection.writeString("");
					connection.writeString("");
					connection.writeInt(0);
					connection.writeInt(0);
					connection.writeInt(0);*/
				}
				i++;
			}
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
		setEquippedBag.putInt(player.getCharacterId());
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
				if(player.getStuff(i).getEquippedGem1() != null) {
					setEquippedItem.putInt(player.getStuff(i).getEquippedGem1().getId());
				}
				else {
					setEquippedItem.putInt(0);
				}
				if(player.getStuff(i).getEquippedGem2() != null) {
					setEquippedItem.putInt(player.getStuff(i).getEquippedGem2().getId());
				}
				else {
					setEquippedItem.putInt(0);
				}	
				if(player.getStuff(i).getEquippedGem3() != null) {
					setEquippedItem.putInt(player.getStuff(i).getEquippedGem3().getId());
				}
				else {
					setEquippedItem.putInt(0);
				}
			}
			i++;
		}
		setEquippedItem.putInt(player.getCharacterId());
		setEquippedItem.execute();
	}
	
	public void getEquippedItems(Player player) throws SQLException {
		int id;
		int gem1Id;
		int gem2Id;
		int gem3Id;
		Stuff temp;
		if(getEquippedItem == null) {
			getEquippedItem = Server.getJDO().prepare("SELECT head, head_gem1, head_gem2, head_gem3, necklace, necklace_gem1, necklace_gem2, necklace_gem3, shoulders, shoulders_gem1, shoulders_gem2, shoulders_gem3, back, back_gem1, back_gem2, back_gem3, chest, chest_gem1, chest_gem2, chest_gem3, wrists, wrists_gem1, wrists_gem2, wrists_gem3, gloves, gloves_gem1, gloves_gem2, gloves_gem3, belt, belt_gem1, belt_gem2, belt_gem3, leggings, leggings_gem1, leggings_gem2, leggings_gem3, boots, boots_gem1, boots_gem2, boots_gem3, ring, ring2, trinket, trinket2, mainhand, mainhand_gem1, mainhand_gem2, mainhand_gem3, offhand, offhand_gem1, offhand_gem2, offhand_gem3, ranged, ranged_gem1, ranged_gem2, ranged_gem3 FROM character_stuff WHERE character_id = ?");
		}
		getEquippedItem.clear();
		getEquippedItem.putInt(player.getCharacterId());
		getEquippedItem.execute();
		Connection connection = player.getConnectionManager().getConnection();
		if(getEquippedItem.fetch()) {
			connection.writeByte(PacketID.LOAD_EQUIPPED_ITEMS);
			id = getEquippedItem.getInt();
			gem1Id = getEquippedItem.getInt();
			gem2Id = getEquippedItem.getInt();
			gem3Id = getEquippedItem.getInt();
			connection.writeInt(id);
			connection.writeInt(gem1Id);
			connection.writeInt(gem2Id);
			connection.writeInt(gem3Id);
			if(StuffManager.exists(id) && StuffManager.getStuff(id).isHead() && player.canEquipStuff(StuffManager.getStuff(id))) {
				temp = StuffManager.getClone(id);
				player.setStuff(0, temp);
				setGems(temp, gem1Id, gem2Id, gem3Id);
			}
			id = getEquippedItem.getInt();
			gem1Id = getEquippedItem.getInt();
			gem2Id = getEquippedItem.getInt();
			gem3Id = getEquippedItem.getInt();
			connection.writeInt(id);
			connection.writeInt(gem1Id);
			connection.writeInt(gem2Id);
			connection.writeInt(gem3Id);
			if(StuffManager.exists(id) && StuffManager.getStuff(id).isNecklace() && player.canEquipStuff(StuffManager.getStuff(id))) {
				temp = StuffManager.getClone(id);
				player.setStuff(1, temp);
				setGems(temp, gem1Id, gem2Id, gem3Id);
			}
			id = getEquippedItem.getInt();
			gem1Id = getEquippedItem.getInt();
			gem2Id = getEquippedItem.getInt();
			gem3Id = getEquippedItem.getInt();
			connection.writeInt(id);
			connection.writeInt(gem1Id);
			connection.writeInt(gem2Id);
			connection.writeInt(gem3Id);
			if(StuffManager.exists(id) && StuffManager.getStuff(id).isShoulders() && player.canEquipStuff(StuffManager.getStuff(id))) {
				temp = StuffManager.getClone(id);
				player.setStuff(2, temp);
				setGems(temp, gem1Id, gem2Id, gem3Id);
			}
			id = getEquippedItem.getInt();
			gem1Id = getEquippedItem.getInt();
			gem2Id = getEquippedItem.getInt();
			gem3Id = getEquippedItem.getInt();
			connection.writeInt(id);
			connection.writeInt(gem1Id);
			connection.writeInt(gem2Id);
			connection.writeInt(gem3Id);
			if(StuffManager.exists(id) && StuffManager.getStuff(id).isBack() && player.canEquipStuff(StuffManager.getStuff(id))) {
				temp = StuffManager.getClone(id);
				player.setStuff(3, temp);
				setGems(temp, gem1Id, gem2Id, gem3Id);
			}
			id = getEquippedItem.getInt();
			gem1Id = getEquippedItem.getInt();
			gem2Id = getEquippedItem.getInt();
			gem3Id = getEquippedItem.getInt();
			connection.writeInt(id);
			connection.writeInt(gem1Id);
			connection.writeInt(gem2Id);
			connection.writeInt(gem3Id);
			if(StuffManager.exists(id) && StuffManager.getStuff(id).isChest() && player.canEquipStuff(StuffManager.getStuff(id))) {
				temp = StuffManager.getClone(id);
				player.setStuff(4, temp);
				setGems(temp, gem1Id, gem2Id, gem3Id);
			}
			id = getEquippedItem.getInt();
			gem1Id = getEquippedItem.getInt();
			gem2Id = getEquippedItem.getInt();
			gem3Id = getEquippedItem.getInt();
			connection.writeInt(id);
			connection.writeInt(gem1Id);
			connection.writeInt(gem2Id);
			connection.writeInt(gem3Id);
			if(StuffManager.exists(id) && StuffManager.getStuff(id).isWrists() && player.canEquipStuff(StuffManager.getStuff(id))) {
				temp = StuffManager.getClone(id);
				player.setStuff(7, temp);
				setGems(temp, gem1Id, gem2Id, gem3Id);
			}
			id = getEquippedItem.getInt();
			gem1Id = getEquippedItem.getInt();
			gem2Id = getEquippedItem.getInt();
			gem3Id = getEquippedItem.getInt();
			connection.writeInt(id);
			connection.writeInt(gem1Id);
			connection.writeInt(gem2Id);
			connection.writeInt(gem3Id);
			if(StuffManager.exists(id) && StuffManager.getStuff(id).isGloves() && player.canEquipStuff(StuffManager.getStuff(id))) {
				temp = StuffManager.getClone(id);
				player.setStuff(8, temp);
				setGems(temp, gem1Id, gem2Id, gem3Id);
			}
			id = getEquippedItem.getInt();
			gem1Id = getEquippedItem.getInt();
			gem2Id = getEquippedItem.getInt();
			gem3Id = getEquippedItem.getInt();
			connection.writeInt(id);
			connection.writeInt(gem1Id);
			connection.writeInt(gem2Id);
			connection.writeInt(gem3Id);
			if(StuffManager.exists(id) && StuffManager.getStuff(id).isBelt() && player.canEquipStuff(StuffManager.getStuff(id))) {
				temp = StuffManager.getClone(id);
				player.setStuff(9, temp);
				setGems(temp, gem1Id, gem2Id, gem3Id);
			}
			id = getEquippedItem.getInt();
			gem1Id = getEquippedItem.getInt();
			gem2Id = getEquippedItem.getInt();
			gem3Id = getEquippedItem.getInt();
			connection.writeInt(id);
			connection.writeInt(gem1Id);
			connection.writeInt(gem2Id);
			connection.writeInt(gem3Id);
			if(StuffManager.exists(id) && StuffManager.getStuff(id).isLeggings() && player.canEquipStuff(StuffManager.getStuff(id))) {
				temp = StuffManager.getClone(id);
				player.setStuff(10, temp);
				setGems(temp, gem1Id, gem2Id, gem3Id);
			}
			id = getEquippedItem.getInt();
			gem1Id = getEquippedItem.getInt();
			gem2Id = getEquippedItem.getInt();
			gem3Id = getEquippedItem.getInt();
			connection.writeInt(id);
			connection.writeInt(gem1Id);
			connection.writeInt(gem2Id);
			connection.writeInt(gem3Id);
			if(StuffManager.exists(id) && StuffManager.getStuff(id).isBoots() && player.canEquipStuff(StuffManager.getStuff(id))) {
				temp = StuffManager.getClone(id);
				player.setStuff(11, temp);
				setGems(temp, gem1Id, gem2Id, gem3Id);
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
			connection.writeInt(gem1Id);
			connection.writeInt(gem2Id);
			connection.writeInt(gem3Id);
			if(WeaponManager.exists(id) && player.canEquipWeapon(WeaponManager.getWeapon(id))) {
				temp = WeaponManager.getClone(id);
				player.setStuff(16, temp);
				setGems(temp, gem1Id, gem2Id, gem3Id);
			}
			id = getEquippedItem.getInt();
			gem1Id = getEquippedItem.getInt();
			gem2Id = getEquippedItem.getInt();
			gem3Id = getEquippedItem.getInt();
			connection.writeInt(id);
			connection.writeInt(gem1Id);
			connection.writeInt(gem2Id);
			connection.writeInt(gem3Id);
			if(WeaponManager.exists(id) && player.canEquipWeapon(WeaponManager.getWeapon(id))) {
				temp = WeaponManager.getClone(id);
				player.setStuff(17, temp);
				setGems(temp, gem1Id, gem2Id, gem3Id);
			}
			id = getEquippedItem.getInt();
			gem1Id = getEquippedItem.getInt();
			gem2Id = getEquippedItem.getInt();
			gem3Id = getEquippedItem.getInt();
			connection.writeInt(id);
			connection.writeInt(gem1Id);
			connection.writeInt(gem2Id);
			connection.writeInt(gem3Id);
			if(WeaponManager.exists(id) && WeaponManager.getWeapon(id).isRanged() && player.canEquipWeapon(WeaponManager.getWeapon(id))) {
				temp = WeaponManager.getClone(id);
				player.setStuff(18, temp);
				setGems(temp, gem1Id, gem2Id, gem3Id);
			}
			connection.send();
		}
		else {
			System.out.println("statement error (stuff load)");
		}
	}
	
	private static void setGems(Stuff stuff, int gem1Id, int gem2Id, int gem3Id) {
		if(stuff.getGemSlot1() != GemColor.NONE && GemManager.exists(gem1Id)) {
			stuff.setEquippedGem1(GemManager.getClone(gem1Id));
		}
		if(stuff.getGemSlot2() != GemColor.NONE && GemManager.exists(gem2Id)) {
			stuff.setEquippedGem1(GemManager.getClone(gem2Id));
		}
		if(stuff.getGemSlot3() != GemColor.NONE && GemManager.exists(gem3Id)) {
			stuff.setEquippedGem1(GemManager.getClone(gem3Id));
		}
	}
}

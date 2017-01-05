package tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

import jdo.JDO;
import jdo.JDOStatement;
import jdo.wrapper.MariaDB;
import net.connection.Buffer;
import net.game.ClassType;
import net.game.Wear;
import net.game.item.ItemType;
import net.game.item.bag.Container;
import net.game.item.bag.ContainerManager;
import net.game.item.gem.GemBonusType;
import net.game.item.gem.GemColor;
import net.game.item.gem.GemManager;
import net.game.item.potion.PotionManager;
import net.game.item.stuff.Stuff;
import net.game.item.stuff.StuffManager;
import net.game.item.stuff.StuffType;
import net.game.item.weapon.WeaponManager;
import net.game.item.weapon.WeaponSlot;
import net.game.item.weapon.WeaponType;
import net.game.manager.DatabaseMgr;

public class ItemCacheWDBFileCreator {

	private final static String FILE_PATH = "itemcache.wdb";
	private static Buffer writeBuffer;
	private final static byte[] HEADER_SIGNATURE = new byte[] {66, 68, 73, 87};
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		FileChannel out;
		JDO jdo = new MariaDB("127.0.0.1", DatabaseMgr.PORT, DatabaseMgr.TABLE_NAME, DatabaseMgr.USER_NAME, DatabaseMgr.PASSWORD);
		writeBuffer = new Buffer(1000000);
		JDOStatement loadItems = jdo.prepare(StuffManager.LOAD_STUFF_REQUEST);
		writeHeader();
		int position = writeBuffer.position();
		writeBuffer.writeInt(0);
		loadItems.execute();
		while(loadItems.fetch()) {
			int id = loadItems.getInt();
			String tempType = loadItems.getString();
			StuffType type = StuffManager.getType(tempType);
			String name = loadItems.getString();
			short classeTemp = loadItems.getShort();
			ClassType[] classeType = StuffManager.getClasses(classeTemp);
			String tempWear = loadItems.getString();
			Wear wear = StuffManager.getWear(tempWear);
			String sprite_id = loadItems.getString();
			int quality = loadItems.getInt();
			String tempColor = loadItems.getString();
			GemColor color1 = GemManager.convColor(tempColor);
			tempColor = loadItems.getString();
			GemColor color2 = GemManager.convColor(tempColor);
			tempColor = loadItems.getString();
			GemColor color3 = GemManager.convColor(tempColor);
			String tempBonusType = loadItems.getString();
			GemBonusType bonusType = StuffManager.convBonusType(tempBonusType);
			int bonusValue = loadItems.getInt();
			int level = loadItems.getInt();
			int armor = loadItems.getInt();
			int stamina = loadItems.getInt();
			int mana = loadItems.getInt();
			int critical = loadItems.getInt();
			int strength = loadItems.getInt();
			int sellPrice = loadItems.getInt();
			writeBuffer.writeByte(ItemType.STUFF.getValue());
			writeBuffer.writeStuff(new Stuff(type, classeType, sprite_id, id, name, quality, color1, color2, color3, bonusType, bonusValue, level, wear, critical, strength, stamina, armor, mana, sellPrice));
		}
		loadItems = jdo.prepare(WeaponManager.LOAD_WEAPON_REQUEST);
		loadItems.execute();
		while(loadItems.fetch()) {
			int id = loadItems.getInt();
			String name = loadItems.getString();
			String sprite_id = loadItems.getString();
			short classeTemp = loadItems.getShort();
			ClassType[] classeType = StuffManager.getClasses(classeTemp);
			String tempType = loadItems.getString();
			WeaponType type = WeaponManager.getType(tempType);
			String tempSlot = loadItems.getString();
			WeaponSlot slot = WeaponManager.getSlot(tempSlot);
			int quality = loadItems.getInt();
			String tempColor = loadItems.getString();
			GemColor color1 = GemManager.convColor(tempColor);
			tempColor = loadItems.getString();
			GemColor color2 = GemManager.convColor(tempColor);
			tempColor = loadItems.getString();
			GemColor color3 = GemManager.convColor(tempColor);
			String tempBonusType = loadItems.getString();
			GemBonusType bonusType = StuffManager.convBonusType(tempBonusType);
			int bonusValue = loadItems.getInt();
			int level = loadItems.getInt();
			int armor = loadItems.getInt();
			int stamina = loadItems.getInt();
			int mana = loadItems.getInt();
			int critical = loadItems.getInt();
			int strength = loadItems.getInt();
			int sellPrice = loadItems.getInt();
			writeBuffer.writeByte(ItemType.WEAPON.getValue());
			writeBuffer.writeWeapon(new Stuff(id, name, sprite_id, classeType, type, slot, quality, color1, color2, color3, bonusType, bonusValue, level, armor, stamina, mana, critical, strength, sellPrice));
		}
		loadItems = jdo.prepare(PotionManager.LOAD_POTION_REQUEST);
		loadItems.execute();
		while(loadItems.fetch()) {
			int id = loadItems.getInt();
			String sprite_id = loadItems.getString();
			String name = loadItems.getString();
			int level = loadItems.getInt();
			int heal = loadItems.getInt();
			int mana = loadItems.getInt();
			int sellPrice = loadItems.getInt();
			writeBuffer.writeByte(ItemType.POTION.getValue());
			writeBuffer.writeInt(id);
			writeBuffer.writeString(sprite_id);
			writeBuffer.writeString(name);
			writeBuffer.writeInt(level);
			writeBuffer.writeInt(heal);
			writeBuffer.writeInt(mana);
			writeBuffer.writeInt(sellPrice);
			writeBuffer.writeInt(1);
		}
		loadItems = jdo.prepare(ContainerManager.LOAD_CONTAINER_REQUEST);
		loadItems.execute();
		while(loadItems.fetch()) {
			int id = loadItems.getInt();
			String sprite_id = loadItems.getString();
			String name = loadItems.getString();
			int quality = loadItems.getInt();
			int size = loadItems.getInt();
			int sellPrice = loadItems.getInt();
			writeBuffer.writeByte(ItemType.CONTAINER.getValue());
			writeBuffer.writeContainer(new Container(id, sprite_id, name, quality, size, sellPrice));
		}
		loadItems = jdo.prepare(GemManager.LOAD_GEM_REQUEST);
		loadItems.execute();
		while(loadItems.fetch()) {
			int id = loadItems.getInt();
			String sprite_id = loadItems.getString();
			String name = loadItems.getString();
			int quality = loadItems.getInt();
			String tempColor = loadItems.getString();
			GemColor color = GemManager.convColor(tempColor);
			int sellPrice = loadItems.getInt();
			GemBonusType stat1Type = GemManager.convGemBonusType(loadItems.getString());
			int stat1Value = loadItems.getInt();
			GemBonusType stat2Type = GemManager.convGemBonusType(loadItems.getString());
			int stat2Value = loadItems.getInt();
			GemBonusType stat3Type = GemManager.convGemBonusType(loadItems.getString());
			int stat3Value = loadItems.getInt();
			writeBuffer.writeByte(ItemType.GEM.getValue());
			writeBuffer.writeInt(id);
			writeBuffer.writeString(sprite_id);
			writeBuffer.writeString(name);
			writeBuffer.writeInt(quality);
			writeBuffer.writeByte(color.getValue());
			writeBuffer.writeInt(sellPrice);
			writeBuffer.writeByte(stat1Type.getValue());
			writeBuffer.writeInt(stat1Value);
			writeBuffer.writeByte(stat2Type.getValue());
			writeBuffer.writeInt(stat2Value);
			writeBuffer.writeByte(stat3Type.getValue());
			writeBuffer.writeInt(stat3Value);
		}
		int endPosition = writeBuffer.position();
		writeBuffer.position(position);
		writeBuffer.writeInt(endPosition);
		writeBuffer.position(endPosition);
		writeBuffer.flip();
		writeBuffer.setOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			checkFileStatus();
			FileOutputStream outputStream = new FileOutputStream(FILE_PATH);
			out = outputStream.getChannel();
			out.write(writeBuffer.getBuffer());
			out.close();
			outputStream.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		System.out.println("File "+FILE_PATH+" created successfully, file size : "+writeBuffer.position()+" byte");
	}
	
	private static void writeHeader() {
		int i = 0;
		while(i < HEADER_SIGNATURE.length) {
			writeBuffer.writeByte(HEADER_SIGNATURE[i]);
			i++;
		}
	}
	
	private static void checkFileStatus() {
		File file = new File(FILE_PATH);
		if(!file.exists()) {
			try {
				file.createNewFile();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}

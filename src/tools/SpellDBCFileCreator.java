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
import net.game.manager.DatabaseMgr;
import net.game.spell.SpellMgr;

public class SpellDBCFileCreator {

	private final static String FILE_PATH = "Spell.dbc";
	private final static byte[] HEADER_SIGNATURE = new byte[] {(byte)87, (byte)68, (byte)66, (byte)67};
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		JDO jdo = new MariaDB("127.0.0.1", DatabaseMgr.PORT, DatabaseMgr.TABLE_NAME, DatabaseMgr.USER_NAME, DatabaseMgr.PASSWORD);
		FileChannel out;
		Buffer writeBuffer;
		JDOStatement loadNumberLine = jdo.prepare("SELECT COUNT(*) FROM spell");
		loadNumberLine.execute();
		int numberLine = 0;
		if(loadNumberLine.fetch()) {
			numberLine = loadNumberLine.getInt();
		}
		if(numberLine == 0) {
			System.out.println("Table `spell` is empty.");
			return;
		}
		writeBuffer = new Buffer(numberLine*200);
		int i = 0;
		while(i < HEADER_SIGNATURE.length) {
			writeBuffer.writeByte(HEADER_SIGNATURE[i]);
			i++;
		}
		int position = writeBuffer.position();
		writeBuffer.writeInt(0);
		writeBuffer.writeInt(0);
		int numberSpellLoaded = 0;
		JDOStatement loadSpells = jdo.prepare(SpellMgr.LOAD_SPELL_REQUEST);
		loadSpells.execute();
		while(loadSpells.fetch()) {
			writeBuffer.writeInt(loadSpells.getInt());
			writeBuffer.writeString(loadSpells.getString());
			writeBuffer.writeString(loadSpells.getString());
			writeBuffer.writeString(loadSpells.getString());
			writeBuffer.writeByte(loadSpells.getByte());
			writeBuffer.writeInt(loadSpells.getInt());
			writeBuffer.writeInt(loadSpells.getInt());
			writeBuffer.writeFloat(loadSpells.getFloat());
			writeBuffer.writeInt(loadSpells.getInt());
			writeBuffer.writeBoolean(loadSpells.getBoolean());
			writeBuffer.writeInt(loadSpells.getInt());
			writeBuffer.writeInt(loadSpells.getInt());
			writeBuffer.writeByte(SpellMgr.convStringToMagicalSchool(loadSpells.getString()).getValue());
			writeBuffer.writeBoolean(loadSpells.getBoolean());
			numberSpellLoaded++;
		}
		int endPosition = writeBuffer.position();
		writeBuffer.position(position);
		writeBuffer.writeInt(endPosition);
		writeBuffer.writeInt(numberSpellLoaded);
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

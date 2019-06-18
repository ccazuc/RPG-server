package tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

import jdo.JDO;
import jdo.JDOStatement;
import jdo.wrapper.MariaDB;
import net.connection.Buffer;
import net.game.aura.AuraMgr;
import net.game.manager.DatabaseMgr;

public class AuraDBCFileCreator {

	private final static String FILE_PATH = "Aura.dbc";
	private final static byte[] HEADER_SIGNATURE = new byte[] {'W', 'D', 'B', 'C'};
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException
	{
		JDO jdo = new MariaDB("127.0.0.1", DatabaseMgr.PORT, DatabaseMgr.TABLE_NAME, DatabaseMgr.USER_NAME, DatabaseMgr.PASSWORD);
		FileChannel out;
		Buffer writeBuffer;
		JDOStatement loadNumberLine = jdo.prepare("SELECT COUNT(*) FROM aura");
		loadNumberLine.execute();
		int numberLine = 0;
		if (loadNumberLine.fetch())
		{
			numberLine = loadNumberLine.getInt();
		}
		if (numberLine == 0)
		{
			System.out.println("Table `aura` is empty.");
			return;
		}
		writeBuffer = new Buffer(numberLine*200);
		int i = -1;
		while (++i < HEADER_SIGNATURE.length)
		{
			writeBuffer.writeByte(HEADER_SIGNATURE[i]);
		}
		int position = writeBuffer.position();
		writeBuffer.writeInt(0);
		writeBuffer.writeInt(0);
		int numberAuraLoaded = 0;
		JDOStatement loadAuras = jdo.prepare(AuraMgr.LOAD_AURA_REQUEST);
		loadAuras.execute();
		while (loadAuras.fetch())
		{
			writeBuffer.writeInt(loadAuras.getInt());
			writeBuffer.writeString(loadAuras.getString());
			writeBuffer.writeString(loadAuras.getString());
			writeBuffer.writeInt(loadAuras.getInt());
			writeBuffer.writeInt(loadAuras.getInt());
			writeBuffer.writeBoolean(loadAuras.getBoolean());
			writeBuffer.writeByte(loadAuras.getByte());
			writeBuffer.writeByte(loadAuras.getByte());
			writeBuffer.writeInt(loadAuras.getInt());
			writeBuffer.writeBoolean(loadAuras.getBoolean());
			writeBuffer.writeBoolean(loadAuras.getBoolean());
			writeBuffer.writeByte(AuraMgr.convStringToAuraEffect(loadAuras.getString()).getValue());
			writeBuffer.writeInt(loadAuras.getInt());
			writeBuffer.writeByte(AuraMgr.convStringToAuraEffect(loadAuras.getString()).getValue());
			writeBuffer.writeInt(loadAuras.getInt());
			writeBuffer.writeByte(AuraMgr.convStringToAuraEffect(loadAuras.getString()).getValue());
			writeBuffer.writeInt(loadAuras.getInt());
			writeBuffer.writeBoolean(loadAuras.getBoolean());
			writeBuffer.writeBoolean(loadAuras.getBoolean());
			writeBuffer.writeBoolean(loadAuras.getBoolean());
			writeBuffer.writeBoolean(loadAuras.getBoolean());
			numberAuraLoaded++;
		}
		int endPosition = writeBuffer.position();
		writeBuffer.position(position);
		writeBuffer.writeInt(endPosition);
		writeBuffer.writeInt(numberAuraLoaded);
		writeBuffer.position(endPosition);
		writeBuffer.flip();
		writeBuffer.setOrder(ByteOrder.LITTLE_ENDIAN);
		checkFileStatus();
		FileOutputStream outputStream;
		try
		{
			outputStream = new FileOutputStream(FILE_PATH);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return;
		}
		out = outputStream.getChannel();
		try
		{
			out.write(writeBuffer.getBuffer());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
		finally
		{
			out.close();
			outputStream.close();
		}
		System.out.println("File "+FILE_PATH+" created successfully, file size : "+writeBuffer.position()+" byte");
	}
	
	private static void checkFileStatus()
	{
		File file = new File(FILE_PATH);
		if (!file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
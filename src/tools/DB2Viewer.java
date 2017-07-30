package tools;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import net.connection.Buffer;

public class DB2Viewer {

	private final static String FOLDER_PATH = "Data/";
	private final static String FILE_PATH = "Data/Spell.dbc";
	private static Buffer readBuffer;
	private static Buffer readBufferHeader = new Buffer(12);
	private final static byte[] HEADER_SIGNATURE = new byte[] {(byte)87, (byte)68, (byte)66, (byte)67};

	public static void main(String[] args) {
		try {
			if(!checkFileStatus()) {
				System.out.println("**ERROR** "+FILE_PATH+" not found, the game will now exit.");
				return;
			}
			FileChannel fc = (FileChannel) Files.newByteChannel(Paths.get(FILE_PATH), StandardOpenOption.READ);
			readBufferHeader.setOrder(ByteOrder.BIG_ENDIAN);
			fc.read(readBufferHeader.getBuffer());
			readBufferHeader.flip();
			int i = 0;
			while(i < HEADER_SIGNATURE.length) {
				if(readBufferHeader.readByte() != HEADER_SIGNATURE[i]) {
					System.out.println("**ERROR** "+FILE_PATH+" invalid signature, the game will now exit.");
					return;
				}
				i++;
			}
			int fileSize = readBufferHeader.readInt();
			int numberSpell = readBufferHeader.readInt();
			readBuffer = new Buffer(fileSize);
			readBuffer.setOrder(ByteOrder.BIG_ENDIAN);
			fc.read(readBuffer.getBuffer());
			readBuffer.flip();
			i = 0;
			while(readBuffer.hasRemaining()) {
				i++;
			}
			if(i != numberSpell) {
				System.out.println("**ERROR** "+FILE_PATH+" number of spell is not correct, the game will now exit.");
				return;
			}
		}
		catch(IOException e) {
			
		}
	}
	
	private static boolean checkFileStatus() {
		File folder = new File(FOLDER_PATH);
		if(!folder.exists()) {
			return false;
		}
		File file = new File(FILE_PATH);
		if(!file.exists()) {
			return false;
		}
		return true;
	}
}

package net.connection;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import net.game.Player;
import net.game.item.Item;
import net.game.item.bag.Container;
import net.game.item.gem.Gem;
import net.game.item.potion.Potion;
import net.game.item.stuff.Stuff;
import net.utils.Color;

public class Connection {

	private Buffer wBuffer;
	private Buffer rBuffer;
	private int startPacketPosition;
	private SocketChannel socket;
	
	public Connection(SocketChannel socket, Player player) {
		this.socket = socket;
		this.wBuffer = new Buffer(socket, player);
		this.rBuffer = new Buffer(socket, player);
	}
	
	public Connection(SocketChannel socket) {
		this.socket = socket;
		this.wBuffer = new Buffer(socket);
		this.rBuffer = new Buffer(socket);
	}
	
	public void setSocket(SocketChannel socket) {
		this.socket = socket;
	}

	public int wBufferRemaining() {
		return this.wBuffer.remaining();
	}
	
	public int wBufferCapacity() {
		return this.wBuffer.capacity();
	}
	
	public int wBufferPosition() {
		return this.wBuffer.getPosition();
	}
	
	public void wBufferSetPosition(int position) {
		this.wBuffer.setPosition(position);
	}
	
	public int rBufferPosition() {
		return this.rBuffer.getPosition();
	}
	
	public int rBufferRemaining() {
		return this.rBuffer.remaining();
	}
	
	public void rBufferSetPosition(int position) {
		this.rBuffer.setPosition(position);
	}
	
	public final void close() {
		try {
			this.socket.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public final String getIpAdress() {
		return this.socket.socket().getInetAddress().toString();
	}
	
	public final void clearRBuffer() {
		this.rBuffer.clear();
	}
	
	public final void flipRBuffer() {
		this.rBuffer.flip();
	}
	
	public final byte read() throws IOException {
		return this.rBuffer.read();
	}
	
	public final void send() {
		try {
			this.wBuffer.send();
		} 
		catch (IOException e) {
			//e.printStackTrace();
			System.out.println("IOException on send");
		}
	}
	
	public final void startPacket() {
		this.startPacketPosition = this.wBuffer.getPosition();
		writeInt(0);
	}
	
	public final void endPacket() {
		int position = this.wBuffer.getPosition();
		this.wBuffer.setPosition(this.startPacketPosition);
		this.wBuffer.writeInt(position-this.startPacketPosition);
		this.wBuffer.setPosition(position);
	}
	
	public final boolean hasRemaining() {
		return this.rBuffer.hasRemaining();
	}
	
	public final void writeItem(final Item item) {
		synchronized(this.wBuffer) {
			this.wBuffer.writeItem(item);
		}
	}
	
	public final void writeStuff(final Stuff stuff) {
		synchronized(this.wBuffer) {
			this.wBuffer.writeStuff(stuff);
		}
	}
	
	public final void writeGem(final Gem gem) {
		synchronized(this.wBuffer) {
			this.wBuffer.writeGem(gem);
		}
	}
	
	public final void writePotion(final Potion potion) {
		synchronized(this.wBuffer) {
			this.wBuffer.writePotion(potion);
		}
	}
	
	public final void writeWeapon(final Stuff weapon) {
		synchronized(this.wBuffer) {
			this.wBuffer.writeWeapon(weapon);
		}
	}
	
	public final void writeContainer(final Container bag) {
		synchronized(this.wBuffer) {
			this.wBuffer.writeContainer(bag);
		}
	}
	
	public final void writeColor(final Color color) {
		synchronized(this.wBuffer) {
			this.wBuffer.writeColor(color);
		}
	}
	
	public final void writeBoolean(final boolean b) {
		synchronized(this.wBuffer) {
			this.wBuffer.writeBoolean(b);
		}
	}
	
	public final boolean readBoolean() {
		return this.rBuffer.readBoolean();
	}
	
	public final void writeByte(final byte b) {
		synchronized(this.wBuffer) {
			this.wBuffer.writeByte(b);
		}
	}
	
	public final byte readByte() {
		return this.rBuffer.readByte();
	}
	
	public final void writeShort(final short s) {
		synchronized(this.wBuffer) {
			this.wBuffer.writeShort(s);
		}
	}
	
	public final short readShort() {
		return this.rBuffer.readShort();
	}
	
	public final void writeInt(final int i) {
		synchronized(this.wBuffer) {
			this.wBuffer.writeInt(i);
		}
	}
	
	public final int readInt() {
		return this.rBuffer.readInt();
	}
	
	public final void writeLong(final long l) {
		synchronized(this.wBuffer) {
			this.wBuffer.writeLong(l);
		}
	}
	
	public final long readLong() {
		return this.rBuffer.readLong();
	}
	
	public final void writeFloat(final float f) {
		synchronized(this.wBuffer) {
			this.wBuffer.writeFloat(f);
		}
	}
	
	public final float readFloat() {
		return this.rBuffer.readFloat();
	}
	
	public final void writeDouble(final double d) {
		synchronized(this.wBuffer) {
			this.wBuffer.writeDouble(d);
		}
	}
	
	public final double readDouble() {
		return this.rBuffer.readDouble();
	}
	
	public final void writeChar(final char c) {
		synchronized(this.wBuffer) {
			this.wBuffer.writeChar(c);
		}
	}
	
	public final char readChar() {
		return this.rBuffer.readChar();
	}
	
	public final void writeString(final String s) {
		synchronized(this.wBuffer) {
			this.wBuffer.writeString(s);
		}
	}
	
	public final String readString() {
		return this.rBuffer.readString();
	}
}


package net.connection;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import net.game.Player;
import net.game.item.Item;
import net.game.item.bag.Container;
import net.game.item.gem.Gem;
import net.game.item.potion.Potion;
import net.game.item.stuff.Stuff;

public class Connection {

	private Buffer wBuffer;
	private Buffer rBuffer;
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
	
	public final byte read() throws IOException {
		return this.rBuffer.read();
	}
	
	public final void send() {
		try {
			this.wBuffer.send();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public final boolean hasRemaining() {
		return this.rBuffer.hasRemaining();
	}
	
	public final void writeItem(final Item item) {
		this.wBuffer.writeItem(item);
	}
	
	public final void writeStuff(final Stuff stuff) {
		this.wBuffer.writeStuff(stuff);
	}
	
	public final void writeGem(final Gem gem) {
		this.wBuffer.writeGem(gem);
	}
	
	public final void writePotion(final Potion potion) {
		this.wBuffer.writePotion(potion);
	}
	
	public final void writeWeapon(final Stuff weapon) {
		this.wBuffer.writeWeapon(weapon);
	}
	
	public final void writeContainer(final Container bag) {
		this.wBuffer.writeContainer(bag);
	}
	
	public final void writeBoolean(final boolean b) {
		this.wBuffer.writeBoolean(b);
	}
	
	public final boolean readBoolean() {
		return this.rBuffer.readBoolean();
	}
	
	public final void writeByte(final byte b) {
		this.wBuffer.writeByte(b);
	}
	
	public final byte readByte() {
		return this.rBuffer.readByte();
	}
	
	public final void writeShort(final short s) {
		this.wBuffer.writeShort(s);
	}
	
	public final short readShort() {
		return this.rBuffer.readShort();
	}
	
	public final void writeInt(final int i) {
		this.wBuffer.writeInt(i);
	}
	
	public final int readInt() {
		return this.rBuffer.readInt();
	}
	
	public final void writeLong(final long l) {
		this.wBuffer.writeLong(l);
	}
	
	public final long readLong() {
		return this.rBuffer.readLong();
	}
	
	public final void writeFloat(final float f) {
		this.wBuffer.writeFloat(f);
	}
	
	public final float readFloat() {
		return this.rBuffer.readFloat();
	}
	
	public final void writeDouble(final double d) {
		this.wBuffer.writeDouble(d);
	}
	
	public final double readDouble() {
		return this.rBuffer.readDouble();
	}
	
	public final void writeChar(final char c) {
		this.wBuffer.writeChar(c);
	}
	
	public final char readChar() {
		return this.rBuffer.readChar();
	}
	
	public final void writeString(final String s) {
		this.wBuffer.writeString(s);
	}
	
	public final String readString() {
		return this.rBuffer.readString();
}
	
}

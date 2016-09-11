package net.connection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

public class Buffer {

	private ByteBuffer buffer;	
	private boolean written;
	private SocketChannel socket;
	
	public Buffer(SocketChannel socket) {
		buffer = ByteBuffer.allocateDirect(16000);
		this.socket = socket;
	}
	
	protected final void send() throws IOException {
		if(this.socket.isOpen()) {
			if(this.written) {
				send(this.buffer);
				this.written = false;
			}
		}
		else {
			throw new ClosedChannelException();
		}
	}

	private final void send(final ByteBuffer buffer) throws IOException {
		buffer.flip();
		while(buffer.hasRemaining()) {
			this.socket.write(buffer);
		}
		buffer.clear();
	}

	protected final byte read() throws IOException {
		return read(this.buffer);
	}
	
	private final byte read(final ByteBuffer buffer) throws IOException {
		buffer.clear();
		if(this.socket.read(buffer) >= 0) {
			buffer.flip();
			return 1;
		}
		return 2;
	}
	
	protected final boolean hasRemaining() {
		return this.buffer.hasRemaining();
	}

	protected final void writeString(final String s) {
		writeShort((short)s.length());
		int i = -1;
		while(++i < s.length()) {
			writeChar(s.charAt(i));
		}
		this.written = true;
	}
	
	protected final String readString() {
		final short length = readShort();
		final char[] chars = new char[length];
		int i = -1;
		while(++i < length) {
			chars[i] = readChar();
		}
		return new String(chars);
	}
	
	protected final void clear() {
		this.buffer.clear();
	}
	
	protected final void writeBoolean(final boolean b) {
		this.buffer.put((byte)(b?1:0));
		this.written = true;
	}
	
	protected final boolean readBoolean() {
		return this.buffer.get() == 1;
	}
	
	protected final void writeByte(final byte b) {
		this.buffer.put(b);
		this.written = true;
	}
	
	protected final byte readByte() {
		return this.buffer.get();
	}
	
	protected final void writeShort(final short s) {
		this.buffer.putShort(s);
		this.written = true;
	}
	
	protected final short readShort() {
		return this.buffer.getShort();
	}
	
	protected final void writeInt(final int i) {
		this.buffer.putInt(i);
		this.written = true;
	}
	
	protected final int readInt() {
		return this.buffer.getInt();
	}
	
	protected final void writeLong(final long l) {
		this.buffer.putLong(l);
		this.written = true;
	}
	
	protected final long readLong() {
		return this.buffer.getLong();
	}
	
	protected final void writeFloat(final float f) {
		this.buffer.putFloat(f);
		this.written = true;
	}
	
	protected final float readFloat() {
		return this.buffer.getFloat();
	}
	
	protected final void writeDouble(final double d) {
		this.buffer.putDouble(d);
		this.written = true;
	}
	
	protected final double readDouble() {
		return this.buffer.getDouble();
	}
	
	protected final void writeChar(final char c) {
		this.buffer.putChar((char)(Character.MAX_VALUE-c));
		this.written = true;
	}
	
	protected final char readChar() {
		return (char)(Character.MAX_VALUE-this.buffer.getChar());
}
}

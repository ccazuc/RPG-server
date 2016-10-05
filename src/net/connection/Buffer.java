package net.connection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

import net.game.item.bag.Container;
import net.game.item.gem.Gem;
import net.game.item.potion.Potion;
import net.game.item.stuff.Stuff;

public class Buffer {

	private ByteBuffer buffer;	
	private boolean written;
	private SocketChannel socket;
	
	public Buffer(SocketChannel socket) {
		this.buffer = ByteBuffer.allocateDirect(16000);
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
		if(this.socket.read(buffer) >= 1) {
			buffer.flip();
			return 1;
		}
		return 2;
	}
	
	protected final boolean hasRemaining() {
		return this.buffer.hasRemaining();
	}
	
	protected final void writeStuff(final Stuff stuff) {
		int i = 0;
		writeChar(stuff.getType().getValue());
		writeInt(stuff.getClassType().length);
		while(i < stuff.getClassType().length) {
			writeChar(stuff.getClassType(i).getValue());
			i++;
		}
		writeString(stuff.getSpriteId());
		writeInt(stuff.getId());
		writeString(stuff.getStuffName());
		writeInt(stuff.getQuality());
		writeChar(stuff.getGemSlot1().getValue());
		writeChar(stuff.getGemSlot2().getValue());
		writeChar(stuff.getGemSlot3().getValue());
		writeChar(stuff.getGemBonusType().getValue());
		writeInt(stuff.getGemBonusValue());
		writeInt(stuff.getLevel());
		writeChar(stuff.getWear().getValue());
		writeInt(stuff.getCritical());
		writeInt(stuff.getStrength());
		writeInt(stuff.getStamina());
		writeInt(stuff.getArmor());
		writeInt(stuff.getMana());
		writeInt(stuff.getSellPrice());
	}
	
	protected final void writeGem(final Gem gem) {
		writeInt(gem.getId());
		writeString(gem.getSpriteId());
		writeString(gem.getStuffName());
		writeInt(gem.getQuality());
		writeChar(gem.getColor().getValue());
		writeInt(gem.getStrength());
		writeInt(gem.getStamina());
		writeInt(gem.getArmor());
		writeInt(gem.getMana());
		writeInt(gem.getCritical());
		writeInt(gem.getSellPrice());
	}
	
	protected final void writePotion(final Potion potion) {
		writeInt(potion.getId());
		writeString(potion.getSpriteId());
		writeString(potion.getStuffName());
		writeInt(potion.getLevel());
		writeInt(potion.getPotionHeal());
		writeInt(potion.getPotionMana());
		writeInt(potion.getSellPrice());
	}
	
	protected final void writeWeapon(final Stuff weapon) {
		int i = 0;
		writeInt(weapon.getId());
		writeString(weapon.getStuffName());
		writeString(weapon.getSpriteId());
		writeInt(weapon.getClassType().length);
		while(i < weapon.getClassType().length) {
			writeChar(weapon.getClassType(i).getValue());
			i++;
		}
		writeChar(weapon.getWeaponType().getValue());
		writeChar(weapon.getWeaponSlot().getValue());
		writeInt(weapon.getQuality());
		writeChar(weapon.getGemSlot1().getValue());
		writeChar(weapon.getGemSlot2().getValue());
		writeChar(weapon.getGemSlot3().getValue());
		writeChar(weapon.getGemBonusType().getValue());
		writeInt(weapon.getGemBonusValue());
		writeInt(weapon.getLevel());
		writeInt(weapon.getArmor());
		writeInt(weapon.getStamina());
		writeInt(weapon.getMana());
		writeInt(weapon.getCritical());
		writeInt(weapon.getStrength());
		writeInt(weapon.getSellPrice());
	}
	
	protected final void writeContainer(final Container bag) {
		writeInt(bag.getId());
		writeString(bag.getStuffName());
		writeString(bag.getSpriteId());
		writeInt(bag.getQuality());
		writeInt(bag.getSize());
		writeInt(bag.getSellPrice());
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

package net.connection;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

import net.game.Player;
import net.game.item.Item;
import net.game.item.ItemType;
import net.game.item.bag.Container;
import net.game.item.gem.Gem;
import net.game.item.potion.Potion;
import net.game.item.stuff.Stuff;

public class Buffer {

	private ByteBuffer buffer;	
	private boolean written;
	private SocketChannel socket;
	private Player player;
	
	public Buffer(SocketChannel socket, Player player) {
		this.buffer = ByteBuffer.allocateDirect(16000);
		this.socket = socket;
		this.player = player;
	}
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
	
	public final int remaining() {
		return this.buffer.remaining();
	}
	
	public final int capacity() {
		return this.buffer.capacity();
	}

	private final void send(final ByteBuffer buffer) {
		buffer.flip();
		//System.out.println("FLIPPED BUFFER");
		try {
			while(buffer.hasRemaining()) {
				this.socket.write(buffer);
				//System.out.println("WRITE IN BUFFER");
			}
		}
		catch(IOException e) {
			buffer.clear();
			e.printStackTrace();
		}
		//System.out.println("CLEARED BUFFER");
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
	
	protected final void writeItem(final Item item) {
		this.player.addItemSentToClient(item.getId());
		writeChar(item.getItemType().getValue());
		if(item.getItemType() == ItemType.CONTAINER) {
			writeContainer((Container)item);
		}
		else if(item.getItemType() == ItemType.GEM) {
			writeGem((Gem)item);
		}
		else if(item.getItemType() == ItemType.ITEM) {
			//
		}
		else if(item.getItemType() == ItemType.POTION) {
			writePotion((Potion)item);
		}
		else if(item.getItemType() == ItemType.STUFF) {
			writeStuff((Stuff)item);
		}
		else if(item.getItemType() == ItemType.WEAPON) {
			writeWeapon((Stuff)item);
		}
		this.written = true;
	}
	
	protected final void writeStuff(final Stuff stuff) {
		this.player.addItemSentToClient(stuff.getId());
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
		this.written = true;
	}
	
	protected final void writeGem(final Gem gem) {
		this.player.addItemSentToClient(gem.getId());
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
		this.written = true;
	}
	
	protected final void writePotion(final Potion potion) {
		this.player.addItemSentToClient(potion.getId());
		writeInt(potion.getId());
		writeString(potion.getSpriteId());
		writeString(potion.getStuffName());
		writeInt(potion.getLevel());
		writeInt(potion.getPotionHeal());
		writeInt(potion.getPotionMana());
		writeInt(potion.getSellPrice());
		writeInt(potion.getAmount());
		this.written = true;
	}
	
	protected final void writeWeapon(final Stuff weapon) {
		this.player.addItemSentToClient(weapon.getId());
		int i = 0;
		writeInt(weapon.getId());
		writeString(weapon.getStuffName());
		writeString(weapon.getSpriteId());
		writeInt(weapon.getClassType().length);
		while(i < weapon.getClassType().length) {
			System.out.println(weapon.getClassType(i)+" classType");
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
		this.written = true;
	}
	
	protected final void writeContainer(final Container bag) {
		this.player.addItemSentToClient(bag.getId());
		writeInt(bag.getId());
		writeString(bag.getStuffName());
		writeString(bag.getSpriteId());
		writeInt(bag.getQuality());
		writeInt(bag.getSize());
		writeInt(bag.getSellPrice());
		this.written = true;
	}

	protected final void writeString(final String s) {
		writeShort((short)s.length());
		int i = -1;
		while(++i < s.length()) {
			try {
				writeChar(s.charAt(i));
			}
			catch(BufferOverflowException e) {
				e.printStackTrace();
				System.out.println("String that caused overflow: "+s+" remaining: "+this.buffer.remaining()+" position: "+this.buffer.position());
			}
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
		try {
			return this.buffer.get() == 1;
		}
		catch(BufferUnderflowException e) {
			e.printStackTrace();
			this.player.close();
			return false;
		}
	}
	
	protected final void writeByte(final byte b) {
		this.buffer.put(b);
		this.written = true;
	}
	
	protected final byte readByte() {
		try {
			return this.buffer.get();
		}
		catch(BufferUnderflowException e) {
			e.printStackTrace();
			this.player.close();
			return 0;
		}
	}
	
	protected final void writeShort(final short s) {
		try {
			this.buffer.putShort(s);
		}
		catch(BufferOverflowException e) {
			e.printStackTrace();
				System.out.println("Short that caused overflow: "+s+" remaining: "+this.buffer.remaining()+" position: "+this.buffer.position());
		}
		this.written = true;
	}
	
	protected final short readShort() {
		try {
			return this.buffer.getShort();
		}
		catch(BufferUnderflowException e) {
			e.printStackTrace();
			this.player.close();
			return 0;
		}
	}
	
	protected final void writeInt(final int i) {
		this.buffer.putInt(i);
		this.written = true;
	}
	
	protected final int readInt() {
		try {
			return this.buffer.getInt();
		}
		catch(BufferUnderflowException e) {
			e.printStackTrace();
			this.player.close();
			return 0;
		}
	}
	
	protected final void writeLong(final long l) {
		this.buffer.putLong(l);
		this.written = true;
	}
	
	protected final long readLong() {
		try {
			return this.buffer.getLong();
		}
		catch(BufferUnderflowException e) {
			e.printStackTrace();
			this.player.close();
			return 0;
		}
	}
	
	protected final void writeFloat(final float f) {
		this.buffer.putFloat(f);
		this.written = true;
	}
	
	protected final float readFloat() {
		try {
			return this.buffer.getFloat();
		}
		catch(BufferUnderflowException e) {
			e.printStackTrace();
			this.player.close();
			return 0;
		}
	}
	
	protected final void writeDouble(final double d) {
		this.buffer.putDouble(d);
		this.written = true;
	}
	
	protected final double readDouble() {
		try {
			return this.buffer.getDouble();
		}
		catch(BufferUnderflowException e) {
			e.printStackTrace();
			this.player.close();
			return 0;
		}
	}
	
	protected final void writeChar(final char c) {
		try {
			this.buffer.putChar((char)(Character.MAX_VALUE-c));
		}
		catch(BufferOverflowException e) {
			e.printStackTrace();
			System.out.println("Char that caused the overflow exception; "+c+" "+(int)c+" remaining: "+this.buffer.remaining()+" position: "+this.buffer.position());
		}
		this.written = true;
	}
	
	protected final char readChar() {
		try {
			return (char)(Character.MAX_VALUE-this.buffer.getChar());
		}
		catch(BufferUnderflowException e) {
			e.printStackTrace();
			this.player.close();
			return 0;
		}
	}
}

package net.connection;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

import net.Server;
import net.game.item.Item;
import net.game.item.ItemType;
import net.game.item.bag.Container;
import net.game.item.gem.Gem;
import net.game.item.potion.Potion;
import net.game.item.stuff.Stuff;
import net.game.unit.Player;
import net.utils.Color;

public class Buffer {

	private ByteBuffer buffer;	
	private boolean written;
	private SocketChannel socket;
	private Player player;
	private long lastEmptySentTimer;
	private static final long TIMEOUT_TIMER = 20000;
	
	public Buffer(SocketChannel socket, Player player) {
		this.buffer = ByteBuffer.allocateDirect(16000);
		this.socket = socket;
		this.player = player;
	}
	
	public Buffer(SocketChannel socket) {
		this.buffer = ByteBuffer.allocateDirect(16000);
		this.socket = socket;
	}
	
	public Buffer() {
		this.buffer = ByteBuffer.allocateDirect(16000);
	}
	
	public Buffer(int capacity) {
		this.buffer = ByteBuffer.allocateDirect(capacity);
	}
	
	protected final void send() {
		synchronized(this) {
			if(this.socket.isOpen()) {
				if(this.written) {
					send(this.buffer);
					this.written = false;
				}
			}
			else {
				//throw new ClosedChannelException();
			}
		}
	}

	private final void send(final ByteBuffer buffer) {
		buffer.flip();
		//System.out.println("FLIPPED BUFFER");
		try {
			int sent = 0;
			if(buffer.hasRemaining()) {
				sent = this.socket.write(buffer);
				if(sent == 0 && this.player != null) {
					if(this.lastEmptySentTimer != 0) {
						if(Server.getLoopTickTimer()-this.lastEmptySentTimer >= TIMEOUT_TIMER) {
							this.player.close();
						}
					}
					else {
						this.lastEmptySentTimer = Server.getLoopTickTimer();
					}
				}
				else if(sent != 0) {
					this.lastEmptySentTimer = 0;
				}
				//System.out.println("WRITE IN BUFFER");
			}
		}
		catch(IOException e) {
			buffer.clear();
			//e.printStackTrace();
		}
		//System.out.println("CLEARED BUFFER");
		buffer.clear();
	}

	protected final byte read() throws IOException {
		return read(this.buffer);
	}
	
	private final byte read(final ByteBuffer buffer) throws IOException {
		if(!buffer.hasRemaining()) {
			buffer.clear();
		}
		if(this.socket.read(buffer) >= 0) {
			buffer.flip();
			return 1;
		}
		return 2;
	}
	
	public final int remaining() {
		return this.buffer.remaining();
	}
	
	public final int capacity() {
		return this.buffer.capacity();
	}
	
	public final boolean hasRemaining() {
		return this.buffer.hasRemaining();
	}
	
	public final void flip() {
		this.buffer.flip();
	}
	
	protected final void writeItem(final Item item) {
		if(this.player != null) {
			this.player.addItemSentToClient(item.getId());
		}
		writeByte(item.getItemType().getValue());
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
	
	public final void writeStuff(final Stuff stuff) {
		if(this.player != null) {
			this.player.addItemSentToClient(stuff.getId());
		}
		int i = 0;
		writeByte(stuff.getType().getValue());
		writeInt(stuff.getClassType().length);
		while(i < stuff.getClassType().length) {
			writeByte(stuff.getClassType(i).getValue());
			i++;
		}
		writeString(stuff.getSpriteId());
		writeInt(stuff.getId());
		writeString(stuff.getStuffName());
		writeByte(stuff.getQuality().getValue());
		writeByte(stuff.getGemSlot1().getValue());
		writeByte(stuff.getGemSlot2().getValue());
		writeByte(stuff.getGemSlot3().getValue());
		writeByte(stuff.getGemBonusType().getValue());
		writeInt(stuff.getGemBonusValue());
		writeByte(stuff.getLevel());
		writeByte(stuff.getWear().getValue());
		writeInt(stuff.getCritical());
		writeInt(stuff.getStrength());
		writeInt(stuff.getStamina());
		writeInt(stuff.getArmor());
		writeInt(stuff.getMana());
		writeInt(stuff.getSellPrice());
		this.written = true;
	}
	
	public final void writeGem(final Gem gem) {
		if(this.player != null) {
			this.player.addItemSentToClient(gem.getId());
		}
		writeInt(gem.getId());
		writeString(gem.getSpriteId());
		writeString(gem.getStuffName());
		writeByte(gem.getQuality().getValue());
		writeByte(gem.getColor().getValue());
		writeInt(gem.getSellPrice());
		writeByte(gem.getBonus1Type().getValue());
		writeInt(gem.getBonus1Value());
		writeByte(gem.getBonus2Type().getValue());
		writeInt(gem.getBonus2Value());
		writeByte(gem.getBonus3Type().getValue());
		writeInt(gem.getBonus3Value());
		this.written = true;
	}
	
	public final void writePotion(final Potion potion) {
		if(this.player != null) {
			this.player.addItemSentToClient(potion.getId());
		}
		writeInt(potion.getId());
		writeString(potion.getSpriteId());
		writeString(potion.getStuffName());
		writeByte(potion.getLevel());
		writeInt(potion.getPotionHeal());
		writeInt(potion.getPotionMana());
		writeInt(potion.getSellPrice());
		writeInt(potion.getAmount());
		this.written = true;
	}
	
	public final void writeWeapon(final Stuff weapon) {
		if(this.player != null) {
			this.player.addItemSentToClient(weapon.getId());
		}
		int i = 0;
		writeInt(weapon.getId());
		writeString(weapon.getStuffName());
		writeString(weapon.getSpriteId());
		writeInt(weapon.getClassType().length);
		while(i < weapon.getClassType().length) {
			writeByte(weapon.getClassType(i).getValue());
			i++;
		}
		writeByte(weapon.getWeaponType().getValue());
		writeByte(weapon.getWeaponSlot().getValue());
		writeByte(weapon.getQuality().getValue());
		writeByte(weapon.getGemSlot1().getValue());
		writeByte(weapon.getGemSlot2().getValue());
		writeByte(weapon.getGemSlot3().getValue());
		writeByte(weapon.getGemBonusType().getValue());
		writeInt(weapon.getGemBonusValue());
		writeByte(weapon.getLevel());
		writeInt(weapon.getArmor());
		writeInt(weapon.getStamina());
		writeInt(weapon.getMana());
		writeInt(weapon.getCritical());
		writeInt(weapon.getStrength());
		writeInt(weapon.getSellPrice());
		this.written = true;
	}
	
	public final void writeContainer(final Container bag) {
		if(this.player != null) {
			this.player.addItemSentToClient(bag.getId());
		}
		writeInt(bag.getId());
		writeString(bag.getStuffName());
		writeString(bag.getSpriteId());
		writeByte(bag.getQuality().getValue());
		writeByte(bag.getSize());
		writeInt(bag.getSellPrice());
		this.written = true;
	}
	
	protected final void writeColor(final Color color) {
		writeFloat(color.getRed());
		writeFloat(color.getGreen());
		writeFloat(color.getBlue());
		writeFloat(color.getAlpha());
		this.written = true;
	}

	public final void writeString(final String s) {
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
	
	public final int position() {
		return this.buffer.position();
	}
	
	public final void position(int position) {
		this.buffer.position(position);
	}
	
	public final void setOrder(ByteOrder order) {
		this.buffer.order(order);
	}
	
	public final ByteBuffer getBuffer() {
		return this.buffer;
	}
	
	public final String readString() {
		final short length = readShort();
		final char[] chars = new char[length];
		int i = -1;
		while(++i < length) {
			chars[i] = readChar();
		}
		return new String(chars);
	}
	
	public final void clear() {
		this.buffer.clear();
	}
	
	public final void writeBoolean(final boolean b) {
		try
		{
			this.buffer.put((byte)(b?1:0));
			this.written = true;
		}
		catch (BufferOverflowException e)
		{
			e.printStackTrace();
			this.player.close();
		}
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
	
	public final void writeByte(final byte b) {
		try
		{
			this.buffer.put(b);
			this.written = true;
		}
		catch (BufferOverflowException e)
		{
			e.printStackTrace();
			this.player.close();
		}
	}
	
	public final byte readByte() {
		try {
			return this.buffer.get();
		}
		catch(BufferUnderflowException e) {
			e.printStackTrace();
			this.player.close();
			return 0;
		}
	}
	
	public final void writeShort(final short s) {
		try {
			this.buffer.putShort(s);
		}
		catch(BufferOverflowException e) {
			e.printStackTrace();
			this.player.close();
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
	
	public final void writeInt(final int i) {
		try
		{
			this.buffer.putInt(i);
			this.written = true;
		}
		catch (BufferOverflowException e)
		{
			e.printStackTrace();
			this.player.close();
		}
	}
	
	public final int readInt() {
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
		try
		{
			this.buffer.putLong(l);
			this.written = true;
		}
		catch (BufferOverflowException e)
		{
			e.printStackTrace();
			this.player.close();
		}
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
	
	public final void writeFloat(final float f) {
		try
		{
			this.buffer.putFloat(f);
			this.written = true;
		}
		catch (BufferOverflowException e)
		{
			e.printStackTrace();
			this.player.close();
		}
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
		try
		{
			this.buffer.putDouble(d);
			this.written = true;
		}
		catch (BufferOverflowException e)
		{
			e.printStackTrace();
			this.player.close();
		}
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
	
	public final void writeChar(final char c) {
		try {
			this.buffer.putChar((char)(Character.MAX_VALUE-c));
			this.written = true;
		}
		catch(BufferOverflowException e) {
			e.printStackTrace();
		}
	}
	
	public final char readChar() {
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

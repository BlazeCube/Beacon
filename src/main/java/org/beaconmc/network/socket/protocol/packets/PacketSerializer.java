/**	
 * Beacon - Open Source Minecraft Server
 * Copyright (C) 2014  Jan Delius
 * Copyright (C) 2014  Blazecube
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @author Jan Delius
 */
 package org.beaconmc.network.socket.protocol.packets;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import lombok.Getter;

import org.beaconmc.chat.ChatElement;
import org.beaconmc.entity.meta.EntityMetaKey;
import org.beaconmc.inventory.ItemStack;
import org.beaconmc.logging.ErrorStream;
import org.beaconmc.material.Material;
import org.beaconmc.utils.Vector3i;
import org.beaconmc.world.Location;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

import com.google.common.collect.Maps;

public class PacketSerializer {

	private static final Charset charset = Charset.forName("UTF-8");

	@Getter
	private final ByteBuf byteBuf;

	public PacketSerializer(ByteBuf byteBuf) {
		this.byteBuf = byteBuf;
	}

	public static int MojangDotA(int i) {
		return (i & -128) == 0 ? 1 : ((i & -16384) == 0 ? 2 : ((i & -2097152) == 0 ? 3
				: ((i & -268435456) == 0 ? 4 : 5)));
	}

	public void writeVarInt(int varint) {
		while ((varint & -128) != 0) {
			this.byteBuf.writeByte(varint & 127 | 128);
			varint >>>= 7;
		}
		this.byteBuf.writeByte(varint);
	}

	public int readVarInt() {
		int out = 0;
		int bytes = 0;
		byte in;
		while (true) {
			in = this.byteBuf.readByte();
			out |= (in & 0x7F) << (bytes++ * 7);
			if (bytes > 5)
				throw new RuntimeException("VarInt too big");
			if ((in & 0x80) != 0x80)
				break;
		}
		return out;
	}

	public void writeString(String string) {
		byte[] bytes = string.getBytes(charset);
		this.writeVarInt(bytes.length);
		this.byteBuf.writeBytes(bytes);
	}

	public String readString() {
		byte[] bytes = new byte[this.readVarInt()];
		this.byteBuf.readBytes(bytes);
		return new String(bytes, charset);
	}

	public void writeChat(ChatElement chatElement) {
		this.writeString(chatElement != null ? chatElement.toJSON() : null);
	}

	public ChatElement readChat() {
		String input = this.readString();
		if (input == null)
			return null;
		return ChatElement.fromJSON(input);
	}

	public void writeLong(long value) {
		this.byteBuf.writeLong(value);
	}

	public long readLong() {
		return this.byteBuf.readLong();
	}

	public void writeShort(short value) {
		this.byteBuf.writeShort(value);
	}

	public void writeShort(int value) {
		this.byteBuf.writeShort(value);
	}

	public short readShort() {
		return this.byteBuf.readShort();
	}
	
	public void writeByteArray(byte[] bytes) {
		this.writeVarInt(bytes.length);
		this.byteBuf.writeBytes(bytes);
	}

	public byte[] readByteArray() {
		byte[] bytes = new byte[this.readVarInt()];
		this.byteBuf.readBytes(bytes);
		return bytes;
	}

	public void writeInt(int value) {
		this.byteBuf.writeInt(value);
	}

	public int readInt() {
		return this.byteBuf.readInt();
	}

	public void writeUnsignedByte(short value) {
		this.byteBuf.writeByte(value);// Yep.. UnsignedByte..
	}

	public short readUnsignedByte() {
		return this.byteBuf.readUnsignedByte();
	}

	public void writeByte(byte value) {
		this.byteBuf.writeByte(value);
	}

	public byte readByte() {
		return this.byteBuf.readByte();
	}

	public void writeBoolean(boolean value) {
		this.byteBuf.writeBoolean(value);
	}

	public boolean readBoolean() {
		return this.byteBuf.readBoolean();
	}

	public void writeUnsignedShort(int value) {
		this.byteBuf.writeShort(value & 0xFFFF);
	}

	public int readUnsignedShort() {
		return this.byteBuf.readUnsignedShort();
	}

	public void writeItem(ItemStack itemStack) {
		if (itemStack == null)
			this.writeShort(-1);
		else {
			this.writeShort(itemStack.getMaterial().getLegacyId());
			this.writeByte((byte) itemStack.getAmount());
			this.writeShort(itemStack.getDamage());
			this.writeCompoundTag(itemStack.hasTag() ? new CompoundTag("tag", itemStack.getTag()) : null);
		}
	}

	public ItemStack readItem() {
		short id = this.readShort();
		if (id <= 0)
			return null;
		ItemStack itemStack = new ItemStack(Material.byID(id), this.readByte(), this.readShort());
		itemStack.setTag(this.readCompoundTag());
		return itemStack;
	}

	public void writeCompoundTag(CompoundTag compoundTag) {
		if (compoundTag == null) {
			this.writeShort(-1);
		} else {
			try {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				NBTOutputStream nbtOutputStream = new NBTOutputStream(new GZIPOutputStream(byteArrayOutputStream));
				nbtOutputStream.writeTag(compoundTag);
				byte[] bytes = byteArrayOutputStream.toByteArray();
				nbtOutputStream.close();
				this.writeShort(bytes.length);
				this.getByteBuf().writeBytes(bytes);
			} catch (IOException e) {
				ErrorStream.handle(e);
			}
		}

	}

	public CompoundTag readCompoundTag() {
		short length = this.readShort();
		if (length < 0) {
			return null;
		} else {
			try {
				byte[] bytes = new byte[length];
				this.getByteBuf().readBytes(bytes);
				CompoundTag compoundTag;
				NBTInputStream nbtInputStream = new NBTInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes)));
				compoundTag = (CompoundTag) nbtInputStream.readTag();
				nbtInputStream.close();
				return compoundTag;
			} catch (IOException e) {
				ErrorStream.handle(e);
				return null;
			}
		}
	}

	public void writePosition(int x, int y, int z) {
		this.writeLong((x & 0x3FFFFFF) << 38 | (y & 0xFFF) << 26 | z & 0x3FFFFFF);
	}

    public void writePosition(Location location){
        this.writePosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
    
    public Vector3i readPosition(){
    	long l =  this.readLong(); 
    	return new Vector3i((int)(l >> 38), (int)((l >>26) & 0xFFF),(int) (l << 38 >> 38));
    }

	public void writeDouble(double value){
		this.byteBuf.writeDouble(value);
	}

	public double readDouble(){
		return this.byteBuf.readDouble();
	}

	public void writeFloat(float value){
		this.byteBuf.writeFloat(value);
	}

	public float readFloat(){
		return this.byteBuf.readFloat();
	}

	public void writeEntityMeta(HashMap<EntityMetaKey, Object> values){
		for(Map.Entry<EntityMetaKey, Object> meta: values.entrySet())
			this.writeEntityMetaKey(meta.getKey(), meta.getValue());
		this.writeByte((byte)127);
	}

	private void writeEntityMetaKey(EntityMetaKey key, Object value){
		this.getByteBuf().writeByte((key.getTypeClassId() << 5 | key.getId() & 31) & 255);
		switch(key.getTypeClassId()) {
			case 0:
				this.writeByte((Byte) value);
				break;
			case 1:
				this.writeShort((Short) value);
			case 2:
				this.writeInt((Integer) value);
			case 3:
				this.writeFloat((Float) value);
			case 4:
				this.writeString((String) value);
			case 5:
				this.writeItem((ItemStack) value);
		}
	}

	public HashMap<EntityMetaKey, Object> readEntityMeta(){
		HashMap<EntityMetaKey, Object> metas = Maps.newHashMap();
		byte topbyte;
		while((topbyte = this.readByte()) != 127){
			int type = (topbyte & 224) >> 5;
			int id = topbyte & 31;
			EntityMetaKey key = EntityMetaKey.byId(id);
			Object value = null;
			switch(type){
				case 0:
					value = readByte();
					break;
				case 1:
					value = readShort();
					break;
				case 2:
					value = readInt();
					break;
				case 3:
					value = readFloat();
					break;
				case 4:
					value = readString();
					break;
				case 5:
					value = readItem();
					break;
				//case 6:
			}
			metas.put(key, value);
		}
		return metas;
	}

}

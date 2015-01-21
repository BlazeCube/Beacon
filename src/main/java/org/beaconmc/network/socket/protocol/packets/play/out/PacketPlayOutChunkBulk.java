/**	
 * Beacon - Open Source Minecraft Server
 * Copyright (C) 2014  Marvin Seidl
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
 * @author Marvin Seidl
 */
 package org.beaconmc.network.socket.protocol.packets.play.out;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.beaconmc.logging.DebugStream;
import org.beaconmc.network.socket.protocol.packets.PacketOut;
import org.beaconmc.network.socket.protocol.packets.PacketSerializer;
import org.beaconmc.network.socket.protocol.packets.play.PacketPlay;

@NoArgsConstructor
public class PacketPlayOutChunkBulk extends PacketPlay implements PacketOut{

	@Getter@Setter
	private boolean skyLight;
	@Getter
	private List<PacketPlayOutChunkData> chunks;
	
	public PacketPlayOutChunkBulk(boolean skyLight,List<PacketPlayOutChunkData> chunks){
		this(skyLight);
		this.chunks.addAll(chunks);
	}
	
	public PacketPlayOutChunkBulk(boolean skyLight){
		this.skyLight = skyLight;
		this.chunks = new LinkedList<>();
	}
	
	public void addChunk(PacketPlayOutChunkData chunkData){
		this.chunks.add(chunkData);
	}
	
	@Override
	public void write(PacketSerializer packetSerializer, int protocolVersion) {
		DebugStream.handle("Writing bulk: ");
		DebugStream.handle("   Skylight: " + skyLight);
		DebugStream.handle("   Chunks: " + chunks.size());	
		
		packetSerializer.writeBoolean(skyLight);
		packetSerializer.writeVarInt(chunks.size());
		for(PacketPlayOutChunkData ppocd : chunks){
			packetSerializer.writeInt(ppocd.getChunkX());
			packetSerializer.writeInt(ppocd.getChunkZ());
			packetSerializer.writeUnsignedShort(ppocd.getBitmap());
		}
		for(PacketPlayOutChunkData ppocd : chunks){
			//Write directly without varint: array size
			packetSerializer.getByteBuf().writeBytes(ppocd.getChunkData());
			//packetSerializer.writeByteArray(ppocd.getChunkData());
		}		
	}
	
	//Slightly under protocol max size of 0x200000
	//Acording to GlowStone
	public static final int MAXSIZE =0x1fff00;
	
	public static List<PacketPlayOutChunkBulk> bulkifyChunks(List<PacketPlayOutChunkData> chunks,boolean skyLight){
		//size of Bulk Header
		//Boolean 1 Byte
		//Varint 5 Bytes
		int bulkSize = 6; 
		
		List<PacketPlayOutChunkBulk> packets = new LinkedList<>();
		
		List<PacketPlayOutChunkData> bulkChunks = new LinkedList<>();
		
		for(PacketPlayOutChunkData chunk : chunks){
			//size of chunk headers inside the bulk
			//2 x Int 8 Bytes
			//1 Unsigned Short 2 Bytes
			int messageSize = 10 + chunk.getChunkData().length;
			
			//if another chunk would make the packet to big
			if(bulkSize + messageSize > MAXSIZE){
				//Flush current packets
				packets.add(new PacketPlayOutChunkBulk(skyLight,bulkChunks));
				bulkChunks.clear();
				bulkSize = 6;
			}
			
			bulkSize += messageSize;
			bulkChunks.add(chunk);
		}
		
		return packets;
		
	}
}

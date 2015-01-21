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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.beaconmc.network.socket.protocol.packets.PacketOut;
import org.beaconmc.network.socket.protocol.packets.PacketSerializer;
import org.beaconmc.network.socket.protocol.packets.play.PacketPlay;

@NoArgsConstructor
@AllArgsConstructor
public class PacketPlayOutChunkData extends PacketPlay implements PacketOut{

    @Getter@Setter
    private int chunkX;
    @Getter@Setter
    private int chunkZ;
    @Getter@Setter
    private boolean groundUpContinuous;
    @Getter@Setter
    private int bitmap;
    @Getter@Setter
    private byte[] chunkData;

    @Override
    public void write(PacketSerializer packetSerializer, int protocolVersion) {
        packetSerializer.writeInt(this.chunkX);
        packetSerializer.writeInt(this.chunkZ);
        packetSerializer.writeBoolean(this.groundUpContinuous);
        packetSerializer.writeUnsignedShort(this.bitmap);
        packetSerializer.writeByteArray(this.chunkData);
    }
    
    public static PacketPlayOutChunkData getEmpty(int x,int z){
    	return new PacketPlayOutChunkData(x, z, true, 0, new byte[0]);
    }

}

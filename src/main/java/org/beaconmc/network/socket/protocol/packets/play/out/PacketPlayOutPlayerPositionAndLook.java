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
 package org.beaconmc.network.socket.protocol.packets.play.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.beaconmc.network.socket.protocol.packets.PacketOut;
import org.beaconmc.network.socket.protocol.packets.PacketSerializer;
import org.beaconmc.network.socket.protocol.packets.play.PacketPlay;
import org.beaconmc.world.Location;

@NoArgsConstructor
@AllArgsConstructor
public class PacketPlayOutPlayerPositionAndLook extends PacketPlay implements PacketOut{

    @Getter@Setter
    private Location location;
    @Getter@Setter
    private byte flags;

    @Override
    public void write(PacketSerializer packetSerializer, int protocolVersion) {
        packetSerializer.writeDouble(this.location.getX());
        packetSerializer.writeDouble(this.location.getY());
        packetSerializer.writeDouble(this.location.getZ());
        packetSerializer.writeFloat(this.location.getYaw());
        packetSerializer.writeFloat(this.location.getPitch());
        packetSerializer.writeByte(this.flags);
    }

}

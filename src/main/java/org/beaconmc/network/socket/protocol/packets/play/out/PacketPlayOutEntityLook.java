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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.beaconmc.network.socket.protocol.packets.PacketOut;
import org.beaconmc.network.socket.protocol.packets.PacketSerializer;
import org.beaconmc.utils.MathUtils;

@NoArgsConstructor
public class PacketPlayOutEntityLook extends PacketPlayOutEntity implements PacketOut{

    @Getter@Setter
    private float yaw;
    @Getter@Setter
    private float pitch;
    @Getter@Setter
    private boolean onGround;

    public PacketPlayOutEntityLook(int entityId, float yaw, float pitch, boolean onGround){
        super(entityId);
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    @Override
    public void write(PacketSerializer packetSerializer, int protocolVersion) {
        super.write(packetSerializer, protocolVersion);
        packetSerializer.writeByte(MathUtils.toPacket360Fraction(this.yaw));
        packetSerializer.writeByte(MathUtils.toPacket360Fraction(this.pitch));
        packetSerializer.writeBoolean(this.onGround);
    }

}

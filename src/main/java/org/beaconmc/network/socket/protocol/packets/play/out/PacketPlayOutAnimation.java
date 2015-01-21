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
public class PacketPlayOutAnimation extends PacketPlay implements PacketOut{
 
        @Getter @Setter
        private int entityId;
        @Getter @Setter
        private Animation animation;
 
        @Override
        public void write(PacketSerializer packetSerializer, int protocolVersion) {
                packetSerializer.writeInt(this.entityId);
                packetSerializer.writeUnsignedByte(this.animation.getAnimationId());
        }
 
        public enum Animation {
                SWING_ARM(0),
                DAMAGE_ANIMATION(1),
                LEAVE_BED(2), EAT(3),
                CRIT_EFFECT(4),
                MAGIC_CRIT_EFFECT(5),
                UNKNOWN(102),
                CROUCH(104),
                UNCROUCH(105);
 
                @Getter
                private short animationId;
 
                Animation(int animationId) {
                        this.animationId = (short) animationId;
                }
        }
 
}

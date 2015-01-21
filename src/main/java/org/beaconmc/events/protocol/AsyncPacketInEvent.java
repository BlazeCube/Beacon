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
 package org.beaconmc.events.protocol;

import lombok.Getter;
import lombok.Setter;
import org.beaconmc.network.socket.handler.PacketHandler;
import org.beaconmc.network.socket.protocol.packets.Packet;
import org.beaconmc.network.socket.protocol.packets.PacketIn;

public class AsyncPacketInEvent extends AsyncPacketEvent {

    @Getter@Setter
    private PacketHandler packetHandler;

    @Override
    public PacketIn getPacket(){
        return (PacketIn)super.packet;
    }

    @Override
    public void setPacket(Packet packet){
        this.setPacket((PacketIn)packet);//PacketOut caused exception. perfect here
    }

    public void setPacket(PacketIn packet){
        super.packet = packet;
    }

    public AsyncPacketInEvent(PacketHandler packetHandler, PacketIn packetIn){
        super();
        this.packetHandler = packetHandler;
        super.packet = packetIn;
    }


}

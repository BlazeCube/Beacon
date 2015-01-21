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
 package org.beaconmc.network.socket.protocol;

import org.beaconmc.network.socket.protocol.packets.status.in.PacketStatusInPing;
import org.beaconmc.network.socket.protocol.packets.status.in.PacketStatusInRequest;
import org.beaconmc.network.socket.protocol.packets.status.out.PacketStatusOutPing;
import org.beaconmc.network.socket.protocol.packets.status.out.PacketStatusOutResponse;

public class StatusProtocol extends Protocol{

    public StatusProtocol(){
        inbound(0x00, PacketStatusInRequest.class);
        inbound(0x01, PacketStatusInPing.class);

        outbound(0x00, PacketStatusOutResponse.class);
        outbound(0x01, PacketStatusOutPing.class);
    }


}

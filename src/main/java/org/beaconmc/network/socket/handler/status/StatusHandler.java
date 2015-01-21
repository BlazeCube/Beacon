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
 package org.beaconmc.network.socket.handler.status;

import org.beaconmc.events.EventFactory;
import org.beaconmc.events.server.AsyncServerPingEvent;
import org.beaconmc.network.socket.ClientConnection;
import org.beaconmc.network.socket.handler.PacketHandler;
import org.beaconmc.network.socket.protocol.packets.status.in.PacketStatusInPing;
import org.beaconmc.network.socket.protocol.packets.status.in.PacketStatusInRequest;
import org.beaconmc.network.socket.protocol.packets.status.out.PacketStatusOutPing;
import org.beaconmc.network.socket.protocol.packets.status.out.PacketStatusOutResponse;

public class StatusHandler extends PacketHandler {

    public StatusHandler(ClientConnection clientConnection) {
        super(clientConnection);
    }

    public void handle(PacketStatusInRequest packetStatusInRequest){
        AsyncServerPingEvent asyncServerPingEvent = EventFactory.callAsyncServerPingEvent(this.clientConnection.getServer().createServerPing(this.clientConnection.getProtocolVersion()));
        if(asyncServerPingEvent.getPing() != null)
            this.clientConnection.sendPacket(new PacketStatusOutResponse(asyncServerPingEvent.getPing()));
    }

    public void handle(PacketStatusInPing packetStatusInPing){
        this.clientConnection.sendPacket(new PacketStatusOutPing(packetStatusInPing.getTime()));
    }

}

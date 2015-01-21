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
 package org.beaconmc.network.socket.handler.handshake;

import org.beaconmc.network.socket.ClientConnection;
import org.beaconmc.network.socket.handler.PacketHandler;
import org.beaconmc.network.socket.protocol.ProtocolState;
import org.beaconmc.network.socket.protocol.packets.handshake.in.PacketHandshakeInHandshake;

public class HandshakeHandler extends PacketHandler {

    public HandshakeHandler(ClientConnection clientConnection) {
        super(clientConnection);
    }

    public void handle(PacketHandshakeInHandshake packetHandshakeInHandshake){
        clientConnection.setHandshake(packetHandshakeInHandshake);

        if(packetHandshakeInHandshake.getNextState() == 1){
            this.clientConnection.setProtocolState(ProtocolState.STATUS);
        }else if(packetHandshakeInHandshake.getNextState() == 2){
            this.clientConnection.setProtocolState(ProtocolState.LOGIN);
        }else{
            throw new IllegalStateException("Cannot work with PacketInHandshake.nextState() = "+ packetHandshakeInHandshake.getNextState());
        }
    }
}

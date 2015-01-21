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

import org.beaconmc.network.socket.handler.PacketHandler;
import org.beaconmc.network.socket.handler.handshake.HandshakeHandler;
import org.beaconmc.network.socket.handler.login.LoginHandler;
import org.beaconmc.network.socket.handler.play.PlayHandler;
import org.beaconmc.network.socket.handler.status.StatusHandler;

public enum ProtocolState {

    HANDSHAKE(new HandshakeProtocol(), HandshakeHandler.class),
    STATUS(new StatusProtocol(), StatusHandler.class),
    LOGIN(new LoginProtocol(), LoginHandler.class),
    PLAY(new PlayProtocol(), PlayHandler.class);

    private Protocol protocol;
    private Class<? extends PacketHandler> packetHandlerClass;

    private ProtocolState(Protocol protocol, Class<? extends PacketHandler> packetHandlerClass){
        this.protocol = protocol;
        this.packetHandlerClass = packetHandlerClass;
    }

    public Protocol getProtocol(){
        return this.protocol;
    }

    public Class<? extends PacketHandler> getPacketHandlerClass(){
        return this.packetHandlerClass;
    }

}

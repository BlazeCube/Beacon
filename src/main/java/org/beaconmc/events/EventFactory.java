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
 package org.beaconmc.events;

import org.beaconmc.events.player.AsyncPlayerLoginStartEvent;
import org.beaconmc.events.player.PlayerLoginEvent;
import org.beaconmc.events.protocol.AsyncPacketInEvent;
import org.beaconmc.events.protocol.AsyncPacketOutEvent;
import org.beaconmc.events.server.AsyncServerPingEvent;
import org.beaconmc.network.socket.ClientConnection;
import org.beaconmc.network.socket.handler.PacketHandler;
import org.beaconmc.network.socket.handler.login.LoginHandler;
import org.beaconmc.network.socket.protocol.ServerPing;
import org.beaconmc.network.socket.protocol.packets.PacketIn;
import org.beaconmc.network.socket.protocol.packets.PacketOut;

public class EventFactory {

    public static AsyncServerPingEvent callAsyncServerPingEvent(ServerPing serverPing){
        AsyncServerPingEvent asyncServerPingEvent = new AsyncServerPingEvent(serverPing);
        callEvent(asyncServerPingEvent);
        return asyncServerPingEvent;
    }

    public static AsyncPlayerLoginStartEvent callAsyncPlayerLoginStartEvent(ClientConnection clientConnection, LoginHandler loginHandler){
        AsyncPlayerLoginStartEvent asyncPlayerLoginStartEvent = new AsyncPlayerLoginStartEvent(clientConnection, loginHandler);
        callEvent(asyncPlayerLoginStartEvent);
        return asyncPlayerLoginStartEvent;
    }

    public static PlayerLoginEvent callPlayerLoginEvent(ClientConnection clientConnection){
        PlayerLoginEvent playerLoginEvent = new PlayerLoginEvent(clientConnection);
        callEvent(playerLoginEvent);
        return playerLoginEvent;
    }

    public static AsyncPacketInEvent callAsyncPacketInEvent(PacketHandler packetHandler, PacketIn packetIn){
        AsyncPacketInEvent asyncPacketInEvent = new AsyncPacketInEvent(packetHandler, packetIn);
        callEvent(asyncPacketInEvent);
        return asyncPacketInEvent;
    }

    public static AsyncPacketOutEvent callAsyncPacketOutEvent(PacketOut packetOut){
        AsyncPacketOutEvent asyncPacketOutEvent = new AsyncPacketOutEvent(packetOut);
        callEvent(asyncPacketOutEvent);
        return asyncPacketOutEvent;
    }

    private static void callEvent(Event event){
        //TODO -> call event
    }

}

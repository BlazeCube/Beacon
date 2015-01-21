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
 package org.beaconmc.network.socket.pipeline;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.beaconmc.events.EventFactory;
import org.beaconmc.events.protocol.AsyncPacketInEvent;
import org.beaconmc.network.socket.ClientConnection;
import org.beaconmc.network.socket.NetworkHandler;
import org.beaconmc.network.socket.protocol.packets.AsyncPacket;
import org.beaconmc.network.socket.protocol.packets.PacketIn;

import java.util.concurrent.atomic.AtomicReference;

public class ConnectionHandler extends SimpleChannelInboundHandler<PacketIn> {

    private final AtomicReference<ClientConnection> connection = new AtomicReference<ClientConnection>(null);
    private NetworkHandler networkHandler;

    public ConnectionHandler(NetworkHandler networkHandler){
        this.networkHandler = networkHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        ClientConnection clientConnection = this.networkHandler.newConnection(ctx);
        if(!connection.compareAndSet(null, clientConnection)){
            throw new IllegalStateException("Tried to initialize session more than once");
        }
        clientConnection.setup();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, PacketIn packetIn) throws Exception {

        AsyncPacketInEvent asyncPacketInEvent = EventFactory.callAsyncPacketInEvent(this.connection.get().getPacketHandler(), packetIn);

        if(asyncPacketInEvent.getPacket() instanceof AsyncPacket)
            this.connection.get().handle(asyncPacketInEvent);
        else
            this.connection.get().handleOnQueue(asyncPacketInEvent);
    }
}

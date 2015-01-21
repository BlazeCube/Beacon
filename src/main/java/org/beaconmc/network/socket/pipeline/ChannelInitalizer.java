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

import io.netty.channel.socket.SocketChannel;
import org.beaconmc.network.socket.NetworkHandler;
import org.beaconmc.network.socket.protocol.ProtocolState;

public class ChannelInitalizer extends io.netty.channel.ChannelInitializer<SocketChannel>{

    private final NetworkHandler networkHandler;

    public ChannelInitalizer(NetworkHandler networkHandler){
        this.networkHandler = networkHandler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                .addLast("encryption", PacketNone.HANDLER)
                .addLast("legacy_ping", new PacketLegacy(this.networkHandler.getServer()))
                .addLast("framing", new PacketFraming())
                .addLast("compression", PacketNone.HANDLER)
                .addLast("encoder", new PacketEncoder(ProtocolState.HANDSHAKE.getProtocol(), 0))
                .addLast("decoder", new PacketDecoder(ProtocolState.HANDSHAKE.getProtocol(), 0))
                .addLast("handler", new ConnectionHandler(this.networkHandler));
    }
}

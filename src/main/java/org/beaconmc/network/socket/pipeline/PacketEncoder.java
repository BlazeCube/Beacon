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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.beaconmc.logging.ErrorStream;
import org.beaconmc.network.socket.protocol.Protocol;
import org.beaconmc.network.socket.protocol.ProtocolDirection;
import org.beaconmc.network.socket.protocol.packets.PacketOut;
import org.beaconmc.network.socket.protocol.packets.PacketSerializer;

import java.io.IOException;
import java.util.List;

public class PacketEncoder extends MessageToMessageEncoder<PacketOut> {

    private Protocol protocol;
    private int protocolVersion;

    public PacketEncoder(Protocol protocol, int protocolVersion){
        this.protocol = protocol;
        this.protocolVersion = protocolVersion;
    }
    @Override
    protected void encode(ChannelHandlerContext ctx, PacketOut in, List<Object> out) throws Exception {
        Integer id = protocol.getId(in.getClass(), ProtocolDirection.OUT);

        if(id == null) {
            ErrorStream.handle(new IOException("Tried to serialize unregistered packet "+in.getClass().getSimpleName()));
        }else{
            //DebugStream.handle("Packet id "+id);
            ByteBuf buf = ctx.alloc().buffer();
            PacketSerializer packetSerializer = new PacketSerializer(buf);
            packetSerializer.writeVarInt(id);
            in.write(packetSerializer, this.protocolVersion);
            out.add(packetSerializer.getByteBuf());
        }
    }

    public void writeVarInt(int varint, ByteBuf byteBuf){
        while((varint & -128) != 0){
            byteBuf.writeByte(varint & 127 | 128);
            varint >>>= 7;
        }
        byteBuf.writeByte(varint);
    }
}

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
import io.netty.handler.codec.MessageToMessageDecoder;
import org.beaconmc.logging.ErrorStream;
import org.beaconmc.network.socket.protocol.Protocol;
import org.beaconmc.network.socket.protocol.ProtocolDirection;
import org.beaconmc.network.socket.protocol.packets.PacketIn;
import org.beaconmc.network.socket.protocol.packets.PacketSerializer;

import java.util.List;

public class PacketDecoder extends MessageToMessageDecoder<ByteBuf> {

    private Protocol protocol;
    private int protocolVersion;

    public PacketDecoder(Protocol protocol, int protocolVersion){
        this.protocol = protocol;
        this.protocolVersion = protocolVersion;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        int len = in.readableBytes();
        if (len != 0) {
            PacketSerializer packetserializer = new PacketSerializer(in);
            int id = packetserializer.readVarInt();
            PacketIn packet = (PacketIn)protocol.newPacket(id, ProtocolDirection.IN);
            
            if (packet == null) {
                /*if(this.protocol instanceof PlayProtocol) {
                    ctx.writeAndFlush(new PacketPlayOutDisconnect(new ChatElement(ChatColor.RED+"Failed to handle packet "+id)));
                    ctx.disconnect();
                }*/
               // ErrorStream.handle(new IOException("Bad packet id " + id));
            	ErrorStream.handle("Incoming Packet", "Bad packet id " + id);
            } else {
            	//DebugStream.handle("Implemented Packet with id " + id + " Name: " + packet.getClass().getSimpleName());
            	
                packet.read(packetserializer, this.protocolVersion);
                if (packetserializer.getByteBuf().readableBytes() > 0) {
                    //ErrorStream.handle(new IOException("Packet was larger than expected, found " + packetserializer.getByteBuf().readableBytes() + " bytes extra whilst reading packet " + id));
                	ErrorStream.handle("Incoming Packet", "Packet was larger than expected, found " + packetserializer.getByteBuf().readableBytes() + " bytes extra whilst reading packet " + id);
                } else {
                    out.add(packet);
                }
            }
        }
    }
}

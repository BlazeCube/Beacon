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
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

/*
    This code is from Glowstone
    Original File can be found at https://github.com/GlowstoneMC/Glowstone/blob/master/src/main/java/net/glowstone/net/pipeline/FramingHandler.java
 */

public final class PacketFraming extends ByteToMessageCodec<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        writeVarInt(msg.readableBytes(), out);
        out.writeBytes(msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        if (!readableVarInt(in)) {
            return;
        }
        int length = readVarInt(in);
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        ByteBuf buf = ctx.alloc().buffer(length);
        in.readBytes(buf, length);
        out.add(buf);
    }

    public void writeVarInt(int varint, ByteBuf byteBuf){
        while((varint & -128) != 0){
            byteBuf.writeByte(varint & 127 | 128);
            varint >>>= 7;
        }
        byteBuf.writeByte(varint);
    }

    public int readVarInt(ByteBuf byteBuf){
        int out = 0;
        int bytes = 0;
        byte in;
        while (true) {
            in = byteBuf.readByte();
            out |= (in & 0x7F) << (bytes++ * 7);
            if (bytes > 5) throw new RuntimeException("VarInt too big");
            if ((in & 0x80) != 0x80) break;
        }
        return out;
    }

    private static boolean readableVarInt(ByteBuf buf) {
        if (buf.readableBytes() > 5) {
            return true;
        }
        int idx = buf.readerIndex();
        byte in;
        do {
            if (buf.readableBytes() < 1) {
                buf.readerIndex(idx);
                return false;
            }
            in = buf.readByte();
        } while ((in & 0x80) != 0);
        buf.readerIndex(idx);
        return true;
    }
}

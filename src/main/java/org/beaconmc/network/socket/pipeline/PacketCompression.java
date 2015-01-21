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
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.beaconmc.network.socket.protocol.packets.PacketSerializer;

import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class PacketCompression extends MessageToMessageCodec<ByteBuf, ByteBuf> {

    private static final int COMPRESSION_LEVEL = Deflater.DEFAULT_COMPRESSION;
    private final int threshold;
    private final Inflater inflater;
    private final Deflater deflater;

    public PacketCompression(int threshold){
        this.threshold = threshold;
        this.inflater = new Inflater();
        this.deflater = new Deflater(COMPRESSION_LEVEL);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> objects) throws Exception {

        ByteBuf prefixByteBuf = channelHandlerContext.alloc().buffer(5);
        PacketSerializer prefixPacketSerializer = new PacketSerializer(prefixByteBuf);
        ByteBuf contentByteBuf;

        if(byteBuf.readableBytes() >= threshold) {

            int index = byteBuf.readerIndex();
            int length = byteBuf.readableBytes();

            byte[] source = new byte[length];
            byteBuf.readBytes(source);
            this.deflater.setInput(source);
            this.deflater.finish();

            byte[] compressed = new byte[length];
            int compressedLength = this.deflater.deflate(compressed);
            this.deflater.reset();

            if (compressedLength == 0) {
                throw new IllegalStateException("Failed to compress packet");
            } else if (compressedLength >= length) {
                prefixPacketSerializer.writeVarInt(0);
                byteBuf.readerIndex(index);
                byteBuf.retain();
                contentByteBuf = byteBuf;
            } else {
                prefixPacketSerializer.writeVarInt(length);
                contentByteBuf = Unpooled.wrappedBuffer(compressed, 0, compressedLength);
            }
        } else {
            prefixPacketSerializer.writeVarInt(0);
            byteBuf.retain();
            contentByteBuf = byteBuf;
        }
        objects.add(Unpooled.wrappedBuffer(prefixPacketSerializer.getByteBuf(), contentByteBuf));
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> objects) throws Exception {

        int index = byteBuf.readerIndex();

        PacketSerializer inPacketSerializer = new PacketSerializer(byteBuf);

        int uncompressedSize = inPacketSerializer.readVarInt();

        if(uncompressedSize == 0){
            int length = byteBuf.readableBytes();
            if(length >= threshold){
                throw new IllegalArgumentException("Received uncompressed packet");
            }

            ByteBuf outByteBuf = channelHandlerContext.alloc().buffer(length);
            byteBuf.readBytes(outByteBuf, length);
            objects.add(outByteBuf);
        }else{
            byte[] source = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(source);
            this.inflater.setInput(source);

            byte[] output = new byte[uncompressedSize];
            int resultLength = this.inflater.inflate(output);
            this.inflater.reset();

            if(resultLength == 0){
                byteBuf.readerIndex(index);
                byteBuf.retain();
                objects.add(byteBuf);
            }else if(resultLength != uncompressedSize){
                throw new IllegalStateException("Received compressed packet with invalid length");
            } else {
                objects.add(Unpooled.wrappedBuffer(output));
            }

        }

    }
}
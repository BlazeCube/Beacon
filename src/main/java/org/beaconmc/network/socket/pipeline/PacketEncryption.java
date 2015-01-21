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

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.List;

public class PacketEncryption extends MessageToMessageCodec<ByteBuf, ByteBuf> {

    private final CryptBuf encodeBuf;
    private final CryptBuf decodeBuf;

    public PacketEncryption(SecretKey sharedSecret) {
        try {
            encodeBuf = new CryptBuf(Cipher.ENCRYPT_MODE, sharedSecret);
            decodeBuf = new CryptBuf(Cipher.DECRYPT_MODE, sharedSecret);
        } catch (GeneralSecurityException e) {
            throw new AssertionError("Failed to initialize encrypted channel");
        }
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        encodeBuf.crypt(msg, out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        decodeBuf.crypt(msg, out);
    }

    private static class CryptBuf {
        private final Cipher cipher;

        private CryptBuf(int mode, SecretKey sharedSecret) throws GeneralSecurityException {
            cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(mode, sharedSecret, new IvParameterSpec(sharedSecret.getEncoded()));
        }

        public void crypt(ByteBuf msg, List<Object> out) {
            ByteBuffer outBuffer = ByteBuffer.allocate(msg.readableBytes());

            try {
                cipher.update(msg.nioBuffer(), outBuffer);
            } catch (ShortBufferException e) {
                throw new AssertionError("Encryption buffer was too short");
            }

            outBuffer.flip();
            out.add(Unpooled.wrappedBuffer(outBuffer));
        }
    }

}
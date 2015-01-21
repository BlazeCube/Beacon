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
 package org.beaconmc.network.socket.protocol.packets.login.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.beaconmc.network.socket.protocol.packets.AsyncPacket;
import org.beaconmc.network.socket.protocol.packets.PacketIn;
import org.beaconmc.network.socket.protocol.packets.PacketSerializer;
import org.beaconmc.network.socket.protocol.packets.login.PacketLogin;
import org.beaconmc.utils.EncryptionUtils;

import javax.crypto.SecretKey;
import java.security.PrivateKey;

@NoArgsConstructor
@AllArgsConstructor
public class PacketLoginInEncryptionResponse extends PacketLogin implements PacketIn, AsyncPacket {

    @Getter@Setter
    private byte[] sharedSecret;
    @Getter@Setter
    private byte[] verifyToken;

    @Override
    public void read(PacketSerializer packetSerializer, int protocolVersion) {
        this.sharedSecret = packetSerializer.readByteArray();
        this.verifyToken = packetSerializer.readByteArray();
    }

    public byte[] getVerifyToken(PrivateKey privateKey){
        return (privateKey == null)? this.getVerifyToken() : EncryptionUtils.b(privateKey, this.verifyToken);
    }

    public SecretKey getSecretKey(PrivateKey privateKey){
        return EncryptionUtils.a(privateKey, this.sharedSecret);
    }

}

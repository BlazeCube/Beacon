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

import org.beaconmc.network.socket.protocol.packets.login.in.PacketLoginInEncryptionResponse;
import org.beaconmc.network.socket.protocol.packets.login.in.PacketLoginInStart;
import org.beaconmc.network.socket.protocol.packets.login.out.PacketLoginOutDisconnect;
import org.beaconmc.network.socket.protocol.packets.login.out.PacketLoginOutEncryptionRequest;
import org.beaconmc.network.socket.protocol.packets.login.out.PacketLoginOutSetCompression;
import org.beaconmc.network.socket.protocol.packets.login.out.PacketLoginOutSuccess;

public class LoginProtocol extends Protocol{

    public LoginProtocol(){
        inbound(0x00, PacketLoginInStart.class);
        inbound(0x01, PacketLoginInEncryptionResponse.class);

        outbound(0x00, PacketLoginOutDisconnect.class);
        outbound(0x01, PacketLoginOutEncryptionRequest.class);
        outbound(0x02, PacketLoginOutSuccess.class);
        outbound(0x03, PacketLoginOutSetCompression.class);
    }


}

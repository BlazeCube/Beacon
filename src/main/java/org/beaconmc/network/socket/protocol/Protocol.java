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

import com.google.common.collect.HashBiMap;
import org.beaconmc.logging.ErrorStream;
import org.beaconmc.network.socket.protocol.packets.Packet;
import org.beaconmc.network.socket.protocol.packets.PacketIn;
import org.beaconmc.network.socket.protocol.packets.PacketOut;

public class Protocol {

    protected HashBiMap<Integer, Class<? extends PacketIn>> inbound = HashBiMap.create();
    protected HashBiMap<Integer, Class<? extends PacketOut>> outbound = HashBiMap.create();

    protected void inbound(int id, Class<? extends PacketIn> packetclass){
        this.inbound.put(id, packetclass);
    }

    protected void outbound(int id, Class<? extends PacketOut> packetclass){
        this.outbound.put(id, packetclass);
    }

    public Packet newPacket(int id, ProtocolDirection protocolDirection){
        try {
            switch (protocolDirection) {
                case IN:
                    if (this.inbound.containsKey(id)) return this.inbound.get(id).newInstance();
                    break;
                case OUT:
                    if(this.outbound.containsKey(id)) return this.outbound.get(id).newInstance();
                    break;
            }
        } catch (InstantiationException e) {
            ErrorStream.handle(e);
        } catch (IllegalAccessException e) {
            ErrorStream.handle(e);
        }
        return null;
    }

    public Integer getId(Class<? extends Packet> packetclass, ProtocolDirection protocolDirection){
        switch(protocolDirection){
            case IN:
                return this.inbound.inverse().get(packetclass);
            case OUT:
                return this.outbound.inverse().get(packetclass);
        }
        return null;
    }



}

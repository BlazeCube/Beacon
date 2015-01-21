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
 package org.beaconmc.network.socket.handler;

import org.beaconmc.chat.ChatColor;
import org.beaconmc.chat.ChatElement;
import org.beaconmc.logging.ErrorStream;
import org.beaconmc.network.socket.ClientConnection;
import org.beaconmc.network.socket.protocol.packets.Packet;

import java.lang.reflect.Method;

public class PacketHandler {

    protected final ClientConnection clientConnection;

    public PacketHandler(ClientConnection clientConnection){
        this.clientConnection = clientConnection;
    }

    public void handle(Packet packet){
        try{
            Method method = this.getClass().getMethod("handle", packet.getClass());
            method.invoke(this, packet);
        } catch (NoSuchMethodException e) {
            this.disconnect(new ChatElement(ChatColor.RED + "Failed to handle packet"));
            throw new IllegalStateException("Packet "+packet.getClass().getSimpleName()+" cannot be handled in "+this.getClass().getName());
        } catch (Exception e) {
            ErrorStream.handle(e);
        }
    }

    public void disconnect(ChatElement reason){
        throw new IllegalStateException("Disconnect is not supportet in "+this.getClass().getSimpleName());
    }

}

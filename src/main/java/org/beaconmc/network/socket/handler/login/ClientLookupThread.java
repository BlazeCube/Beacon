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
 package org.beaconmc.network.socket.handler.login;

import com.google.gson.JsonParseException;
import org.beaconmc.BeaconServer;
import org.beaconmc.chat.ChatColor;
import org.beaconmc.logging.ErrorStream;
import org.beaconmc.network.socket.ClientConnection;
import org.beaconmc.network.socket.protocol.PlayerProfile;
import org.beaconmc.utils.UUIDUtils;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public class ClientLookupThread extends Thread{

    private ClientConnection clientConnection;
    private String url;

    public ClientLookupThread(ClientConnection connection, String hash){
        super("Client Lookup Thread #"+connection.getAuthenticationName());
        this.clientConnection = connection;
        this.url = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username="+this.clientConnection.getAuthenticationName()+"&serverId="+hash;
    }

    @Override
    public void run(){
        try{
            URLConnection connection = new URL(url).openConnection();
            LookupProfile lookupProfile;
            try{
                lookupProfile = BeaconServer.gson.fromJson(new InputStreamReader(connection.getInputStream()), LookupProfile.class);
            }catch(JsonParseException ex){
                clientConnection.disconnect(ChatColor.RED+"Failed to verify username");
                return;
            }
            if(lookupProfile == null){
                clientConnection.disconnect(ChatColor.RED+"Failed to verify username");
                return;
            }

            UUID uuid = UUIDUtils.fromMojangString(lookupProfile.id);

            PlayerProfile playerProfile = new PlayerProfile(uuid, lookupProfile.name).setProperties(lookupProfile.properties);
            clientConnection.createPlayer(playerProfile);
        }catch(Exception e){
            ErrorStream.handle(e);
            clientConnection.disconnect(ChatColor.RED + "Internal error during authentication");
        }
    }

    private class LookupProfile{
        private String id;
        private String name;
        private PlayerProfile.Property[] properties;
    }

}
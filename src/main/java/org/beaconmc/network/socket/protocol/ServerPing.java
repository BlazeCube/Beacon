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

import lombok.Getter;
import lombok.Setter;
import org.beaconmc.BeaconServer;

public class ServerPing{

    @Getter@Setter
    private Protocol version;
    @Getter@Setter
    private Players players;
    @Getter@Setter
    private Description description;
    @Getter@Setter
    private String favicon;

    public ServerPing(Protocol version, Players players, Description description, String favicon){
        this.version = version;
        this.players = players;
        this.description = description;
        this.favicon = favicon;
    }

    public ServerPing(Protocol version, Players players, String description, String favicon){
        this(version, players, new Description(description), favicon);
    }

    public String toJSON(){
        return BeaconServer.gson.toJson(this);
    }

    public static ServerPing fromJSON(String json){
        return BeaconServer.gson.fromJson(json, ServerPing.class);
    }

    public static class Protocol{

        @Getter@Setter
        private String name;
        @Getter@Setter
        private int protocol;

        public Protocol(String protocolName, int protocolVersion){
            this.name = protocolName;
            this.protocol = protocolVersion;
        }
    }

    public static class Players{

        @Getter@Setter
        private int max;
        @Getter@Setter
        private int online;
        @Getter@Setter
        private PlayerSample[] sample;

        public Players(int maxPlayers, int onlinePlayers, PlayerSample...entries){
            this.max = maxPlayers;
            this.online = onlinePlayers;
            this.sample = entries;
        }

        public static class PlayerSample{

            @Getter@Setter
            private String name;
            @Getter@Setter
            private String id;

        }
    }

    public static class Description{

        @Getter@Setter
        private String text;

        public Description(String text){
            this.text = text;
        }

    }

}

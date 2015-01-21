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
 package org.beaconmc.network.socket.protocol.packets.play.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.beaconmc.GameMode;
import org.beaconmc.entity.living.Player;
import org.beaconmc.network.socket.protocol.packets.PacketOut;
import org.beaconmc.network.socket.protocol.packets.PacketSerializer;
import org.beaconmc.network.socket.protocol.packets.play.PacketPlay;

@NoArgsConstructor
@AllArgsConstructor
public class PacketPlayOutJoinGame extends PacketPlay implements PacketOut{

    @Getter@Setter
    private int entityId;
    @Getter@Setter
    private GameMode gamemode;
    @Getter@Setter
    private byte dimension;
    @Getter@Setter
    private short difficulty;
    @Getter@Setter
    private short maxPlayers;
    @Getter@Setter
    private String levelType;
    @Getter@Setter
    private boolean reducedDebugInfo;

    public PacketPlayOutJoinGame(Player player){
        this.entityId = player.getEntityId();
        this.gamemode = player.getGameMode();
        this.dimension = 0;//TODO -> world dimension
        this.difficulty = (short)player.getServer().getDifficulty();
        this.maxPlayers = player.getServer().getMaxPlayers() > 127 ? (short)127 : (short)player.getServer().getMaxPlayers();
        this.levelType = "default";//TODO -> world type
        this.reducedDebugInfo = false;//TODO -> server reduced Debug info
    }

    @Override
    public void write(PacketSerializer packetSerializer, int protocolVersion) {
        packetSerializer.writeInt(this.entityId);
        packetSerializer.writeUnsignedByte((short)this.gamemode.getId());
        packetSerializer.writeByte(this.dimension);
        packetSerializer.writeUnsignedByte(this.difficulty);
        packetSerializer.writeUnsignedByte(this.maxPlayers);
        packetSerializer.writeString(this.levelType);
        packetSerializer.writeBoolean(this.reducedDebugInfo);
    }

}

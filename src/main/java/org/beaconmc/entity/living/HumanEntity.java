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
 package org.beaconmc.entity.living;

import lombok.Getter;
import org.beaconmc.BeaconServer;
import org.beaconmc.GameMode;
import org.beaconmc.entity.LivingEntity;
import org.beaconmc.network.socket.protocol.PlayerProfile;
import org.beaconmc.world.Location;

import java.util.UUID;

public class HumanEntity extends LivingEntity {

    @Getter
    protected final PlayerProfile playerProfile;
    @Getter
    protected GameMode gameMode = server.getDefaultGamemode();

    protected HumanEntity(BeaconServer server, Location location, PlayerProfile playerProfile) {
        super(server, location);
        this.playerProfile = playerProfile;
    }

    @Override
    public void heartBeat(){
        super.heartBeat();
    }

    @Override
    public void spawn() {

    }

    public String getName(){
        return this.playerProfile.getUsername();
    }

    @Override
    public UUID getUuid(){
        return this.playerProfile.getUuid();
    }

}

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
 package org.beaconmc.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.beaconmc.entity.living.Player;
import org.beaconmc.network.socket.protocol.packets.PacketOut;
import org.beaconmc.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class EntityManager implements Iterable<Entity>{

    private static int CURRENT_ENTITY_ID = 1;
    private static final int MAX_ENTITY_ID = Integer.MAX_VALUE-10000;//10000 for entity packets by plugin

    @Getter
    private final World world;
    @Getter
    private final HashMap<Integer, Entity> entities = Maps.newHashMap();
    @Getter
    private final ArrayList<Player> players = Lists.newArrayList();

    public EntityManager(World world){
        this.world = world;
    }
    
	public void heartBeat(){
        for(Entity entity: Lists.newArrayList(this.entities.values())){
            entity.heartBeat();

            if(entity.hasFuckingUpdatesForClientsToBroadcastOnTheWorldWithinBeacon())
                for(PacketOut packetOut: entity.getUpdatePackets())
                    this.world.sendPacket(packetOut);

        }
    }

    public int register(Entity entity){
        if(entity == null || entity.getLocation().getWorld().equals(this.getWorld()) && entity.getEntityId() != -1 && this.entities.containsKey(entity.getEntityId()) && this.entities.get(entity.getEntityId()).equals(entity)) return entity != null?entity.getEntityId() : -1;
        int entityId = entity.getEntityId() != -1 ? entity.getEntityId(): (CURRENT_ENTITY_ID++ % MAX_ENTITY_ID);
        this.entities.put(entityId, entity);
        if(entity instanceof Player)
            this.players.add((Player)entity);
        return entityId;
    }

    public void unregister(Entity entity) {
        if (entity == null || entity.getEntityId() == -1 || !this.entities.get(entity.getEntityId()).equals(entity))
            return;
        this.entities.remove(entity.getEntityId());
        if(entity instanceof Player)
            this.players.remove(entity);
    }

    public Entity getEntity(int id){
        return this.entities.get(id);
    }

    @Override
    public Iterator<Entity> iterator() {
        return this.entities.values().iterator();
    }

    public void sendPacket(PacketOut packetOut) {
        for(Player player: Lists.newArrayList(this.players)) {
            player.getClientConnection().sendPacket(packetOut);
        }
    }
}

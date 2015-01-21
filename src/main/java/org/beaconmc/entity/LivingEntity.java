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

import lombok.Getter;
import lombok.Setter;
import org.beaconmc.BeaconServer;
import org.beaconmc.entity.meta.EntityMetaKey;
import org.beaconmc.world.Location;

public abstract class LivingEntity extends Entity{

    @Getter@Setter
    private double maxHealth = 20;
    @Getter
    private double health = maxHealth;
    @Getter
    private double lastDamage;
    @Getter
    private int noDamageTicks = 0;
    private int startNoDamageTicks = 20;
    @Getter
    private String customName;
    @Getter
    private boolean customNameVisible;
    private boolean ai;

    protected LivingEntity(BeaconServer server, Location location) {
        super(server, location);
    }

    public boolean hasAi(){
        return this.ai;
    }

    @Override
    public void heartBeat(){
        super.heartBeat();
        if(this.noDamageTicks > 0)
            this.noDamageTicks--;
    }

    public void setHealth(double health){
        this.health = health;
        this.getMetadata().set(EntityMetaKey.HEALTH, (float)health);
    }

    public void setCustomName(String customName){
        this.customName = customName;
        this.getMetadata().set(EntityMetaKey.DISPLAY_NAME, customName);
    }

    public void setCustomNameVisible(boolean customNameVisible){
        this.customNameVisible = customNameVisible;
        this.getMetadata().set(EntityMetaKey.ALWAYS_SHOW_DISPLAY_NAME, (byte)(customNameVisible ? 1 : 0));
    }

    public void setHasAI(boolean ai){
        this.ai = ai;
        this.getMetadata().set(EntityMetaKey.HAS_AI, (byte)(ai ? 1 : 0));
    }


}

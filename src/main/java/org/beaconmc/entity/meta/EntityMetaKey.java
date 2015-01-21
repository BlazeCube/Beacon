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
 package org.beaconmc.entity.meta;

import com.google.common.collect.Maps;
import org.beaconmc.entity.Entity;
import org.beaconmc.entity.LivingEntity;
import org.beaconmc.entity.living.HumanEntity;
import org.beaconmc.inventory.ItemStack;

import java.util.HashMap;

public enum EntityMetaKey{

    STATUS(0, Byte.class, Entity.class),
    AIR(1, Short.class, Entity.class),

    DISPLAY_NAME(2, String.class, LivingEntity.class),
    ALWAYS_SHOW_DISPLAY_NAME(3, Byte.class, LivingEntity.class),
    HEALTH(6, Float.class, LivingEntity.class),
    POTION_COLOR(7, Integer.class, LivingEntity.class),
    POTION_AMBIENT(8, Byte.class, LivingEntity.class),
    ARROWS(9, Byte.class, LivingEntity.class),
    HAS_AI(15, Byte.class, LivingEntity.class),

    //Ageable

    //ArmorStand

    HUMAN_SKIN_FLAG(10, Byte.class, HumanEntity.class),
    HUMAN_HIDE_CAPE(16, Byte.class, HumanEntity.class),
    HUMAN_ABSORPTION_HEARTS(17, Float.class, HumanEntity.class),
    HUMAN_SCORE(17, Integer.class, HumanEntity.class),


    //Horse

    //Bat

    //Tameable

    //Ocelot

    //Wolf

    //Pig

    //Rabbit

    //Sheep

    //Villager

    //Enderman

    //Zombie

    //Zombie Pigman

    //Blaze

    //Cave Spider -> Spider

    //Creeper

    //Ghast

    //Slime

    //Magma Cube -> Slime

    //Skeleton

    //Witch

    //Iron Golem

    //Wither

    //Boat

    //Minecart

    //Furnace Minecart

    //Item

    //Arrow

    //Firework

    //Item Frame

    //Ender Crystal

    ;

    private static final HashMap<Class, Integer> TYPE_IDS = Maps.newHashMap();
    private static final HashMap<Integer, EntityMetaKey> BY_ID = Maps.newHashMap();

    private int id;
    private Class<?> type;
    private Class<? extends Entity> entityclass;

    private EntityMetaKey(int id, Class<?> type, Class<? extends Entity> entityclass){
        this.id = id;
        this.type = type;
        this.entityclass = entityclass;
    }

    public int getId(){
        return this.id;
    }

    public Class<?> getTypeClass(){
        return this.type;
    }

    public int getTypeClassId(){
        return TYPE_IDS.get(this.type);
    }

    public Class<? extends Entity> getEntityClass(){
        return this.entityclass;
    }

    static{
        TYPE_IDS.put(Byte.class, 0);
        TYPE_IDS.put(Short.class, 1);
        TYPE_IDS.put(Integer.class, 2);
        TYPE_IDS.put(float.class, 3);
        TYPE_IDS.put(String.class, 4);
        TYPE_IDS.put(ItemStack.class, 5);
        //TYPE_IDS.put(Chunk.class, Integer.valueOf(6));


        for(EntityMetaKey key: values()){
            BY_ID.put(key.getId(), key);
        }
    }

    public static EntityMetaKey byId(int id){
        return BY_ID.get(id);
    }

}

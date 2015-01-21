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

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.Validate;
import org.beaconmc.entity.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityMeta{

    private final Entity entity;
    private HashMap<EntityMetaKey, Object> values = Maps.newHashMap();
    private HashMap<EntityMetaKey, Object> changes = Maps.newHashMap();

    public EntityMeta(Entity entity){
        this.entity = entity;
    }

    public void set(EntityMetaKey key, Object value){
        Validate.isTrue(this.entity == null || key.getEntityClass().isAssignableFrom(entity.getClass()), "Cannot apply "+key.toString()+" to "+(this.entity == null ? "null" : this.entity.getClass().getSimpleName()));
        Validate.isTrue(key.getTypeClass().isAssignableFrom(value.getClass()), "Cannot apply value of "+value.getClass().getSimpleName()+" to "+key.toString());
        if(!Objects.equal(values.put(key, key.getTypeClass().cast(value)), value))
            changes.put(key, value);
        values.put(key, value);
    }

    public boolean has(EntityMetaKey key){
        return this.values.containsKey(key);
    }

    public void apply(List<Map.Entry<EntityMetaKey, Object>> metas){
        for(Map.Entry<EntityMetaKey, Object> entry: metas)
            this.values.put(entry.getKey(), entry.getValue());
    }

    public void setValues(List<Map.Entry<EntityMetaKey, Object>> metas){
        this.values.clear();
        this.apply(metas);
    }

    public HashMap<EntityMetaKey, Object> values(){
        return this.values;
    }

    public HashMap<EntityMetaKey, Object> getChanges(){
        return this.changes;
    }

    public void resetChanges(){
        this.changes.clear();
    }

}

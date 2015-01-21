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
 package org.beaconmc;

import com.google.common.collect.Maps;

import java.util.Map;

public enum GameMode {

    SURVIVAL("survival", 0),
    CREATIVE("creative", 1),
    ADVENTURE("adventure", 2),
    SPECTATOR("spectator", 3);

    private final static Map<Integer, GameMode> BY_ID = Maps.newHashMap();
    private final static Map<String, GameMode> BY_NAME = Maps.newHashMap();

    private int id;
    private String name;

    private GameMode(String name, int id){
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString(){
        return this.name;
    }

    public int getId(){
        return this.id;
    }

    static{
        for(GameMode gm: values()){
            BY_ID.put(gm.getId(), gm);
            BY_NAME.put(gm.toString().toLowerCase(), gm);
        }
    }

    public static GameMode getByName(String name){
        return BY_NAME.get(name.toLowerCase());
    }

    public static GameMode getById(int id){
        return BY_ID.get(id);
    }
}

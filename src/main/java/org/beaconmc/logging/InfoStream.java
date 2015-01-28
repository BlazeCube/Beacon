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
 package org.beaconmc.logging;

import com.google.common.collect.Lists;

import java.util.ArrayList;

public abstract class InfoStream {

    private static ArrayList<InfoStream> streams = Lists.newArrayList();

    public static void register(InfoStream infoStream){
        if(!streams.contains(infoStream))
            streams.add(infoStream);
    }

    public static void unregister(InfoStream infoStream){
        streams.remove(infoStream);
    }

    public static void handle(String message){
        for(InfoStream infoStream: Lists.newArrayList(streams))
        infoStream.abstractHandle(message);
    }

    protected abstract void abstractHandle(String message);

}

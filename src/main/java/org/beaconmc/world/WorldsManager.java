/**	
 * Beacon - Open Source Minecraft Server
 * Copyright (C) 2014  Marvin Seidl
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
 * @author Marvin Seidl
 */
 package org.beaconmc.world;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.beaconmc.BeaconServer;
import org.beaconmc.logging.DebugStream;

import com.google.common.collect.Lists;


public class WorldsManager {
	
	private HashMap<String,World> worlds;
	private HashMap<String,WorldHandler> handlers;
	private BeaconServer server;
	
	
	public WorldsManager(BeaconServer server){
		this.worlds = new HashMap<>();
		this.handlers = new HashMap<>();
		this.server = server;
	}	
	
	public void loadAllworlds(){
		DebugStream.handle("Loading all Worlds");
		for (File f : server.getWorldsDir().listFiles()){
			if (f.isDirectory() && new File(f, "level.dat").exists()) {
				DebugStream.handle("-" + f.getName());
				World w = new World(f.getName());
				worlds.put(w.getName(), w);
			}
		}
		
		startAllWorldHandlers();
	}
	
	private void startAllWorldHandlers(){
		DebugStream.handle("Starting all WorldHandlers");
		for(World w : worlds.values()){
			DebugStream.handle("-" + w.getName());
			WorldHandler wh = new WorldHandler(w);
			handlers.put(w.getName(), wh);
			wh.start();
		}
	}
	
	public World getWorld(String name){
		return worlds.get(name);
	}
	
	public WorldHandler getWorldHandler(String name){
		return handlers.get(name);
	}

	public List<World> getWorlds(){
		return Lists.newArrayList(this.worlds.values());
	}
	
	public List<World> getWorldHandlers(){
		return Lists.newArrayList(this.worlds.values());
	}
}

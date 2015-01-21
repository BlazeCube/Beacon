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

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.beaconmc.material.Material;

@RequiredArgsConstructor
public class Block {
	@Getter
	private final Chunk c;
	@Getter
	private final int x;
	@Getter
	private final int y;
	@Getter
	private final int z;
	
	public Block(World w,int x,int y,int z){
		this(w.getChunkAt(new ChunkLocation(w, x >>4,z>>4)),x,y,z);
	}
	
	public Material getType(){
		return c.getType(x, y, z);
	}
	
	public void setType(Material m){
		c.setType(x, y, z, m);
	}
	
	public int getBlockLight(){
		return c.getBlockLightAt(x, y, z);
	}
	
	public int getSkyLight(){
		return c.getSkyLightAt(x, y, z);
	}
}

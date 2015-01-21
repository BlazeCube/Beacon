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

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.beaconmc.logging.DebugStream;
import org.beaconmc.logging.ErrorStream;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.Tag;
import org.spout.nbt.TagType;
import org.spout.nbt.stream.NBTInputStream;

public class ChunkProvider {

	private static final int REGIONFILECACHE_MAXSIZE = 16;
	
	private World	w;
	private RegionFileCache	rfc;
	private Map<ChunkLocation,Chunk>loaded;

	public ChunkProvider(World w) {
		this.w = w;
		this.rfc = new RegionFileCache(REGIONFILECACHE_MAXSIZE);
		loaded = new HashMap<ChunkLocation,Chunk>();
	}
	
	public Chunk getChunk(ChunkLocation cl){
		if(loaded.containsKey(cl))
			return loaded.get(cl);
	
		DebugStream.handle("Loading new Chunk from Disc..");
		
		NBTInputStream nis = null;
		
		Chunk c = new Chunk(cl.getX(),cl.getZ());
		

		try{
			DataInputStream dis = rfc.getChunkDataInputStream(w.getWorldFile(), cl.getX(), cl.getZ());
			
			if(dis == null)
				return c;
			
			nis = new NBTInputStream(dis, false);
			
			
	
			Tag<?> t = nis.readTag();
	
			if (t == null) 
				return c;
			
			if (t.getType() != TagType.TAG_COMPOUND) 
				return c;
			
			
			CompoundTag ct = (CompoundTag) t;
			
			c.loadNBT(ct);
			
			
		} catch (IOException e) {
			ErrorStream.handle(e);
		} finally{
			try {
				if(nis != null)
					nis.close();
			} catch (Exception e1) {
				ErrorStream.handle(e1);
			}
		}
		
		if(c.isLoaded()){
			loaded.put(cl, c);
		}
		
		return c;
	}
}

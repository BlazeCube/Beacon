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
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public class RegionFileCache {

	private final Map<File, Reference<RegionFile>> cache = new HashMap<>();

	private final String extension	= "mca";
	
	private final int maxSize;

	public RegionFileCache(int maxSize) {
		this.maxSize = maxSize;
	}

	public RegionFile getRegionFile(File basePath, int chunkX, int chunkZ) throws IOException {
		File regionDir = new File(basePath, "region");
		File file = new File(regionDir, "r." + (chunkX >> 5) + "." + (chunkZ >> 5) + "." + extension);

		Reference<RegionFile> ref = cache.get(file);

		if (ref != null && ref.get() != null) {
			return ref.get();
		}

		if (!regionDir.isDirectory() && !regionDir.mkdirs()) {
			// TODO: Logger
		}

		if (cache.size() >= maxSize) {
			clear();
		}

		RegionFile reg = new RegionFile(file);
		cache.put(file, new SoftReference<>(reg));
		return reg;
	}

	public void clear() throws IOException {
		for (Reference<RegionFile> ref : cache.values()) {
			RegionFile value = ref.get();
			if (value != null) {
				value.close();
			}
		}
		cache.clear();
	}

	public int getSizeDelta(File basePath, int chunkX, int chunkZ) throws IOException {
		RegionFile r = getRegionFile(basePath, chunkX, chunkZ);
		return r.getSizeDelta();
	}

	public DataInputStream getChunkDataInputStream(File basePath, int chunkX, int chunkZ)
			throws IOException {
		RegionFile r = getRegionFile(basePath, chunkX, chunkZ);
		return r.getChunkDataInputStream(chunkX & 31, chunkZ & 31);
	}
	

	public DataOutputStream getChunkDataOutputStream(File basePath, int chunkX, int chunkZ)
			throws IOException {
		RegionFile r = getRegionFile(basePath, chunkX, chunkZ);
		return r.getChunkDataOutputStream(chunkX & 31, chunkZ & 31);
	}
}
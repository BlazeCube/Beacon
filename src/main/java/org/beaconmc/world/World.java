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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import lombok.Getter;

import org.beaconmc.BeaconServer;
import org.beaconmc.entity.EntityManager;
import org.beaconmc.entity.living.Player;
import org.beaconmc.logging.ErrorStream;
import org.beaconmc.network.socket.protocol.packets.PacketOut;
import org.beaconmc.utils.NBTUtils;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.stream.NBTInputStream;

public class World {

	private ChunkProvider	cp;

	@Getter
	private String	name;
	@Getter
	private EntityManager entityManager;
	@Getter
	private String leveltype;
	private int		spawnX;
	private int		spawnY;
	private int		spawnZ;

	public World(String name) {
		this.name = name;
		this.entityManager = new EntityManager(this);

		loadLevelDat();

		cp = new ChunkProvider(this);
	}

	public File getWorldFile() {
		return new File(BeaconServer.getServer().getWorldsDir(), name);
	}
	
	public Chunk getChunkAt(ChunkLocation cl){
		return cp.getChunk(cl);
	}
	
	public void heartBeat(){
		entityManager.heartBeat();
    }

	private void loadLevelDat() {
		File f = new File(getWorldFile(), "level.dat");
		NBTInputStream nis = null;
		CompoundMap dataMap = null;

		try {
			nis = new NBTInputStream(new FileInputStream(f));
			dataMap = ((CompoundTag) ((CompoundTag) nis.readTag()).getValue().get("Data")).getValue();
		} catch (IOException e) {
			ErrorStream.handle("World: " + name,"Error loading level.dat");
		} finally {
			try {
				if (nis != null)
					nis.close();
			} catch (IOException e) {
				ErrorStream.handle(e);
			}
		}
		
		spawnX = NBTUtils.getInt(dataMap, "SpawnX", 0);
		spawnY = NBTUtils.getInt(dataMap, "SpawnY", 0);
		spawnZ = NBTUtils.getInt(dataMap, "SpawnZ", 0);
		leveltype = NBTUtils.getString(dataMap, "generatorName", "default");
	}
	
	//Convenience Methods
	public Location getSpawnLocation(){
		return new Location(this, spawnX, spawnY, spawnZ, 0, 0);
	}
	
	public void sendPacket(PacketOut packetOut) {
		this.entityManager.sendPacket(packetOut);
	}
	
	public List<Player> getPlayers(){
		return this.entityManager.getPlayers();
	}
}

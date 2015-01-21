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

import java.util.HashMap;
import java.util.Map;

public enum Biome {
	
	OCEAN(0),
	PLAINS(1),
	DESERT(2),
	EXTREME_HILLS(3),
	FOREST(4),
	TAIGA(5),
	SWAMPLAND(6),
	RIVER(7),
	HELL(8),
	THE_END(9),
	FROZEN_OCEAN(10),
	FROZEN_RIVER(11),
	ICE_PLAINS(12),
	ICE_MOUNTAINS(13),
	MUSHROOM_ISLAND(14),
	MUSHROOM_ISLAND_SHORE(15),
	BEACH(16),
	DESERT_HILLS(17),
	FOREST_HILLS(18),
	TAIGA_HILLS(19),
	EXTREME_HILLS_EDGE(20),
	JUNGLE(21),
	JUNGLE_HILLS(22),
	JUNGLE_EDGE(23),
	DEEP_OCEAN(24),
	STONE_BEACH(25),
	COLD_BEACH(26),
	BIRCH_FOREST(27),
	BIRCH_FOREST_HILLS(28),
	ROOFED_FOREST(29),
	COLD_TAIGA(30),
	COLD_TAIGA_HILLS(31),
	MEGA_TAIGA(32),
	MEGA_TAIGA_HILLS(33),
	EXTREME_HILLS_PLUS(34),
	SAVANNA(35),
	SAVANNA_PLATEAU(36),
	MESA(37),
	MESA_PLATEAU_F(38),
	MESA_PLATEAU(39),
	SUNFLOWER_PLAINS(129),
	DESERT_M(130),
	EXTREME_HILLS_M(131),
	FLOWER_FOREST(132),
	TAIGA_M(133),
	SWAMPLAND_M(134),
	ICE_PLAINS_SPIKES(140),
	JUNGLE_M(149),
	JUNGLE_EDGE_M(151),
	BIRCH_FOREST_M(155),
	BIRCH_FOREST_HILLS_M(156),
	ROOFED_FOREST_M(157),
	COLD_TAIGA_M(158),
	MEGA_SPRUCE_TAIGA(160),
	MEGA_SPRUCE_TAIGA_HILLS(161),
	EXTREME_HILLS_PLUS_M(162),
	SAVANNA_M(163),
	SVANNA_PLATEAU_M(164),
	MESA_BRYCE(165),
	MESA_PLATEAU_F_M(166),
	MESA_PLATEAU_M(167);

	private final int	ID;
	
	Biome(int ID) {
		this.ID = ID;
	}

	public int getID() {
		return ID;
	}

	public static Biome getBiome(int id) {
		return ID_TO_BIOME.get(id);
	}

	// Cache ID at first Initialisation
	private static final Map<Integer, Biome>	ID_TO_BIOME	= new HashMap<Integer, Biome>();
	static {
		for (Biome biome : values()) {
			ID_TO_BIOME.put(biome.getID(), biome);
		}
	}
}
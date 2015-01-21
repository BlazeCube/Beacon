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

import java.util.List;

import lombok.Getter;

import org.beaconmc.logging.DebugStream;
import org.beaconmc.material.Material;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutChunkData;
import org.beaconmc.utils.NBTUtils;
import org.beaconmc.utils.NibbleArray;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.TagType;

public class Chunk implements NBTData {
	
	public static final int	WIDTH	= 16, LENGTH = 16, SECTION_HEIGHT = 16,SECTIONS = 16, HEIGHT = 256;
	public static final int BASE_AREA = WIDTH * LENGTH;
	
	private final int		x, z;


	private byte[]			biomes;
	private int[] 			heightMap;
	private ChunkSection[]	sections;

	@Getter
	private boolean			loaded;

	public Chunk(int x, int z) {
		this.x = x;
		this.z = z;
		
		
		biomes = new byte[BASE_AREA];
		heightMap = new int[BASE_AREA];
		sections = new ChunkSection[SECTIONS];
			
		loaded = false;
	}
	
	public Biome getBiome(int x, int z) {
		return Biome.getBiome(biomes[to2DArrayIndex(x,z)]);
	}

	public void setBiome(int x, int z, Biome b) {
		biomes[to2DArrayIndex(x,z)] = (byte) b.getID();
	}
	
	public int getHeightAt(int x,int z){
		return heightMap[to2DArrayIndex(x,z)];
	}
	
	public int getSkyLightAt(int x,int y,int z){
		ChunkSection cs = sections[y >> 4];
		
		return cs == null ? 0 : cs.getSkyLightAt(x, y, z);
	}
	
	public int getBlockLightAt(int x,int y,int z){
		ChunkSection cs = sections[y >> 4];
		
		return cs == null ? 0: cs.getBlockLightAt(x, y, z);
	}
	
	public Material getType(int x,int y,int z){
		ChunkSection cs = sections[y >> 4];
		
		return cs == null ? Material.AIR :  Material.byID(cs.getTypeAt(x, y, z));
	}
	
	public void setType(int x,int y,int z,Material m){
		ChunkSection cs = sections[y >> 4];		
		if(cs == null){
			//If Air, no need to create new ChunkSection
			if(m == Material.AIR)
				return;
			
			DebugStream.handle("Empty ChunkSection -> creating new one");
			cs = new ChunkSection();
		}
		
		//TODO: Light update
		//TODO: Block breaks
		//TODO: Tile entities and stuff
		
		cs.setTypeAt(x, y, z, (char) m.getLegacyId());
	}
	
	public int getBlockCount(){
		int count = 0;
		
		for(ChunkSection cs : sections)
			if(cs != null)
				count+= cs.getBlockCount();
		
		return count;
	}
	
	private int to2DArrayIndex(int x,int z){
		return ((z % 0xf) * WIDTH) + (x % 0xf);
	}
		
	public class ChunkSection{

		public static final int MAX_BLOCKS	= WIDTH * SECTION_HEIGHT * LENGTH;
		
		@Getter
		private final char[]		types;
		@Getter
		private final NibbleArray	skyLight;
		@Getter
		private final NibbleArray	blockLight;
		@Getter
		private int blockCount;
		
		public ChunkSection(){
			this(new char[MAX_BLOCKS],new NibbleArray(MAX_BLOCKS),new NibbleArray(MAX_BLOCKS));
		}
		
		public ChunkSection(char[] types, NibbleArray skyLight, NibbleArray blockLight) {
			this.types = types;
			this.skyLight = skyLight;
			this.blockLight = blockLight;
			countBlocks();
		}
		
		public char getTypeAt(int x,int y,int z){
			return types[toArrayIndex(x,y,z)];
		}
		
		public void setTypeAt(int x,int y,int z,char c){
			types[toArrayIndex(x,y,z)] = c;
		}
		
		public int getSkyLightAt(int x,int y,int z){
			return skyLight.getNibble(toArrayIndex(x,y,z));
		}
		
		public void setSkyLightAt(int x,int y,int z,int value){
			skyLight.setNibble(toArrayIndex(x,y,z), value & 0xf);
		}
		
		public int getBlockLightAt(int x,int y,int z){
			return blockLight.getNibble(toArrayIndex(x,y,z));
		}
		
		public void setBlockLightAt(int x,int y,int z,int value){
			blockLight.setNibble(toArrayIndex(x,y,z), value & 0xf);
		}
		
		public int toArrayIndex(int x,int y,int z){
			return ((y & 0xf) << 8) | ((z & 0xf) << 4) | (x & 0xf);
		}
		
		private void countBlocks(){
			blockCount = MAX_BLOCKS;
			for(char c : types)
				if(c >> 4 == 0)
					blockCount--;
		}
	}

	@Override
	public boolean loadNBT(CompoundTag tag) {
		if(!tag.getValue().containsKey("Level"))
			return false;
		
		if(tag.getValue().get("Level").getType() != TagType.TAG_COMPOUND)
			return false;
		
		CompoundMap levelMap = ((CompoundTag) tag.getValue().get("Level")).getValue();
		
		biomes = NBTUtils.getByteArray(levelMap, "Biomes", biomes);
				
		heightMap = NBTUtils.getIntArray(levelMap, "HeightMap", heightMap);		
		
		if(!levelMap.containsKey("Sections"))
			return false;
		
		if(levelMap.get("Sections").getType() != TagType.TAG_LIST)
			return false;
		
		@SuppressWarnings("unchecked")
		List<CompoundTag> sectionsList = (List<CompoundTag>) levelMap.get("Sections").getValue();
		
		for(CompoundTag sectionTag : sectionsList){
			CompoundMap sectionMap = sectionTag.getValue();
			
			int y = NBTUtils.getByte(sectionMap, "Y", (byte)0);
			
			byte[] blocks = NBTUtils.getByteArray(sectionMap, "Blocks", new byte[ChunkSection.MAX_BLOCKS]);
			NibbleArray data = new NibbleArray(NBTUtils.getByteArray(sectionMap, "Data", new byte[ChunkSection.MAX_BLOCKS / 2]));
			NibbleArray blockLight = new NibbleArray(NBTUtils.getByteArray(sectionMap, "BlockLight", new byte[ChunkSection.MAX_BLOCKS / 2]));
			NibbleArray skyLight = new NibbleArray(NBTUtils.getByteArray(sectionMap, "SkyLight", new byte[ChunkSection.MAX_BLOCKS / 2]));			
			
			char[] types = new char[ChunkSection.MAX_BLOCKS];
			
			for(int i = 0; i < ChunkSection.MAX_BLOCKS;i++){
				types[i] = (char)(((blocks[i] & 0xff) << 4) | data.getNibble(i));
			}
			
			sections[y] = new ChunkSection(types, skyLight, blockLight);
		}
		
		loaded = true;
		return true;
	}

	@Override
	public CompoundTag saveNBT() {
		// TODO save chunks?
		// Not actually needed on Blazecube
		return null;
	}
	
	public PacketPlayOutChunkData getDataPacket(boolean skyLight,boolean wholeChunk){
		
		if(!this.loaded){
			//TODO count blocks:
			
			return PacketPlayOutChunkData.getEmpty(this.x, this.z);
			
		}
		
		//Calculate DataSize
		int size = 0;
		
		for(ChunkSection cs : sections){
			if(cs != null || wholeChunk){
				size += ChunkSection.MAX_BLOCKS * 2; //Types -> 2 byte
				size += ChunkSection.MAX_BLOCKS / 2; //BlockLight -> Nibble
				if(skyLight)
					size += ChunkSection.MAX_BLOCKS / 2; 
			}
		}
		
		if(wholeChunk)
			size += BASE_AREA; //Biomes
		
		//Calculate DataSize end
		
			
		//Write Data Array
		byte[] data = new byte[size];
		
		int pos = 0;
		
		for(ChunkSection cs : sections){
			if(cs != null){
				for (char c : cs.getTypes()) {
                    data[pos++] = (byte) (c & 0xff);
                    data[pos++] = (byte) (c >> 8);
                }
			}
//			else if(wholeChunk){
//				byte[] emptyBlocks = new byte[ChunkSection.MAX_BLOCKS * 2];
//				System.arraycopy(emptyBlocks, 0, data, pos, emptyBlocks.length);
//                pos += emptyBlocks.length;
//			}
		}
		
		for(ChunkSection cs : sections){
			if(cs != null){
				byte[] blockLight = cs.getBlockLight().getByteArray();
				System.arraycopy(blockLight, 0, data, pos, blockLight.length);
                pos += blockLight.length;
			}
//			else if(wholeChunk){
//				NibbleArray blockLight = new NibbleArray(ChunkSection.MAX_BLOCKS);
//				blockLight.fill(0);
//				System.arraycopy(blockLight.getByteArray(), 0, data, pos, blockLight.getByteArray().length);
//                pos += blockLight.getByteArray().length;
//			}
		}
		
		if(skyLight){
			for(ChunkSection cs : sections){
				if(cs != null){
					
					byte[] skyLightArr = cs.getSkyLight().getByteArray();
					System.arraycopy(skyLightArr, 0, data, pos, skyLightArr.length);
	                pos += skyLightArr.length;
	              
				}
//				else if(wholeChunk){
//					NibbleArray skyLightArr = new NibbleArray(ChunkSection.MAX_BLOCKS);
//					skyLightArr.fill(15);
//					System.arraycopy(skyLightArr.getByteArray(), 0, data, pos, skyLightArr.getByteArray().length);
//	                pos += skyLightArr.getByteArray().length;
//				}
			}
		}
		
		if(wholeChunk){
			for(int i = 0 ; i < biomes.length;i++){
				data[pos++] = biomes[i];
			}
		}
		
		int bitMap = 0;
		
		for (int i = 0; i < sections.length; i++) {
			if (sections[i] != null) {
				bitMap |= (1 << i);
			}
		}
		
		return new PacketPlayOutChunkData(x, z, wholeChunk, bitMap, data);
	}
}

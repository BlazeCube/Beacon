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

import lombok.Data;

import org.beaconmc.utils.MathUtils;
import org.beaconmc.utils.Vector3i;

@Data
public class Location implements Cloneable {

	private World world;

	private double x;
	private double y;
	private double z;

	private float yaw;
	private float pitch;

	public Location(World world, double x, double y, double z, float yaw, float pitch){
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = (yaw % 360 + 360) % 360;
		this.pitch = pitch;
	}

	public Location(World world, double x, double y, double z){
		this(world, x, y, z, 0, 0);
	}
	
	public Location(World world,Vector3i v){
		this(world,v.getX(), v.getY(), v.getZ());
		
	}

	public Location(double x, double y, double z){
		this(null, x, y, z, 0, 0);
	}

	public Location(double x, double y, double z, float yaw, float pitch){
		this(null, x, y, z, yaw, pitch);
	}

	public Location add(Location other) {
		this.x += other.x;
		this.y += other.y;
		this.z += other.z;
		this.yaw = ((this.yaw + other.yaw)%360+360)%360;
		this.pitch += other.pitch;
		return this;
	}

	public Location add(Vector3d v) {
		this.x += v.getX();
		this.y += v.getY();
		this.z += v.getZ();
		return this;
	}

	public int getBlockX() {
		return (int) this.getX();
	}

	public int getBlockY() {
		return (int) this.getY();
	}

	public int getBlockZ() {
		return (int) this.getZ();
	}

	public ChunkLocation toChunkLocation() {
		return new ChunkLocation(this.getWorld(), this.getBlockX() >> 4, this.getBlockZ() >> 4);
	}
	
	public BlockLocation toBlockLocation(){
		return new BlockLocation(this.getWorld(), this.getBlockX(), this.getBlockY(), this.getBlockZ());
	}

	@Override
	public Location clone() {
		try {
			return (Location) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}

	public double distanceSquared(Location other) {
		return MathUtils.square(this.getX() - other.getX()) + MathUtils.square(this.getY() - other.getY())
				+ MathUtils.square(this.getZ() - other.getZ());
	}

	public double distance(Location other) {
		return Math.sqrt(this.distanceSquared(other));
	}

	public boolean hasMoved(Location other) {
		return this.distanceSquared(other) != 0.0;
	}

	public boolean hasRotated(Location other) {
		return this.getPitch() != other.getPitch() || this.getYaw() != other.getYaw();
	}
}

/**	
 * Beacon - Open Source Minecraft Server
 * Copyright (C) 2014  podpage
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
 * @author podpage
 */
 package org.beaconmc.world;

import org.beaconmc.utils.MathUtils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Vector3d implements Cloneable {

	private double x;
	private double y;
	private double z;

	public Vector3d clone() {
		return new Vector3d(x, y, z);
	}

	public Vector3d add(Vector3d vec) {
		this.x += vec.x;
		this.y += vec.y;
		this.z += vec.z;
		return this;
	}

	public Vector3d subtract(Vector3d vec) {
		this.x -= vec.x;
		this.y -= vec.y;
		this.z -= vec.z;
		return this;
	}

	public Vector3d multiply(Vector3d vec) {
		this.x *= vec.x;
		this.y *= vec.y;
		this.z *= vec.z;
		return this;
	}

	public Vector3d divide(Vector3d vec) {
		this.x /= vec.x;
		this.y /= vec.y;
		this.z /= vec.z;
		return this;
	}

	public Vector3d multiply(int m) {
		this.x *= m;
		this.y *= m;
		this.z *= m;
		return this;
	}

	public Vector3d multiply(double m) {
		this.x *= m;
		this.y *= m;
		this.z *= m;
		return this;
	}

	public double length() {
		return Math.sqrt(MathUtils.square(this.x) + MathUtils.square(this.y) + MathUtils.square(this.z));
	}

	public Vector3d setLength(double length) {
		normalize();
		multiply(length);
		return this;
	}

	public Vector3d normalize() {
		double length = length();
		this.x /= length;
		this.y /= length;
		this.z /= length;
		return this;
	}

	public Vector3d midpoint(Vector3d vec) {
		return bezier(vec, 0.5);
	}

	public Vector3d bezier(Vector3d vec, double percentage) {
		double x = (1 - percentage) * this.x + percentage * vec.x;
		double y = (1 - percentage) * this.y + percentage * vec.y;
		double z = (1 - percentage) * this.z + percentage * vec.z;
		return new Vector3d(x, y, z);
	}

	public Vector3d round(int position) {
		this.x = MathUtils.round(this.x, position);
		this.y = MathUtils.round(this.y, position);
		this.z = MathUtils.round(this.z, position);
		return this;
	}

	public Vector3d rotateX(double deg) {
		double angle = Math.toRadians(deg);
		double y = getY();
		setY(getY() * Math.cos(angle) - getZ() * Math.sin(angle));
		setZ(y * Math.sin(angle) + getZ() * Math.cos(angle));
		return this;
	}

	public Vector3d rotateY(double deg) {
		double angle = Math.toRadians(deg);
		double x = getX();
		setX(getX() * Math.cos(angle) - getZ() * Math.sin(angle));
		setZ(x * Math.sin(angle) + getZ() * Math.cos(angle));
		return this;
	}

	public Vector3d rotateZ(double deg) {
		double angle = Math.toRadians(deg);
		double x = getX();
		setX(getX() * Math.cos(angle) - getY() * Math.sin(angle));
		setY(x * Math.sin(angle) + getY() * Math.cos(angle));
		return this;
	}

	public Vector3d rotate(double degX, double degY, double degZ) {
		rotateX(degX);
		rotateY(degY);
		rotateZ(degZ);
		return this;
	}

	public Location toLocation(World world) {
		return toLocation(world, 0.0F, 0.0F);
	}

	public Location toLocation(World world, float yaw, float pitch) {
		return new Location(world, this.x, this.y, this.z, yaw, pitch);
	}

}

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
 package org.beaconmc.entity;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Validate;
import org.beaconmc.BeaconServer;
import org.beaconmc.entity.meta.EntityMeta;
import org.beaconmc.entity.meta.EntityMetaKey;
import org.beaconmc.network.socket.protocol.packets.PacketOut;
import org.beaconmc.network.socket.protocol.packets.play.out.*;
import org.beaconmc.world.Location;
import org.beaconmc.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Entity {

	@Getter
	protected final BeaconServer server;
	@Getter
	protected int entityId = -1;
	@Getter
	private UUID uuid;
	private Location location;
	private Location lastLocation;
	@Getter
	@Setter
	private boolean onGround = true;
	@Getter
	@Setter
	private int ticksLived;
	@Getter
	@Setter
	private int fireTicks;
	@Getter
	@Setter
	private int fallDistance;
	@Getter
	private boolean alive;
	@Getter
	private EntityMeta metadata;
	@Getter
	@Setter
	private int maxAirTicks = 3000;
	@Getter
	private int airTicks = maxAirTicks;
	private boolean teleported = false;

	protected Entity(BeaconServer server, Location location) {
		Validate.notNull(server, "Server cannot be null");
		Validate.notNull(location, "Location cannot be null");
		Validate.notNull(location.getWorld(), "World cannot be null");
		this.server = server;
		this.location = location.clone();
		this.lastLocation = location.clone();
		this.uuid = UUID.randomUUID();
		this.entityId = this.location.getWorld().getEntityManager().register(this);
		Validate.isTrue(this.entityId != -1, "Error while register entity. Returned entityId -1");
		this.metadata = new EntityMeta(this);
	}

	public void setAirTicks(int airTicks) {
		this.airTicks = airTicks;
		this.metadata.set(EntityMetaKey.AIR, airTicks);
	}

	public Location getLocation() {
		return this.location.clone();
	}

	public World getWorld() {
		return this.location.getWorld();
	}

	public void teleport(Location location){
		this.setLocation(location, false);
		this.teleported = true;
	}

	public void setLocation(Location location, boolean relative) {
		if (location.getWorld() == null)
			location.setWorld(this.location.getWorld());
		Validate.isTrue(this.location.getWorld().equals(location.getWorld()) || !relative,"Cannot set location relative over worlds");
		
		//this.lastLocation = this.location;
		//TODO: is this right?
		
		if (!this.location.getWorld().equals(location.getWorld()))
			this.setWorld(location.getWorld());

		if (!relative)
			this.location = location.clone();
		else
			this.location = this.location.clone().add(location);
	}

	private void setWorld(World world) {
		Validate.notNull(world, "World cannot be null");
		if (this.location.getWorld().equals(world))
			return;

		throw new IllegalStateException("World changing is not allowed yet");
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{uuid=" + this.uuid + "}";
	}

	public void heartBeat() {
		this.ticksLived++;
		if (this.fireTicks > 0)
			this.fireTicks--;
	}

	public boolean hasFuckingUpdatesForClientsToBroadcastOnTheWorldWithinBeacon() {// entity.hasChanges()
		return this.hasMoved() || this.hasRotated() || this.metadata.getChanges().size() > 0;
	}

	public abstract void spawn();

	private boolean hasMoved() {
		return this.location.hasMoved(this.lastLocation);
	}

	private boolean hasRotated() {
		return this.location.hasRotated(this.lastLocation);
	}

	public List<PacketOut> getUpdatePackets() {
		ArrayList<PacketOut> packetOuts = Lists.newArrayList();

		if (this.ticksLived > 5 && (this.teleported || this.hasMoved() || this.hasRotated())) {
			PacketPlayOutEntity move;
			if (this.teleported || (this.hasMoved() && this.location.distance(this.lastLocation) > 4))
				move = new PacketPlayOutEntityTeleport(this.entityId, this.location, this.onGround);
			else if (this.hasMoved() && !this.hasRotated()) {
				move = new PacketPlayOutEntityRelativeMove(this.entityId, this.location.getX()  - this.lastLocation.getX(), this.location.getY() - this.lastLocation.getY(), this.location.getZ() - this.lastLocation.getZ(), this.onGround);
			} else if (!this.hasMoved() && this.hasRotated()) {
				move = new PacketPlayOutEntityLook(this.entityId, this.location.getYaw(), this.location.getPitch(), this.onGround);
			} else {
				move = new PacketPlayOutEntityLookAndRelativeMove(this.entityId, new Location(null, this.location.getX() - this.lastLocation.getX(), this.location.getY() - this.lastLocation.getY(), this.location.getZ() - this.lastLocation.getZ(), this.location.getYaw(), this.location.getPitch()), this.onGround);
			}
			move.setSendToOwnClient(this.teleported);
			packetOuts.add(move);
			this.lastLocation = this.location.clone();
			this.teleported = false;
		}

		if (this.metadata.getChanges().size() > 0) {
			packetOuts.add(new PacketPlayOutEntityMetadata(this, true));
			this.metadata.resetChanges();
		}

		return packetOuts;
	}

}

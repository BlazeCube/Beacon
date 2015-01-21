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
 package org.beaconmc.network.socket.protocol.packets.play.out;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.beaconmc.BeaconServer;
import org.beaconmc.GameMode;
import org.beaconmc.entity.living.HumanEntity;
import org.beaconmc.network.socket.protocol.packets.PacketOut;
import org.beaconmc.network.socket.protocol.packets.PacketSerializer;
import org.beaconmc.network.socket.protocol.packets.play.PacketPlay;
import org.beaconmc.world.Dimension;

@NoArgsConstructor
@AllArgsConstructor
public class PacketPlayOutRespawn extends PacketPlay implements PacketOut {

	private Dimension dimension;
	private int difficulty = BeaconServer.getServer().getDifficulty();
	private GameMode gameMode;
	private String leveltype;

	public PacketPlayOutRespawn(HumanEntity humanEntity) {
		this.gameMode = humanEntity.getGameMode();
		this.dimension = Dimension.OVERWORLD;// TODO -> Get dimension of world -> can't find the field!;
		this.leveltype = humanEntity.getLocation().getWorld().getLeveltype();
	}

	@Override
	public void write(PacketSerializer packetSerializer, int protocolVersion) {
		packetSerializer.getByteBuf().writeInt(dimension.getDimension());
		packetSerializer.writeUnsignedByte((byte) this.difficulty);
		packetSerializer.writeUnsignedByte((byte) this.gameMode.getId());
		packetSerializer.writeString(this.leveltype);
	}

}

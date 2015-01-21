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
 package org.beaconmc.network.socket.protocol.packets.play.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.beaconmc.network.socket.protocol.packets.PacketIn;
import org.beaconmc.network.socket.protocol.packets.PacketSerializer;
import org.beaconmc.network.socket.protocol.packets.play.PacketPlay;

@AllArgsConstructor
@NoArgsConstructor
public class PacketPlayInClientSettings extends PacketPlay implements PacketIn {

	@Getter@Setter
	private String locale;
	@Getter@Setter
	private byte viewDistance;
	@Getter@Setter
	private byte chatFlags;
	@Getter@Setter
	private boolean chatColors;
	@Getter@Setter
	private short skinParts;

	@Override
	public void read(PacketSerializer packetSerializer, int protocolVersion) {
		this.locale = packetSerializer.readString();
		this.viewDistance = packetSerializer.readByte();
		this.chatFlags = packetSerializer.readByte();
		this.chatColors = packetSerializer.readBoolean();
		this.skinParts = packetSerializer.readUnsignedByte();
	}

}

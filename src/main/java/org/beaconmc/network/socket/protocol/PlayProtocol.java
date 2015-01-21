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
 package org.beaconmc.network.socket.protocol;

import org.beaconmc.network.socket.protocol.packets.play.in.PacketPlayInAnimation;
import org.beaconmc.network.socket.protocol.packets.play.in.PacketPlayInChatMessage;
import org.beaconmc.network.socket.protocol.packets.play.in.PacketPlayInClientSettings;
import org.beaconmc.network.socket.protocol.packets.play.in.PacketPlayInKeepAlive;
import org.beaconmc.network.socket.protocol.packets.play.in.PacketPlayInPlayer;
import org.beaconmc.network.socket.protocol.packets.play.in.PacketPlayInPlayerDigging;
import org.beaconmc.network.socket.protocol.packets.play.in.PacketPlayInPlayerLook;
import org.beaconmc.network.socket.protocol.packets.play.in.PacketPlayInPlayerPosition;
import org.beaconmc.network.socket.protocol.packets.play.in.PacketPlayInPlayerPositionAndLook;
import org.beaconmc.network.socket.protocol.packets.play.in.PacketPlayInPluginMessage;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutAnimation;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutChatMessage;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutChunkBulk;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutChunkData;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutDisconnect;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutEntityEquipment;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutEntityLook;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutEntityLookAndRelativeMove;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutEntityMetadata;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutEntityRelativeMove;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutEntityTeleport;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutJoinGame;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutKeepAlive;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutPlayerPositionAndLook;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutPluginMessage;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutRespawn;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutSpawnPosition;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutTimeUpdate;

public class PlayProtocol extends Protocol {

	public PlayProtocol() {
		outbound(0x00, PacketPlayOutKeepAlive.class);
		outbound(0x01, PacketPlayOutJoinGame.class);
		outbound(0x02, PacketPlayOutChatMessage.class);
		outbound(0x03, PacketPlayOutTimeUpdate.class);
		outbound(0x04, PacketPlayOutEntityEquipment.class);
		outbound(0x05, PacketPlayOutSpawnPosition.class);
		outbound(0x07, PacketPlayOutRespawn.class);
		outbound(0x08, PacketPlayOutPlayerPositionAndLook.class);
		
		outbound(0x15, PacketPlayOutEntityRelativeMove.class);
		outbound(0x16, PacketPlayOutEntityLook.class);
		outbound(0x17, PacketPlayOutEntityLookAndRelativeMove.class);
		outbound(0x18, PacketPlayOutEntityTeleport.class);
		outbound(0x0B, PacketPlayOutAnimation.class);
		outbound(0x1C, PacketPlayOutEntityMetadata.class);

		outbound(0x21, PacketPlayOutChunkData.class);
		outbound(0x26, PacketPlayOutChunkBulk.class);
		
		outbound(0x3F, PacketPlayOutPluginMessage.class);
		outbound(0x40, PacketPlayOutDisconnect.class);
		

		inbound(0x00, PacketPlayInKeepAlive.class);
		inbound(0x01, PacketPlayInChatMessage.class);
		inbound(0x03, PacketPlayInPlayer.class);
		inbound(0x04, PacketPlayInPlayerPosition.class);
		inbound(0x05, PacketPlayInPlayerLook.class);
		inbound(0x06, PacketPlayInPlayerPositionAndLook.class);
		inbound(0x07, PacketPlayInPlayerDigging.class);
		inbound(0x0A, PacketPlayInAnimation.class);
		inbound(0x15, PacketPlayInClientSettings.class);
		inbound(0x17, PacketPlayInPluginMessage.class);
	}
}
